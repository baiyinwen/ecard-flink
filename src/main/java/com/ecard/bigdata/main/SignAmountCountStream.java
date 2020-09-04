package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.SignAmount;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.SignAmountCountSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.SignAmountCountWatermark;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Description 签发量统计
 * @Author WangXueDong
 * @Date 2020/4/10 9:24
 * @Version 1.0
 **/
public class SignAmountCountStream {

    private static Logger logger = LoggerFactory.getLogger(SignAmountCountStream.class);
    private static final String ClassName = SignAmountCountStream.class.getSimpleName();

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/4/10 9:24
     **/
    public static void main(String[] args) throws Exception {

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.SIGN_COUNT_KAFKA_TOPIC);
        final String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        WindowedStream<SignAmount, Tuple2<String, String>, TimeWindow> timeWindowRes = data.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String event = jsonLogInfo.getEvent();
                String inputStr = jsonLogInfo.getInput().toString();
                String outputStr = jsonLogInfo.getOutput().toString();
                if (!JsonUtils.isJsonObject(inputStr) || !JsonUtils.isJsonObject(outputStr)) {
                    return false;
                }
                JSONObject outputJson = JSON.parseObject(outputStr);
                if (CONSTANTS.EVENT_ESSC_LOG_SIGN.equals(event)
                        && CONSTANTS.EVENT_MSG_CODE_VALUE.equals(outputJson.getString(CONSTANTS.EVENT_MSG_CODE_KEY))) {
                    String md5Log = EncodeUtils.md5Encode(jsonLogInfo.getOrigLog());
                    boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.SIGN_REDIS_LOG_COUNT_MD5_KEY + md5Log);
                    if (isMember) {
                        return false;
                    } else {
                        RedisClusterUtils.setValue(CONSTANTS.SIGN_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_COUNT_MD5_KEY);
                        RedisClusterUtils.setExpire(CONSTANTS.SIGN_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS);
                    }
                    return true;
                }
            }
            return false;
        }).map((MapFunction<JsonLogInfo, SignAmount>) jsonLogInfo -> {
            SignAmount signAmount = new SignAmount();
            JSONObject inputObj = JSONObject.parseObject(jsonLogInfo.getInput().toString());
            String cardRegionCode = inputObj.getString(CONSTANTS.EVENT_ESSC_LOG_SIGN_AAB_301);
            if (null == cardRegionCode || cardRegionCode.trim().isEmpty()) {
                cardRegionCode = inputObj.getString(CONSTANTS.EVENT_ESSC_LOG_SIGN_SIGN_SEQ);
                if (null != cardRegionCode && !cardRegionCode.trim().isEmpty()) {
                    cardRegionCode = cardRegionCode.substring(cardRegionCode.length()-6);
                }
            }
            signAmount.setCollectTime(DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            signAmount.setChannelNo(jsonLogInfo.getChannelNo());
            signAmount.setCardRegionCode(cardRegionCode);
            signAmount.setTransferTimes(CONSTANTS.NUMBER_1);
            return signAmount;
        }).returns(TypeInformation.of(new TypeHint<SignAmount>() {})).assignTimestampsAndWatermarks(new SignAmountCountWatermark()).setParallelism(1)
          .keyBy(new KeySelector<SignAmount, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> getKey(SignAmount signAmount) {
                Tuple2<String, String> tuple2 = new Tuple2<>();
                tuple2.f0 = signAmount.getChannelNo();
                tuple2.f1 = signAmount.getCardRegionCode();
                return tuple2;
            }
        }).timeWindow(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_COUNT_TUMBLING_WINDOW_SIZE)))
          .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_COUNT_MAX_ALLOWED_LATENESS)));

        DataStream<SignAmount> reduceRes = timeWindowRes
        .reduce((ReduceFunction<SignAmount>) (s1, s2) -> {
            s1.setTransferTimes(s1.getTransferTimes() + s2.getTransferTimes());
            return s1;
        });

        reduceRes.addSink(new SignAmountCountSink()).name(SignAmountCountSink.class.getSimpleName());

        env.execute(ClassName);

    }

}

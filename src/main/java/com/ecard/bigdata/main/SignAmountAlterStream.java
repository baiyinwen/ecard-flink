package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.SignAmount;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.SignAmountAlterSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.SignAmountAlterWatermark;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @Description 签发量报警
 * @Author WangXueDong
 * @Date 2020/4/10 9:24
 * @Version 1.0
 **/
public class SignAmountAlterStream {

    private static Logger logger = LoggerFactory.getLogger(SignAmountAlterStream.class);
    private static final String ClassName = SignAmountAlterStream.class.getSimpleName();
    private static long STREAM_CHECKPOINT_INTERVAL = 60000;
    private static CheckpointingMode STREAM_CHECKPOINT_MODE = CheckpointingMode.EXACTLY_ONCE;

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
        String topic  = parameterTool.get(CONFIGS.SIGN_ALTER_KAFKA_TOPIC);
        final String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL, STREAM_CHECKPOINT_MODE);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        consumer.setStartFromLatest();
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        SingleOutputStreamOperator<JsonLogInfo> filterRes = data.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String event = jsonLogInfo.getEvent();
                if (CONSTANTS.EVENT_ESSC_LOG_SIGN.equals(event) || CONSTANTS.EVENT_ESSC_LOG_SIGN_ONE_STEP.equals(event) || CONSTANTS.EVENT_ESSC_LOG2_SIGN_PERSON.equals(event) || CONSTANTS.EVENT_ESSC_LOG2_SIGN_ONE_STEP.equals(event)) {
                    String outputStr = jsonLogInfo.getOutput().toString();
                    JSONObject outputJson;
                    try {
                        if (jsonLogInfo.getOutput() instanceof Map) {
                            outputStr = JSONObject.toJSONString(jsonLogInfo.getOutput());
                        }
                        outputJson = JSONObject.parseObject(outputStr);
                    } catch (Exception e) {
                        logger.error(jsonLogInfo.toString() + " --- 日志解析异常" + e.getMessage());
                        e.printStackTrace();
                        return  false;
                    }
                    if (outputJson != null) {
                        if (CONSTANTS.EVENT_MSG_CODE_VALUE.equals(outputJson.getString(CONSTANTS.EVENT_MSG_CODE_KEY))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });

        SingleOutputStreamOperator<JsonLogInfo> distinctRes = filterRes.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String md5Log = EncodeUtils.md5Encode(jsonLogInfo.getOrigLog());
                boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.SIGN_REDIS_LOG_ALTER_MD5_KEY + md5Log);
                if (isMember) {
                    return false;
                } else {
                    RedisClusterUtils.setValue(CONSTANTS.SIGN_REDIS_LOG_ALTER_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_ALTER_MD5_KEY);
                    RedisClusterUtils.setExpire(CONSTANTS.SIGN_REDIS_LOG_ALTER_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_ALTER_KEY_EXPIRE_SECONDS);
                }
                return true;
            }
            return false;
        }).setParallelism(1);

        DataStream<SignAmount> mapRes = distinctRes.map((MapFunction<JsonLogInfo, SignAmount>) jsonLogInfo -> {
            SignAmount signAmount = new SignAmount();
            signAmount.setCollectTime(DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            signAmount.setTransferTimes(CONSTANTS.NUMBER_1);
            return signAmount;
        }).returns(TypeInformation.of(new TypeHint<SignAmount>() {})).setParallelism(1);

        DataStream<SignAmount> reduceRes = mapRes.assignTimestampsAndWatermarks(new SignAmountAlterWatermark())
                .timeWindowAll(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_ALTER_TUMBLING_WINDOW_SIZE)))
                //.allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_ALTER_MAX_ALLOWED_LATENESS)))
                .reduce((ReduceFunction<SignAmount>) (s1, s2) -> {
                    s1.setTransferTimes(s1.getTransferTimes() + s2.getTransferTimes());
                    return s1;
                }).returns(TypeInformation.of(new TypeHint<SignAmount>() {}));

        reduceRes.addSink(new SignAmountAlterSink()).name(SignAmountAlterSink.class.getSimpleName());

        env.execute(ClassName);

    }

}

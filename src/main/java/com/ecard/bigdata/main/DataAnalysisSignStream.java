package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.DataAnalysisSignAmount;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.DataAnalysisSignSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.DataAnalysisSignWatermark;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
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
public class DataAnalysisSignStream {

    private static Logger logger = LoggerFactory.getLogger(DataAnalysisSignStream.class);

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/4/10 9:24
     **/
    public static void main(String[] args) throws Exception {

        final String ClassName = DataAnalysisSignStream.class.getSimpleName();
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, CONFIGS.SIGN_AMOUNT_KAFKA_TOPIC, ClassName);
        String topic  = parameterTool.get(CONFIGS.SIGN_AMOUNT_KAFKA_TOPIC);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        int reParallelism = (int) Math.ceil(parameterTool.getDouble(CONFIGS.STREAM_PARALLELISM)/2.0);

        WindowedStream<DataAnalysisSignAmount, Tuple2<String, String>, TimeWindow> timeWindowRes = data.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String event = jsonLogInfo.getEvent();
                JSONObject outputJson = JSON.parseObject(jsonLogInfo.getOutput().toString());
                if (CONSTANTS.EVENT_ESSC_LOG_SIGN.equals(event)
                        && CONSTANTS.EVENT_MSG_CODE_VALUE.equals(outputJson.getString(CONSTANTS.EVENT_MSG_CODE_KEY))) {
                    String md5Log = Md5Utils.encodeMd5(jsonLogInfo.getOrigLog());
                    boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.SIGN_REDIS_LOG_MIN_MD5_KEY + md5Log);
                    if (isMember) {
                        return false;
                    } else {
                        RedisClusterUtils.setValue(CONSTANTS.SIGN_REDIS_LOG_MIN_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_MIN_MD5_KEY);
                        RedisClusterUtils.setExpire(CONSTANTS.SIGN_REDIS_LOG_MIN_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_MIN_KEY_EXPIRE_SECONDS);
                    }
                    return true;
                }
            }
            return false;
        }).map((MapFunction<JsonLogInfo, DataAnalysisSignAmount>) jsonLogInfo -> {
            DataAnalysisSignAmount dataAnalysisSignAmount = new DataAnalysisSignAmount();
            JSONObject inputObj = JSONObject.parseObject(jsonLogInfo.getInput().toString());
            dataAnalysisSignAmount.setCollectTime(DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            dataAnalysisSignAmount.setChannelNo(jsonLogInfo.getChannelNo());
            dataAnalysisSignAmount.setCardRegionCode(inputObj.getString(CONSTANTS.EVENT_ESSC_LOG_SIGN_CARD_REGION_KEY));
            dataAnalysisSignAmount.setTransferTimes(CONSTANTS.NUMBER_1);
            return dataAnalysisSignAmount;
        }).assignTimestampsAndWatermarks(new DataAnalysisSignWatermark()).setParallelism(reParallelism)
          .keyBy(new KeySelector<DataAnalysisSignAmount, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> getKey(DataAnalysisSignAmount dataAnalysisSignMin) {
                Tuple2<String, String> tuple2 = new Tuple2<>();
                tuple2.f0 = dataAnalysisSignMin.getChannelNo();
                tuple2.f1 = dataAnalysisSignMin.getCardRegionCode();
                return tuple2;
            }
        }).timeWindow(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_AMOUNT_TUMBLING_WINDOW_SIZE)))
          .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_AMOUNT_MAX_ALLOWED_LATENESS)));

        DataStream<DataAnalysisSignAmount> reduceRes = timeWindowRes
        .reduce((ReduceFunction<DataAnalysisSignAmount>) (d1, d2) -> {
            d1.setTransferTimes(d1.getTransferTimes() + d2.getTransferTimes());
            return d1;
        });

        reduceRes.addSink(new DataAnalysisSignSink());

        env.execute(ClassName);

    }

}

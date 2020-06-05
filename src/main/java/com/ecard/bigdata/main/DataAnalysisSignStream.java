package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.DataAnalysisSignMin;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.DataAnalysisSignSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.DataAnalysisSignWatermark;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Description
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

        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, CONFIGS.SIGN_KAFKA_TOPIC);
        String topic  = parameterTool.get(CONFIGS.SIGN_KAFKA_TOPIC);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        consumer.setStartFromLatest();
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        int reParallelism = (int) Math.ceil(parameterTool.getDouble(CONFIGS.STREAM_PARALLELISM)/2.0);

        DataStream<DataAnalysisSignMin> mapRes = data.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String event = jsonLogInfo.getEvent();
                JSONObject outputJson = JSON.parseObject(jsonLogInfo.getOutput().toString());
                if (CONSTANTS.EVENT_ESSC_LOG_SIGN.equals(event)
                        && CONSTANTS.EVENT_MSG_CODE_VALUE.equals(outputJson.getString(CONSTANTS.EVENT_MSG_CODE_KEY))) {
                    String md5Log = Md5Utils.encodeMd5(jsonLogInfo.getOrigLog());
                    boolean isMember = RedisUtils.isExistsKey(CONSTANTS.SIGN_REDIS_LOG_MD5_KEY + md5Log);
                    if (isMember) {
                        return false;
                    } else {
                        RedisUtils.setValue(CONSTANTS.SIGN_REDIS_LOG_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_MD5_KEY);
                        RedisUtils.setExpire(CONSTANTS.SIGN_REDIS_LOG_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_KEY_EXPIRE_SECONDS);
                    }
                    return true;
                }
            }
            return false;
        }).map((MapFunction<JsonLogInfo, DataAnalysisSignMin>) jsonLogInfo -> {
            DataAnalysisSignMin dataAnalysisSignMin = new DataAnalysisSignMin();
            dataAnalysisSignMin.setCollectTime(DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            dataAnalysisSignMin.setTransferTimes(CONSTANTS.NUMBER_1);
            return dataAnalysisSignMin;
        }).returns(TypeInformation.of(new TypeHint<DataAnalysisSignMin>() {})).setParallelism(reParallelism);

        DataStream<DataAnalysisSignMin> reduceRes = mapRes.assignTimestampsAndWatermarks(new DataAnalysisSignWatermark())
                .timeWindowAll(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_TUMBLING_WINDOW_SIZE)))
                .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.SIGN_MAX_ALLOWED_LATENESS)))
                .reduce((ReduceFunction<DataAnalysisSignMin>) (d1, d2) -> {
                    d1.setTransferTimes(d1.getTransferTimes() + d2.getTransferTimes());
                    return d1;
                }).returns(TypeInformation.of(new TypeHint<DataAnalysisSignMin>() {}));

        reduceRes.addSink(new DataAnalysisSignSink());

        env.execute("DataAnalysisSignStream");

    }

}

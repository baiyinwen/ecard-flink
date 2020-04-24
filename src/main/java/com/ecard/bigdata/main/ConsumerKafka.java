package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.DataAnalysisSignMin;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.JsonLog;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.JsonLogSink;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.DateTimeUtils;
import com.ecard.bigdata.utils.ExecutionEnvUtils;
import com.ecard.bigdata.utils.KafkaConfigUtils;
import com.ecard.bigdata.waterMarkers.KafkaWatermark;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 9:24
 * @Version 1.0
 **/
public class ConsumerKafka {

    private static Logger logger = LoggerFactory.getLogger(ConsumerKafka.class);

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/4/10 9:24
     **/
    public static void main(String[] args) throws Exception {

        final ParameterTool parameterTool = ConfigUtils.createParameterTool(args);
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool);
        List<String> topics = Arrays.asList(parameterTool.get(CONFIGS.KAFKA_TOPIC));

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);

        JsonLogSchema kafkaRecordSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLog> consumer = new FlinkKafkaConsumer010<>(topics, kafkaRecordSchema, props);
        //consumer.setStartFromLatest();//设置从最新位置开始消费
        DataStreamSource<JsonLog> data = env.addSource(consumer);

        DataStream<DataAnalysisSignMin> mapRes = data.map((MapFunction<JsonLog, DataAnalysisSignMin>) jsonLog -> {
            DataAnalysisSignMin dataAnalysisSignMin = new DataAnalysisSignMin();
            String event = jsonLog.getEvent();
            JSONObject outputJson = JSON.parseObject(jsonLog.getOutput().toString());
            dataAnalysisSignMin.setCollectTime(DateTimeUtils.toTimestamp(jsonLog.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            if ("essc_log_sign".equals(event) && "000000".equals(outputJson.getString("msgCode"))) {
                dataAnalysisSignMin.setTransferTimes(1);
            } else {
                dataAnalysisSignMin.setTransferTimes(0);
            }
            return dataAnalysisSignMin;
        });

        DataStream<DataAnalysisSignMin> reduceRes = mapRes.assignTimestampsAndWatermarks(new KafkaWatermark())
            .timeWindowAll(Time.seconds(60), Time.seconds(60))
            .reduce((ReduceFunction<DataAnalysisSignMin>) (d1, d2) -> {
                DataAnalysisSignMin dataAnalysisSignMin = new DataAnalysisSignMin();
                dataAnalysisSignMin.setCollectTime(d1.getCollectTime());
                dataAnalysisSignMin.setTransferTimes(d1.getTransferTimes() + d2.getTransferTimes());
                return dataAnalysisSignMin;
            });

        reduceRes.addSink(new JsonLogSink());

        env.execute(parameterTool.get(CONFIGS.JOB_NAME));

    }

}

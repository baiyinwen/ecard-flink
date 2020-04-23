package com.ecard.bigdata.main;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.JsonLogInfo;
import com.ecard.bigdata.schemas.KafkaRecordSchema;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.ExecutionEnvUtils;
import com.ecard.bigdata.utils.KafkaConfigUtils;
import com.ecard.bigdata.waterMarkers.KafkaWatermark;
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

        KafkaRecordSchema kafkaRecordSchema = new KafkaRecordSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topics, kafkaRecordSchema, props);
        //consumer.setStartFromLatest();//设置从最新位置开始消费
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        DataStream<JsonLogInfo> water = data.assignTimestampsAndWatermarks(new KafkaWatermark())
                .timeWindowAll(Time.seconds(10))
                .reduce(new ReduceFunction<JsonLogInfo>() {
                    @Override
                    public JsonLogInfo reduce(JsonLogInfo j1, JsonLogInfo j2) throws Exception {

                        return null;
                    }
                });

        /*DataStream<Tuple1<String>> res = data.map((MapFunction<JsonLogInfo, Tuple1<String>>) jsonLogInfo -> {
            logger.info(jsonLogInfo.toString());
            Tuple1<String> output = new Tuple1<>();
            output.f0 = jsonLogInfo.toString();
            return output;
        }).returns(TypeInformation.of(new TypeHint<Tuple1<String>>() {}));*/

        env.execute(parameterTool.get(CONFIGS.JOB_NAME));

    }

}

package com.ecard.bigdata.main;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.CreditScore;
import com.ecard.bigdata.schemas.CreditScoreSchema;
import com.ecard.bigdata.sink.CreditScoreMoveSink;
import com.ecard.bigdata.utils.ExecutionEnvUtils;
import com.ecard.bigdata.utils.KafkaConfigUtils;
import com.ecard.bigdata.utils.ParameterUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/7/31 18:09
 * @Version 1.0
 **/
public class CreditScoreMoveStream {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreMoveStream.class);
    private static final String ClassName = CreditScoreMoveStream.class.getSimpleName();

    public static void main(String[] args) throws Exception {

        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.CREDIT_SCORE_KAFKA_TOPIC);
        String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        CreditScoreSchema creditScoreSchema = new CreditScoreSchema();
        FlinkKafkaConsumer010<CreditScore> consumer = new FlinkKafkaConsumer010<>(topic, creditScoreSchema, props);
        DataStreamSource<CreditScore> data = env.addSource(consumer);

        DataStream<List<CreditScore>> dataWindow = data.timeWindowAll(Time.seconds(parameterTool.getLong(CONFIGS.CREDIT_SCORE_TUMBLING_WINDOW_SIZE))).apply(new AllWindowFunction<CreditScore, List<CreditScore>, TimeWindow>() {
            @Override
            public void apply(TimeWindow timeWindow, Iterable<CreditScore> iterable, Collector<List<CreditScore>> collector) {
                List<CreditScore> list = new ArrayList<>();
                Iterator<CreditScore> iterator = iterable.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
                collector.collect(list);
            }
        });

        dataWindow.addSink(new CreditScoreMoveSink()).name(CreditScoreMoveSink.class.getSimpleName()).setParallelism(20);

        env.execute(ClassName);
    }

}

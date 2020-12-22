package com.ecard.bigdata.main;

import com.ecard.bigdata.bean.RiskControlPayLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.schemas.RiskControlPayInSchema;
import com.ecard.bigdata.schemas.RiskControlPayOutSchema;
import com.ecard.bigdata.sink.RiskControlPaySaveSink;
import com.ecard.bigdata.utils.ExecutionEnvUtils;
import com.ecard.bigdata.utils.KafkaConfigUtils;
import com.ecard.bigdata.utils.ParameterUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Description 风控支付日志落hbase并推送结果到rocketMq
 * @Author WangXueDong
 * @Date 2020/11/16 11:14
 * @Version 1.0
 **/
public class RiskControlPayStream {

    private static Logger logger = LoggerFactory.getLogger(RiskControlPayStream.class);
    private static final String ClassName = RiskControlPayStream.class.getSimpleName();
    private static long STREAM_CHECKPOINT_INTERVAL = 60000;
    private static CheckpointingMode STREAM_CHECKPOINT_MODE = CheckpointingMode.EXACTLY_ONCE;

    public static void main(String[] args){

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.RISK_CONTROL_PAY_KAFKA_CONSUMER_TOPIC);
        String groupId = parameterTool.get(CONFIGS.RISK_CONTROL_PAY_KAFKA_CONSUMER_GROUP);
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, groupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL, STREAM_CHECKPOINT_MODE);

        RiskControlPayInSchema riskControlPayInSchema = new RiskControlPayInSchema();
        FlinkKafkaConsumer010<RiskControlPayLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, riskControlPayInSchema, props);
        DataStreamSource<RiskControlPayLogInfo> data = env.addSource(consumer);

        //保存到hbase
        data.addSink(new RiskControlPaySaveSink()).name(RiskControlPaySaveSink.class.getSimpleName());
        //推送到kafka
        String pushTopic = parameterTool.get(CONFIGS.RISK_CONTROL_PAY_KAFKA_PRODUCER_TOPIC);
        RiskControlPayOutSchema riskControlPayOutSchema = new RiskControlPayOutSchema();
        data.addSink(new FlinkKafkaProducer010<>(pushTopic, riskControlPayOutSchema, props));

        try {
            env.execute(ClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

package com.ecard.bigdata.main;

import com.ecard.bigdata.bean.RiskControlPayLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.externals.rocketMq.RocketMQConfig;
import com.ecard.bigdata.externals.rocketMq.RocketMQSink;
import com.ecard.bigdata.externals.rocketMq.RocketMQSource;
import com.ecard.bigdata.externals.rocketMq.common.selector.DefaultTopicSelector;
import com.ecard.bigdata.schemas.RiskControlPayInSchema;
import com.ecard.bigdata.schemas.RiskControlPayOutSchema;
import com.ecard.bigdata.sink.RiskControlPaySaveSink;
import com.ecard.bigdata.utils.ExecutionEnvUtils;
import com.ecard.bigdata.utils.ParameterUtils;
import com.ecard.bigdata.utils.RocketMQConfigUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
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
        String topic  = parameterTool.get(CONFIGS.RISK_CONTROL_PAY_ROCKET_MQ_CONSUMER_TOPIC);
        String groupId = parameterTool.get(CONFIGS.RISK_CONTROL_PAY_ROCKET_MQ_CONSUMER_GROUP);

        Properties props = RocketMQConfigUtils.createRocketMQProps(parameterTool, groupId);
        props.setProperty(RocketMQConfig.CONSUMER_TOPIC, topic);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL);

        RiskControlPayInSchema riskControlPayInSchema = new RiskControlPayInSchema();
        RocketMQSource<RiskControlPayLogInfo> consumer = new RocketMQSource(riskControlPayInSchema, props);
        DataStreamSource<RiskControlPayLogInfo> data = env.addSource(consumer);

        //保存到hbase
        data.addSink(new RiskControlPaySaveSink()).name(RiskControlPaySaveSink.class.getSimpleName());
        //推送到rocketMq
        String pushTopic = parameterTool.get(CONFIGS.RISK_CONTROL_PAY_ROCKET_MQ_PRODUCER_TOPIC);
        String producerGroupId = parameterTool.get(CONFIGS.RISK_CONTROL_PAY_ROCKET_MQ_PRODUCER_GROUP);
        int msgDelayLevel = RocketMQConfig.MSG_DELAY_LEVEL04;
        boolean batchFlag = msgDelayLevel <= 0;
        props.setProperty(RocketMQConfig.MSG_DELAY_LEVEL, String.valueOf(msgDelayLevel));
        props.setProperty(RocketMQConfig.PRODUCER_GROUP, producerGroupId);
        RiskControlPayOutSchema riskControlPayOutSchema = new RiskControlPayOutSchema();
        DefaultTopicSelector defaultTopicSelector = new DefaultTopicSelector(pushTopic);
        data.addSink(new RocketMQSink(riskControlPayOutSchema, defaultTopicSelector, props).withBatchFlushOnCheckpoint(batchFlag));

        try {
            env.execute(ClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

package com.ecard.bigdata.utils;

import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.schemas.JsonLogSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 10:31
 * @Version 1.0
 **/
public class KafkaConfigUtils {

    private static Logger logger = LoggerFactory.getLogger(KafkaConfigUtils.class);

    /**
     * 设置 kafka 配置
     *
     * @param parameterTool
     * @return
     */
    public static Properties createKafkaProps(ParameterTool parameterTool, String kafkaTopic, String KafkaGroup) {

        Properties props = parameterTool.getProperties();

        props.put("zookeeper.connect", parameterTool.get(CONFIGS.ZOOKEEPER_SERVERS));
        props.put("bootstrap.servers", parameterTool.get(CONFIGS.KAFKA_BROKERS));

        props.put("group.id", parameterTool.get(kafkaTopic) + "_" + KafkaGroup);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        if (parameterTool.getBoolean(CONFIGS.KAFKA_SASL_ENABLE)) {
            props.put("security.protocol", "SASL_TBDS");
            props.put("sasl.mechanism", "TBDS");
            props.put("sasl.tbds.secure.id", parameterTool.get(CONFIGS.SASL_TBDS_SECURE_ID));
            props.put("sasl.tbds.secure.key", parameterTool.get(CONFIGS.SASL_TBDS_SECURE_KEY));
        }

        return props;
    }

    public static DataStreamSource<JsonLogInfo> createSource(StreamExecutionEnvironment env, String kafkaTopic, String kafkaGroup) {
        ParameterTool parameter = (ParameterTool) env.getConfig().getGlobalJobParameters();
        String topic = parameter.getRequired(kafkaTopic);
        String group = parameter.getRequired(kafkaGroup);
        Long time = parameter.getLong(CONFIGS.CONSUMER_FROM_TIME, 0L);
        return createSource(env, topic, group, time);
    }

    /**
     * @param env
     * @param kafkaTopic
     * @param time  订阅的时间
     * @return
     * @throws IllegalAccessException
     */
    public static DataStreamSource<JsonLogInfo> createSource(StreamExecutionEnvironment env, String kafkaTopic, String kafkaGroup, Long time) {
        ParameterTool parameterTool = (ParameterTool) env.getConfig().getGlobalJobParameters();
        Properties props = createKafkaProps(parameterTool, kafkaTopic, kafkaGroup);
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(kafkaTopic, new JsonLogSchema(), props);
        //重置offset到time时刻
        if (time != 0L) {
            Map<KafkaTopicPartition, Long> partitionOffset = createOffsetByTime(props, parameterTool, kafkaTopic, time);
            consumer.setStartFromSpecificOffsets(partitionOffset);
        }
        return env.addSource(consumer);
    }

    private static Map<KafkaTopicPartition, Long> createOffsetByTime(Properties props, ParameterTool parameterTool, String kafkaTopic, Long time) {
        props.setProperty("group.id", "query_time_" + time);
        KafkaConsumer consumer = new KafkaConsumer(props);
        List<PartitionInfo> partitionsFor = consumer.partitionsFor(parameterTool.getRequired(kafkaTopic));
        Map<TopicPartition, Long> partitionInfoLongMap = new HashMap<>();
        for (PartitionInfo partitionInfo : partitionsFor) {
            partitionInfoLongMap.put(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()), time);
        }
        Map<TopicPartition, OffsetAndTimestamp> offsetResult = consumer.offsetsForTimes(partitionInfoLongMap);
        Map<KafkaTopicPartition, Long> partitionOffset = new HashMap<KafkaTopicPartition, Long>();
        offsetResult.forEach((key, value) -> partitionOffset.put(new KafkaTopicPartition(key.topic(), key.partition()), value.offset()));

        consumer.close();
        return partitionOffset;
    }

}

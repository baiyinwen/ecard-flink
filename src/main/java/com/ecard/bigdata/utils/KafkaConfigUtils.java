package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static Properties createKafkaProps(ParameterTool parameterTool, String KafkaGroupId) {

        Properties props = parameterTool.getProperties();

        props.put("zookeeper.connect", parameterTool.get(CONFIGS.ZOOKEEPER_SERVERS));
        props.put("bootstrap.servers", parameterTool.get(CONFIGS.KAFKA_BROKERS));

        props.put("group.id", KafkaGroupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        if (parameterTool.getBoolean(CONFIGS.KAFKA_SASL_ENABLE)) {
            props.put("security.protocol", "SASL_TBDS");
            props.put("sasl.mechanism", "TBDS");
            props.put("sasl.tbds.secure.id", parameterTool.get(CONFIGS.KAFKA_SASL_TBDS_SECURE_ID));
            props.put("sasl.tbds.secure.key", parameterTool.get(CONFIGS.KAFKA_SASL_TBDS_SECURE_KEY));
        }

        return props;
    }

}

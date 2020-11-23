package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.externals.rocketMq.RocketMQConfig;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/11/16 15:31
 * @Version 1.0
 **/
public class RocketMQConfigUtils {

    private static Logger logger = LoggerFactory.getLogger(RocketMQConfigUtils.class);

    /**
     * 设置 RocketMQ 配置
     *
     * @param parameterTool
     * @return
     */
    public static Properties createRocketMQProps(ParameterTool parameterTool, String groupId) {

        Properties props = parameterTool.getProperties();
        props.setProperty(RocketMQConfig.NAME_SERVER_ADDR, parameterTool.get(CONFIGS.ROCKET_MQ_NAME_SERVER));
        props.setProperty(RocketMQConfig.CONSUMER_GROUP, groupId);

        logger.info(props.toString());
        return props;
    }

}

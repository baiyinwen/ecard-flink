package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2019/10/23 16:27
 * @Version 1.0
 **/
public class RedisClusterUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisClusterUtils.class);

    private static JedisCluster jedisCluster = null;

    private static String [] REDIS_HOSTS;
    private static int REDIS_PORT;
    private static String REDIS_PASSWORD;

    private static int REDIS_MAX_TOTAL = 8;
    private static int REDIS_MAX_IDLE = 3;
    private static int REDIS_MIN_IDLE = 1;
    private static boolean REDIS_BLOCK_WHEN_EXHAUSTED = true;
    private static int REDIS_MAX_WAIT_MILLIS = 10;


    private static int REDIS_CONNECTION_TIMEOUT = 3000;//连接超时
    private static int REDIS_SO_TIMEOUT = 300;//读取超时
    private static int REDIS_MAX_ATTEMPTS = 5;//重试次数

    public RedisClusterUtils() {}

    static {
        try {
            REDIS_HOSTS = ConfigUtils.getString(CONFIGS.REDIS_HOSTS).split(",");
            REDIS_PORT = ConfigUtils.getInteger(CONFIGS.REDIS_PORT);
            REDIS_PASSWORD = ConfigUtils.getString(CONFIGS.REDIS_PASSWORD);

            Set<HostAndPort> hostAndPortsSet = new HashSet<>();
            for (String redis_host : REDIS_HOSTS) {
                hostAndPortsSet.add(new HostAndPort(redis_host, REDIS_PORT));
            }

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(REDIS_MAX_TOTAL);//资源池中最大连接数
            config.setMaxIdle(REDIS_MAX_IDLE);//资源池中允许最大空闲连接数
            config.setMinIdle(REDIS_MIN_IDLE);//资源池中确保最少空闲连接数
            config.setBlockWhenExhausted(REDIS_BLOCK_WHEN_EXHAUSTED);//资源池用尽后，调用者是否需要等待
            config.setMaxWaitMillis(REDIS_MAX_WAIT_MILLIS);//资源池用尽后，调用者最大等待时间（毫秒）

            if (null == REDIS_PASSWORD || REDIS_PASSWORD.isEmpty()) {
                jedisCluster = new JedisCluster(hostAndPortsSet, REDIS_CONNECTION_TIMEOUT, REDIS_SO_TIMEOUT, REDIS_MAX_ATTEMPTS, config);
            } else {
                jedisCluster = new JedisCluster(hostAndPortsSet, REDIS_CONNECTION_TIMEOUT, REDIS_SO_TIMEOUT, REDIS_MAX_ATTEMPTS, REDIS_PASSWORD, config);
            }
            logger.info("redis: initialPool");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isExistsKey(String key) {

        boolean isExists = jedisCluster.exists(key);
        return  isExists;
    }

    public static void setExpire(String key, int seconds) {

        jedisCluster.expire(key, seconds);
    }

    public static String getValue(String key) {

        String value = jedisCluster.get(key);
        return value;
    }

    public static void setValue(String key, String value) {

        jedisCluster.set(key, value);
    }

    public static void main(String[] args) {

        System.err.println(getValue("signa"));

    }
}

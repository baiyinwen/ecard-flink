package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2019/10/23 16:27
 * @Version 1.0
 **/
public class RedisUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static JedisPool jedisPool = null;

    private static String REDIS_HOST;
    private static int REDIS_PORT;
    private static int REDIS_TIMEOUT;
    private static String REDIS_PASSWORD;

    public RedisUtils() {}

    private static void initialPool() {

        try {
            REDIS_HOST = ConfigUtils.getString(CONFIGS.REDIS_HOST);
            REDIS_PORT = ConfigUtils.getInteger(CONFIGS.REDIS_PORT);
            REDIS_TIMEOUT = ConfigUtils.getInteger(CONFIGS.REDIS_TIMEOUT);
            REDIS_PASSWORD = ConfigUtils.getString(CONFIGS.REDIS_PASSWORD);

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(ConfigUtils.getInteger(CONFIGS.REDIS_MAX_TOTAL));//资源池中最大连接数
            config.setMaxIdle(ConfigUtils.getInteger(CONFIGS.REDIS_MAX_IDLE));//资源池中允许最大空闲连接数
            config.setMinIdle(ConfigUtils.getInteger(CONFIGS.REDIS_MIN_IDLE));//资源池中确保最少空闲连接数
            config.setBlockWhenExhausted(ConfigUtils.getBoolean(CONFIGS.REDIS_BLOCK_WHEN_EXHAUSTED));//资源池用尽后，调用者是否需要等待
            config.setMaxWaitMillis(ConfigUtils.getInteger(CONFIGS.REDIS_MAX_WAIT_MILLIS));//资源池用尽后，调用者最大等待时间（毫秒）
            if (null == REDIS_PASSWORD || REDIS_PASSWORD.isEmpty()) {
                jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT, REDIS_TIMEOUT);
            } else {
                jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT, REDIS_TIMEOUT, REDIS_PASSWORD);
            }
            logger.info("redis: initialPool");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得连接
     * @return Jedis
     */
    public static Jedis getConn() {

        if (null == jedisPool) {
            initialPool();
        }
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    public static void returnConn(Jedis jedis){

        if(null != jedis){
            jedis.close();
        }
    }

    public static String getValue(String key) {

        Jedis jedis = getConn();
        String value = jedis.get(key);
        returnConn(jedis);
        return value;
    }

    public static void setKV(String key, String value) {

        Jedis jedis = getConn();
        jedis.set(key, value);
        returnConn(jedis);
    }

    public static Set<String> getSet(String key) {

        Jedis jedis = getConn();
        Set<String> sets = jedis.smembers(key);
        returnConn(jedis);
        return sets;
    }

    public static void setSet(String key, String member) {

        Jedis jedis = getConn();
        jedis.sadd(key, member);
        returnConn(jedis);
    }

    public static boolean isMember(String key, String member) {

        Jedis jedis = getConn();
        boolean isMember = jedis.sismember(key, member);
        returnConn(jedis);
        return isMember;
    }

    public static void main(String[] args) {

        System.err.println(RedisUtils.isMember("signLogMd5", "aaa"));

    }
}

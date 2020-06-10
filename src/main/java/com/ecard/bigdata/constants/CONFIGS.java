package com.ecard.bigdata.constants;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 10:26
 * @Version 1.0
 **/
public class CONFIGS {

    public final static String KAFKA_BROKERS = "kafka.brokers";
    public final static String ZOOKEEPER_SERVERS = "zookeeper.servers";

    /*TBase参数*/
    public final static String TBASE_JDBC_DATASOURCE_SIZE = "tbase.jdbc.datasource.size";
    public final static String TBASE_JDBC_URL = "tbase.jdbc.url";
    public final static String TBASE_JDBC_USER = "tbase.jdbc.user";
    public final static String TBASE_JDBC_PASSWORD = "tbase.jdbc.password";

    /*redis参数*/
    public final static String REDIS_HOSTS = "redis.hosts";
    public final static String REDIS_PORT = "redis.port";
    public final static String REDIS_PASSWORD = "redis.password";
    public final static String REDIS_MAX_TOTAL = "redis.max.total";

    public final static String OPEN_FALCON_PUSH_URL = "open.falcon.push.url";

    public final static String KAFKA_SASL_ENABLE = "kafka.sasl.enable";

    public final static String STREAM_PARALLELISM = "stream.parallelism";

    public final static String SASL_TBDS_SECURE_ID = "sasl.tbds.secure.id";
    public final static String SASL_TBDS_SECURE_KEY = "sasl.tbds.secure.key";

    public final static String CONSUMER_FROM_TIME = "consumer.from.time";

    public final static String SIGN_KAFKA_TOPIC = "sign.kafka.topic";
    public final static String SIGN_TUMBLING_WINDOW_SIZE = "sign.tumbling.window.size";
    public final static String SIGN_MAX_OUT_OF_ORDER = "sign.max.out.of.order";
    public final static String SIGN_MAX_ALLOWED_LATENESS = "sign.max.allowed.lateness";
    /*签发日志push的配置*/
    public final static String SIGN_OPEN_FALCON_ENDPOINT = "sign.open.falcon.endpoint";
    public final static String SIGN_OPEN_FALCON_STEP = "sign.open.falcon.step";
    public final static String SIGN_OPEN_FALCON_COUNTER_TYPE = "sign.open.falcon.counter.type";
    public final static String SIGN_OPEN_FALCON_TAGS = "sign.open.falcon.tags";

    public final static String COST_TIME_KAFKA_TOPIC = "cost.time.kafka.topic";
    public final static String COST_TIME_TUMBLING_WINDOW_SIZE = "cost.time.tumbling.window.size";
    public final static String COST_TIME_MAX_OUT_OF_ORDER = "cost.time.max.out.of.order";
    public final static String COST_TIME_MAX_ALLOWED_LATENESS = "cost.time.max.allowed.lateness";
    /*接口调用时长push的配置*/
    public final static String COST_TIME_OPEN_FALCON_ENDPOINT = "cost.time.open.falcon.endpoint";
    public final static String COST_TIME_OPEN_FALCON_STEP = "cost.time.open.falcon.step";
    public final static String COST_TIME_OPEN_FALCON_COUNTER_TYPE = "cost.time.open.falcon.counter.type";
    public final static String COST_TIME_OPEN_FALCON_TAGS = "cost.time.open.falcon.tags";

    public final static String COST_TIME_EVENT_CODE_POST1 = "cost.time.event.code.post1";
    public final static String COST_TIME_EVENT_CODE_POST2 = "cost.time.event.code.post2";
    public final static String COST_TIME_EVENT_CODE_GET = "cost.time.event.code.get";

}

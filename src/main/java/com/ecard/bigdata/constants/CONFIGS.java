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
    public final static String TBASE_JDBC_URL = "tbase.jdbc.url";
    public final static String TBASE_JDBC_USER = "tbase.jdbc.user";
    public final static String TBASE_JDBC_PASSWORD = "tbase.jdbc.password";

    /*redis参数*/
    public final static String REDIS_HOSTS = "redis.hosts";
    public final static String REDIS_PASSWORD = "redis.password";

    public final static String OPEN_FALCON_PUSH_URL = "open.falcon.push.url";

    /*kafka身份认证*/
    public final static String KAFKA_SASL_ENABLE = "kafka.sasl.enable";
    public final static String KAFKA_SASL_TBDS_SECURE_ID = "kafka.sasl.tbds.secure.id";
    public final static String KAFKA_SASL_TBDS_SECURE_KEY = "kafka.sasl.tbds.secure.key";

    /*签发量统计*/
    public final static String SIGN_COUNT_KAFKA_TOPIC = "sign.count.kafka.topic";
    public final static String SIGN_COUNT_TUMBLING_WINDOW_SIZE = "sign.count.tumbling.window.size";
    public final static String SIGN_COUNT_MAX_OUT_OF_ORDER = "sign.count.max.out.of.order";
    public final static String SIGN_COUNT_MAX_ALLOWED_LATENESS = "sign.count.max.allowed.lateness";

    /*签发量报警*/
    public final static String SIGN_ALTER_KAFKA_TOPIC = "sign.alter.kafka.topic";
    public final static String SIGN_ALTER_TUMBLING_WINDOW_SIZE = "sign.alter.tumbling.window.size";
    public final static String SIGN_ALTER_MAX_OUT_OF_ORDER = "sign.alter.max.out.of.order";
    public final static String SIGN_ALTER_MAX_ALLOWED_LATENESS = "sign.alter.max.allowed.lateness";

    /*接口时长报警*/
    public final static String COST_TIME_KAFKA_TOPIC = "cost.time.kafka.topic";
    public final static String COST_TIME_TUMBLING_WINDOW_SIZE = "cost.time.tumbling.window.size";
    public final static String COST_TIME_EVENT_CODE_POST1 = "cost.time.event.code.post1";
    public final static String COST_TIME_EVENT_CODE_POST2 = "cost.time.event.code.post2";
    public final static String COST_TIME_EVENT_CODE_GET = "cost.time.event.code.get";

    /*hbase身份认证*/
    public final static String HBASE_ZK_ZNODE_PARENT = "hbase.zk.znode.parent";
    public final static String HBASE_TBDS_SECURE_ENABLE = "hbase.tbds.secure.enable";
    public final static String HBASE_TBDS_SECURE_ID = "hbase.tbds.secure.id";
    public final static String HBASE_TBDS_SECURE_KEY = "hbase.tbds.secure.key";

    /*信用评分迁移*/
    public final static String CREDIT_SCORE_KAFKA_TOPIC = "credit.score.kafka.topic";
    public final static String CREDIT_SCORE_HBASE_TABLE = "credit.score.hbase.table";
    public final static String CREDIT_SCORE_TUMBLING_WINDOW_SIZE = "credit.score.tumbling.window.size";

}

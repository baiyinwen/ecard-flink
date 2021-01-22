package com.ecard.bigdata.constants;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 10:26
 * @Version 1.0
 **/
public class CONFIGS {

    /**
     * 系统参数
     **/
    /*kafka、zookeeper地址*/
    public final static String KAFKA_BROKERS = "kafka.brokers";
    public final static String ZOOKEEPER_SERVERS = "zookeeper.servers";

    /*TBase参数*/
    public final static String TBASE_JDBC_URL = "tbase.jdbc.url";
    public final static String TBASE_JDBC_USER = "tbase.jdbc.user";
    public final static String TBASE_JDBC_PASSWORD = "tbase.jdbc.password";

    /*redis参数*/
    public final static String REDIS_HOSTS = "redis.hosts";
    public final static String REDIS_PASSWORD = "redis.password";

    /*open-falcon报警推送url*/
    public final static String OPEN_FALCON_PUSH_URL = "open.falcon.push.url";

    /*kafka身份认证*/
    public final static String KAFKA_SASL_ENABLE = "kafka.sasl.enable";
    public final static String KAFKA_SASL_TBDS_SECURE_ID = "kafka.sasl.tbds.secure.id";
    public final static String KAFKA_SASL_TBDS_SECURE_KEY = "kafka.sasl.tbds.secure.key";

    /*hbase身份认证*/
    public final static String HBASE_ZK_ZNODE_PARENT = "hbase.zk.znode.parent";
    public final static String HBASE_TBDS_SECURE_ENABLE = "hbase.tbds.secure.enable";
    public final static String HBASE_TBDS_SECURE_ID = "hbase.tbds.secure.id";
    public final static String HBASE_TBDS_SECURE_KEY = "hbase.tbds.secure.key";

    /*rocketMQ地址*/
    public final static String ROCKET_MQ_NAME_SERVER = "rocket.mq.name.server";

    /**
     * 应用参数
     **/
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

    /*信用评分迁移*/
    public final static String CREDIT_SCORE_KAFKA_TOPIC = "credit.score.kafka.topic";
    public final static String CREDIT_SCORE_HBASE_TABLE = "credit.score.hbase.table";
    public final static String CREDIT_SCORE_TUMBLING_WINDOW_SIZE = "credit.score.tumbling.window.size";

    /*计费接口调用次数*/
    public final static String BILLING_TRANSFER_KAFKA_TOPIC = "billing.transfer.count.kafka.topic";
    public final static String BILLING_TRANSFER_TUMBLING_WINDOW_SIZE = "billing.transfer.count.tumbling.window.size";
    public final static String BILLING_TRANSFER_MAX_OUT_OF_ORDER = "billing.transfer.count.max.out.of.order";
    public final static String BILLING_TRANSFER_MAX_ALLOWED_LATENESS = "billing.transfer.count.max.allowed.lateness";

    /*风控支付日志落库hbase并推送到kafka*/
    public final static String RISK_CONTROL_PAY_KAFKA_CONSUMER_GROUP = "risk.control.pay.kafka.consumer.group";
    public final static String RISK_CONTROL_PAY_KAFKA_CONSUMER_TOPIC = "risk.control.pay.kafka.consumer.topic";
    public final static String RISK_CONTROL_PAY_KAFKA_PRODUCER_TOPIC = "risk.control.pay.kafka.producer.topic";
    public final static String RISK_CONTROL_PAY_HBASE_TABLE = "risk.control.pay.hbase.table";

    /*信用分调用次数统计*/
    public final static String CREDIT_SCORE_COUNT_KAFKA_TOPIC = "credit.score.count.kafka.topic";
    public final static String CREDIT_SCORE_COUNT_TUMBLING_WINDOW_SIZE = "credit.score.count.tumbling.window.size";
    public final static String CREDIT_SCORE_COUNT_MAX_OUT_OF_ORDER = "credit.score.count.max.out.of.order";
    public final static String CREDIT_SCORE_COUNT_MAX_ALLOWED_LATENESS = "credit.score.count.max.allowed.lateness";

    /*电子社保卡信息核验调用次数统计*/
    public final static String INFO_VERIFICATION_COUNT_KAFKA_TOPIC = "info.verification.count.kafka.topic";
    public final static String INFO_VERIFICATION_COUNT_TUMBLING_WINDOW_SIZE = "info.verification.count.tumbling.window.size";
    public final static String INFO_VERIFICATION_COUNT_MAX_OUT_OF_ORDER = "info.verification.count.max.out.of.order";
    public final static String INFO_VERIFICATION_COUNT_MAX_ALLOWED_LATENESS = "info.verification.count.max.allowed.lateness";

    /*展验码统计量*/
    public final static String QRCODE_ALARM_KAFKA_TOPIC = "qrcode.alarm.kafka.topic";
    public final static String QRCODE_ALARM_LOG_EVENTS = "qrcode.alarm.log.events";
    public final static String QRCODE_ALARM_TUMBLING_WINDOW_SIZE = "qrcode.alarm.tumbling.window.size";
    public final static String QRCODE_ALARM_MAX_OUT_OF_ORDER = "qrcode.alarm.max.out.of.order";
    public final static String QRCODE_ALARM_MAX_ALLOWED_LATENESS = "qrcode.alarm.max.allowed.lateness";

}

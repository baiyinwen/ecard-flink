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

    public final static String TBASE_JDBC_DATASOURCE_SIZE = "tbase.jdbc.datasource.size";
    public final static String TBASE_JDBC_URL = "tbase.jdbc.url";
    public final static String TBASE_JDBC_USER = "tbase.jdbc.user";
    public final static String TBASE_JDBC_PASSWORD = "tbase.jdbc.password";

    public final static String KAFKA_SASL_ENABLE = "kafka.sasl.enable";

    public final static String RESTART_ATTEMPTS = "restart.attempts";
    public final static String DELAY_BETWEEN_ATTEMPTS = "delay.between.attempts";
    public final static String STREAM_PARALLELISM = "stream.parallelism";

    public final static String SASL_TBDS_SECURE_ID = "sasl.tbds.secure.id";
    public final static String SASL_TBDS_SECURE_KEY = "sasl.tbds.secure.key";

    public final static String CONSUMER_FROM_TIME = "consumer.from.time";
    public final static String STREAM_CHECKPOINT_ENABLE = "stream.checkpoint.enable";
    public final static String STREAM_CHECKPOINT_DIR = "stream.checkpoint.dir";
    public final static String STREAM_CHECKPOINT_TYPE = "stream.checkpoint.type";
    public final static String STREAM_CHECKPOINT_INTERVAL = "stream.checkpoint.interval";
    public final static String CHECKPOINT_MEMORY = "memory";
    public final static String CHECKPOINT_FS = "fs";
    public final static String CHECKPOINT_ROCKETSDB = "rocksdb";

    public final static String JOB_NAME = "job.name";
    public final static String KAFKA_TOPIC = "kafka.topic";
    public final static String TUMBLING_WINDOW_SIZE = "tumbling.window.size";
    public final static String MAX_OUT_OF_ORDER = "max.out.of.order";
    public final static String MAX_ALLOWED_LATENESS = "max.allowed.lateness";

}

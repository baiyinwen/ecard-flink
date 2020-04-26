package com.ecard.bigdata.constants;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 10:26
 * @Version 1.0
 **/
public class CONFIGS {

    public static final String KAFKA_BROKERS = "kafka.brokers";
    public static final String ZOOKEEPER_BROKERS = "zookeeper.brokers";

    public static final String RESTART_ATTEMPTS = "restart.attempts";
    public static final String DELAY_BETWEEN_ATTEMPTS = "delay.between.attempts";
    public static final String STREAM_PARALLELISM = "stream.parallelism";

    public static final String CONSUMER_FROM_TIME = "consumer.from.time";
    public static final String STREAM_CHECKPOINT_ENABLE = "stream.checkpoint.enable";
    public static final String STREAM_CHECKPOINT_DIR = "stream.checkpoint.dir";
    public static final String STREAM_CHECKPOINT_TYPE = "stream.checkpoint.type";
    public static final String STREAM_CHECKPOINT_INTERVAL = "stream.checkpoint.interval";
    public static final String CHECKPOINT_MEMORY = "memory";
    public static final String CHECKPOINT_FS = "fs";
    public static final String CHECKPOINT_ROCKETSDB = "rocksdb";

    public static final String JOB_NAME = "job.name";
    public static final String KAFKA_TOPIC = "kafka.topic";
    public static final String TUMBLING_WINDOW_SIZE = "tumbling.window.size";
    public static final String MAX_OUT_OF_ORDER = "max.out.of.order";
    public static final String MAX_ALLOWED_LATENESS = "max.allowed.lateness";

}

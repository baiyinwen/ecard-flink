package com.ecard.bigdata.main;

import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.BillingTransfer;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.BillingTransferCountSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.BillingTransferCountWatermark;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Description 计费系统接口调用次数统计
 * @Author WangXueDong
 * @Date 2020/9/14 15:10
 * @Version 1.0
 **/
public class BillingTransferCountStream {

    private static Logger logger = LoggerFactory.getLogger(BillingTransferCountStream.class);
    private static final String ClassName = BillingTransferCountStream.class.getSimpleName();
    private static long STREAM_CHECKPOINT_INTERVAL = 60000;
    private static CheckpointingMode STREAM_CHECKPOINT_MODE = CheckpointingMode.EXACTLY_ONCE;

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/9/14 15:10
     **/
    public static void main(String[] args) throws Exception {

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.BILLING_TRANSFER_KAFKA_TOPIC);
        final String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL, STREAM_CHECKPOINT_MODE);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        WindowedStream<BillingTransfer, String, TimeWindow> timeWindowRes = data.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String event = jsonLogInfo.getEvent();
                if (CONSTANTS.EVENT_BILLING_LOG_AUTH_PHOTO.equals(event)) {
                    String md5Log = EncodeUtils.md5Encode(jsonLogInfo.getOrigLog());
                    boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.BILLING_REDIS_LOG_COUNT_MD5_KEY + md5Log);
                    if (isMember) {
                        return false;
                    } else {
                        RedisClusterUtils.setValue(CONSTANTS.BILLING_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.BILLING_REDIS_LOG_COUNT_MD5_KEY);
                        RedisClusterUtils.setExpire(CONSTANTS.BILLING_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.BILLING_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS);
                    }
                    return true;
                }
            }
            return false;
        }).map((MapFunction<JsonLogInfo, BillingTransfer>) jsonLogInfo -> {
            BillingTransfer billingTransfer = new BillingTransfer();
            billingTransfer.setCollectTime(DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            billingTransfer.setChannelNo(jsonLogInfo.getChannelNo());
            billingTransfer.setTransferTimes(CONSTANTS.NUMBER_1);
            return billingTransfer;
        }).returns(TypeInformation.of(new TypeHint<BillingTransfer>() {})).assignTimestampsAndWatermarks(new BillingTransferCountWatermark()).setParallelism(1)
          .keyBy(new KeySelector<BillingTransfer, String>() {
            @Override
            public String getKey(BillingTransfer billingTransfer) throws Exception {
                return billingTransfer.getChannelNo();
            }
        }).timeWindow(Time.seconds(parameterTool.getLong(CONFIGS.BILLING_TRANSFER_TUMBLING_WINDOW_SIZE)))
          .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.BILLING_TRANSFER_MAX_ALLOWED_LATENESS)));

        DataStream<BillingTransfer> reduceRes = timeWindowRes
        .reduce((ReduceFunction<BillingTransfer>) (s1, s2) -> {
            s1.setTransferTimes(s1.getTransferTimes() + s2.getTransferTimes());
            return s1;
        }).returns(TypeInformation.of(new TypeHint<BillingTransfer>() {}));

        reduceRes.addSink(new BillingTransferCountSink()).name(BillingTransferCountSink.class.getSimpleName());

        env.execute(ClassName);

    }

}

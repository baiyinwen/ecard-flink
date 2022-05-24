package com.ecard.bigdata.main;

import com.ecard.bigdata.bean.InfoVerifyLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.InfoVerifyAmount;
import com.ecard.bigdata.schemas.InfoVerifyLogSchema;
import com.ecard.bigdata.sink.InfoVerifyAmountCountSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.InfoVerifyAmountCountWatermark;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Description 电子社保卡信息核验调用次数统计
 * @Author WangXueDong
 * @Date 2020/12/03 15:24
 * @Version 1.0
 **/
public class InfoVerifyAmountCountStream {

    private static Logger logger = LoggerFactory.getLogger(InfoVerifyAmountCountStream.class);
    private static final String ClassName = InfoVerifyAmountCountStream.class.getSimpleName();
    private static long STREAM_CHECKPOINT_INTERVAL = 60000;
    private static CheckpointingMode STREAM_CHECKPOINT_MODE = CheckpointingMode.EXACTLY_ONCE;

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/12/03 15:24
     **/
    public static void main(String[] args) {

        try {
            logger.info("start " + ClassName);
            final ParameterTool parameterTool = ParameterUtils.createParameterTool();
            String topic  = parameterTool.get(CONFIGS.INFO_VERIFICATION_COUNT_KAFKA_TOPIC);
            final String KafkaGroupId = topic + "_" + ClassName;
            Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

            StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
            env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL, STREAM_CHECKPOINT_MODE);

            InfoVerifyLogSchema infoVerifyLogSchema = new InfoVerifyLogSchema();
            FlinkKafkaConsumer010<InfoVerifyLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, infoVerifyLogSchema, props);
            DataStreamSource<InfoVerifyLogInfo> data = env.addSource(consumer);

            SingleOutputStreamOperator<InfoVerifyLogInfo> distinctRes = data.filter((FilterFunction<InfoVerifyLogInfo>) infoVerifyLogInfo -> {
                if (null != infoVerifyLogInfo) {
                    if (infoVerifyLogInfo.getTime() == null || infoVerifyLogInfo.getEvent() == null || infoVerifyLogInfo.getAppKey() == null) {
                        logger.info("JSON日志数据异常！" + infoVerifyLogInfo.getOrigLog());
                        return false;
                    }
                    String md5Log = EncodeUtils.md5Encode(infoVerifyLogInfo.getOrigLog());
                    boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.INFO_VERIFICATION_REDIS_LOG_COUNT_MD5_KEY + md5Log);
                    if (isMember) {
                        return false;
                    } else {
                        RedisClusterUtils.setValue(CONSTANTS.INFO_VERIFICATION_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.INFO_VERIFICATION_REDIS_LOG_COUNT_MD5_KEY);
                        RedisClusterUtils.setExpire(CONSTANTS.INFO_VERIFICATION_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.INFO_VERIFICATION_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS);
                    }
                    return true;
                }
                return false;
            }).setParallelism(1);

            WindowedStream<InfoVerifyAmount, Tuple2<String, String>, TimeWindow> mapRes = distinctRes.map((MapFunction<InfoVerifyLogInfo, InfoVerifyAmount>) infoVerifyLogInfo -> {
                InfoVerifyAmount infoVerifyAmount = new InfoVerifyAmount();
                infoVerifyAmount.setCollectTime(DateTimeUtils.toTimestamp(infoVerifyLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
                infoVerifyAmount.setEvent(infoVerifyLogInfo.getEvent());
                infoVerifyAmount.setAppKey(infoVerifyLogInfo.getAppKey());
                infoVerifyAmount.setTransferTimes(CONSTANTS.NUMBER_1);
                return infoVerifyAmount;
            }).returns(TypeInformation.of(new TypeHint<InfoVerifyAmount>() {})).assignTimestampsAndWatermarks(new InfoVerifyAmountCountWatermark())
                    .keyBy(new KeySelector<InfoVerifyAmount, Tuple2<String, String>>() {
                        @Override
                        public Tuple2<String, String> getKey(InfoVerifyAmount infoVerifyAmount) {
                            Tuple2<String, String> tuple2 = new Tuple2<>();
                            tuple2.f0 = infoVerifyAmount.getEvent();
                            tuple2.f1 = infoVerifyAmount.getAppKey();
                            return tuple2;
                        }
                    }).timeWindow(Time.seconds(parameterTool.getLong(CONFIGS.INFO_VERIFICATION_COUNT_TUMBLING_WINDOW_SIZE)))
                    .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.INFO_VERIFICATION_COUNT_MAX_ALLOWED_LATENESS)));

            DataStream<InfoVerifyAmount> reduceRes = mapRes.reduce((ReduceFunction<InfoVerifyAmount>) (s1, s2) -> {
                s1.setTransferTimes(s1.getTransferTimes() + s2.getTransferTimes());
                return s1;
            });

            reduceRes.addSink(new InfoVerifyAmountCountSink()).name(InfoVerifyAmountCountSink.class.getSimpleName());

            env.execute(ClassName);
        } catch (Exception e) {
//            e.printStackTrace();
            logger.warn("异常："+e);
        }
    }

}

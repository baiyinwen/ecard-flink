package com.ecard.bigdata.main;

import com.ecard.bigdata.bean.CreditScoreLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.CreditScoreAmount;
import com.ecard.bigdata.schemas.CreditScoreLogSchema;
import com.ecard.bigdata.sink.CreditScoreAmountCountSink;
import com.ecard.bigdata.utils.*;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
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

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * @Description 信用分统计
 * @Author WangXueDong
 * @Date 2020/12/01 15:24
 * @Version 1.0
 **/
public class CreditScoreAmountCountStream {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreAmountCountStream.class);
    private static final String ClassName = CreditScoreAmountCountStream.class.getSimpleName();
    private static long STREAM_CHECKPOINT_INTERVAL = 60000;
    private static CheckpointingMode STREAM_CHECKPOINT_MODE = CheckpointingMode.EXACTLY_ONCE;

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/12/01 15:24
     **/
    public static void main(String[] args) throws Exception {

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.CREDIT_SCORE_COUNT_KAFKA_TOPIC);
        final String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL, STREAM_CHECKPOINT_MODE);

        CreditScoreLogSchema creditScoreLogSchema = new CreditScoreLogSchema();
        FlinkKafkaConsumer010<CreditScoreLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, creditScoreLogSchema, props);
        DataStreamSource<CreditScoreLogInfo> data = env.addSource(consumer);

        SingleOutputStreamOperator<CreditScoreLogInfo> distinctRes = data.filter((FilterFunction<CreditScoreLogInfo>) creditScoreLogInfo -> {
            if (null != creditScoreLogInfo) {
                String md5Log = EncodeUtils.md5Encode(creditScoreLogInfo.getOrigLog());
                boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.CREDIT_SCORE_REDIS_LOG_COUNT_MD5_KEY + md5Log);
                if (isMember) {
                    return false;
                } else {
                    RedisClusterUtils.setValue(CONSTANTS.CREDIT_SCORE_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.CREDIT_SCORE_REDIS_LOG_COUNT_MD5_KEY);
                    RedisClusterUtils.setExpire(CONSTANTS.CREDIT_SCORE_REDIS_LOG_COUNT_MD5_KEY + md5Log, CONSTANTS.CREDIT_SCORE_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS);
                }
                return true;
            }
            return false;
        }).setParallelism(1);

        WindowedStream<CreditScoreAmount, Tuple2<String, String>, TimeWindow> mapRes = distinctRes.map((MapFunction<CreditScoreLogInfo, CreditScoreAmount>) creditScoreLogInfo -> {
            CreditScoreAmount creditScoreAmount = new CreditScoreAmount();
            creditScoreAmount.setCollectTime(new Timestamp(new Date().getTime()));
            creditScoreAmount.setEvent(creditScoreLogInfo.getEvent());
            creditScoreAmount.setAppKey(creditScoreLogInfo.getAppKey());
            creditScoreAmount.setTransferTimes(CONSTANTS.NUMBER_1);
            return creditScoreAmount;
        }).returns(TypeInformation.of(new TypeHint<CreditScoreAmount>() {}))
          .keyBy(new KeySelector<CreditScoreAmount, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> getKey(CreditScoreAmount creditScoreAmount) {
                Tuple2<String, String> tuple2 = new Tuple2<>();
                tuple2.f0 = creditScoreAmount.getEvent();
                tuple2.f1 = creditScoreAmount.getAppKey();
                return tuple2;
            }
        }).timeWindow(Time.seconds(parameterTool.getLong(CONFIGS.CREDIT_SCORE_COUNT_TUMBLING_WINDOW_SIZE)))
          .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.CREDIT_SCORE_COUNT_MAX_ALLOWED_LATENESS)));

        DataStream<CreditScoreAmount> reduceRes = mapRes.reduce((ReduceFunction<CreditScoreAmount>) (s1, s2) -> {
            s1.setTransferTimes(s1.getTransferTimes() + s2.getTransferTimes());
            return s1;
        });

        reduceRes.addSink(new CreditScoreAmountCountSink()).name(CreditScoreAmountCountSink.class.getSimpleName());

        env.execute(ClassName);

    }

}

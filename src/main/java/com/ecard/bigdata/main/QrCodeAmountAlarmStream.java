package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.QrCodeAmount;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.QrCodeAmountAlarmSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.QrCodeAmountAlarmWatermark;
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
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2021/1/21 14:24
 * @Version 1.0
 **/
public class QrCodeAmountAlarmStream {

    private static Logger logger = LoggerFactory.getLogger(QrCodeAmountAlarmStream.class);
    private static final String ClassName = QrCodeAmountAlarmStream.class.getSimpleName();
    private static long STREAM_CHECKPOINT_INTERVAL = 60000;
    private static CheckpointingMode STREAM_CHECKPOINT_MODE = CheckpointingMode.EXACTLY_ONCE;

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2021/1/21 14:24
     **/
    public static void main(String[] args) throws Exception {

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.QRCODE_ALARM_KAFKA_TOPIC);
        final String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        List<String> qrCodeAlarmLogEvents = Arrays.asList(ConfigUtils.getString(CONFIGS.QRCODE_ALARM_LOG_EVENTS).split(","));

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL, STREAM_CHECKPOINT_MODE);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        consumer.setStartFromLatest();
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        SingleOutputStreamOperator<JsonLogInfo> filterRes = data.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String event = jsonLogInfo.getEvent();
                if (qrCodeAlarmLogEvents.contains(event)) {
                    String outputStr = jsonLogInfo.getOutput().toString();
                    JSONObject outputJson;
                    try {
                        if (jsonLogInfo.getOutput() instanceof Map) {
                            outputStr = JSONObject.toJSONString(jsonLogInfo.getOutput());
                        }
                        outputJson = JSONObject.parseObject(outputStr);
                    } catch (Exception e) {
                        logger.error(jsonLogInfo.toString() + " --- 日志解析异常" + e.getMessage());
//                        e.printStackTrace();
                        logger.warn("异常："+e);
                        return  false;
                    }
                    if (outputJson != null) {
                        if (CONSTANTS.EVENT_MSG_CODE_VALUE.equals(outputJson.getString(CONSTANTS.EVENT_MSG_CODE_KEY))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });

        SingleOutputStreamOperator<JsonLogInfo> distinctRes = filterRes.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String md5Log = EncodeUtils.md5Encode(jsonLogInfo.getOrigLog());
                boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.QRCODE_REDIS_LOG_ALARM_MD5_KEY + md5Log);
                if (isMember) {
                    return false;
                } else {
                    RedisClusterUtils.setValue(CONSTANTS.QRCODE_REDIS_LOG_ALARM_MD5_KEY + md5Log, CONSTANTS.QRCODE_REDIS_LOG_ALARM_MD5_KEY);
                    RedisClusterUtils.setExpire(CONSTANTS.QRCODE_REDIS_LOG_ALARM_MD5_KEY + md5Log, CONSTANTS.QRCODE_REDIS_LOG_ALARM_KEY_EXPIRE_SECONDS);
                }
                return true;
            }
            return false;
        }).setParallelism(1);

        DataStream<QrCodeAmount> mapRes = distinctRes.map((MapFunction<JsonLogInfo, QrCodeAmount>) jsonLogInfo -> {
            QrCodeAmount qrCodeAmount = new QrCodeAmount();
            qrCodeAmount.setCollectTime(DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            qrCodeAmount.setEvent(jsonLogInfo.getEvent());
            qrCodeAmount.setTransferTimes(CONSTANTS.NUMBER_1);
            return qrCodeAmount;
        }).returns(TypeInformation.of(new TypeHint<QrCodeAmount>() {}));

        WindowedStream<QrCodeAmount, String, TimeWindow> timeWindowRes = mapRes.assignTimestampsAndWatermarks(new QrCodeAmountAlarmWatermark())
            .keyBy(new KeySelector<QrCodeAmount, String>() {
                @Override
                public String getKey(QrCodeAmount qrCodeAmount) {
                    return qrCodeAmount.getEvent();
                }
            })
            .timeWindow(Time.seconds(parameterTool.getLong(CONFIGS.QRCODE_ALARM_TUMBLING_WINDOW_SIZE)))
            .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.QRCODE_ALARM_MAX_ALLOWED_LATENESS)));

        DataStream<QrCodeAmount> reduceRes = timeWindowRes.reduce((ReduceFunction<QrCodeAmount>) (s1, s2) -> {
            s1.setTransferTimes(s1.getTransferTimes() + s2.getTransferTimes());
            return s1;
        });//.returns(TypeInformation.of(new TypeHint<QrCodeAmount>() {}));

        reduceRes.addSink(new QrCodeAmountAlarmSink()).name(QrCodeAmountAlarmSink.class.getSimpleName());

        env.execute(ClassName);

    }

}

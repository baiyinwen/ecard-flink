package com.ecard.bigdata.main;

import com.ecard.bigdata.bean.NginxLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.NginxLogCostTime;
import com.ecard.bigdata.schemas.NginxLogSchema;
import com.ecard.bigdata.sink.NginxLogCostTimeSink;
import com.ecard.bigdata.utils.*;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Description 接口调用时长报警（nginx接口监控保留已不再使用）
 * @Author WangXueDong
 * @Date 2020/4/10 9:24
 * @Version 1.0
 **/
public class NginxLogCostTimeStream {

    private static Logger logger = LoggerFactory.getLogger(NginxLogCostTimeStream.class);
    private static final String ClassName = NginxLogCostTimeStream.class.getSimpleName();

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/4/10 9:24
     **/
    public static void main(String[] args) throws Exception {

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.COST_TIME_KAFKA_TOPIC);
        final String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        NginxLogSchema nginxLogSchema = new NginxLogSchema();
        FlinkKafkaConsumer010<NginxLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, nginxLogSchema, props);
        consumer.setStartFromLatest();//设置从最新位置开始消费
        DataStreamSource<NginxLogInfo> data = env.addSource(consumer);

        WindowedStream<NginxLogCostTime, String, TimeWindow> timeWindowRes = data.filter((FilterFunction<NginxLogInfo>) nginxLogInfo -> {
            if (null != nginxLogInfo) {
                String code = nginxLogInfo.getCode();
                List<String> events = new ArrayList<>();
                List<String> costTimeEventCodePost1 = Arrays.asList(ConfigUtils.getString(CONFIGS.COST_TIME_EVENT_CODE_POST1).split(","));
                List<String> costTimeEventCodePost2 = Arrays.asList(ConfigUtils.getString(CONFIGS.COST_TIME_EVENT_CODE_POST2).split(","));;
                List<String> costTimeEventCodeGet = Arrays.asList(ConfigUtils.getString(CONFIGS.COST_TIME_EVENT_CODE_GET).split(","));
                events.addAll(costTimeEventCodePost1);
                events.addAll(costTimeEventCodePost2);
                events.addAll(costTimeEventCodeGet);

                if (CONSTANTS.NGINX_STATUS_SUCCESS_VALUE.equals(code)) {
                    for (String event: events) {
                        if (!event.trim().isEmpty() && event.trim().equals(nginxLogInfo.getEvent().trim())) {
                            String md5Log = EncodeUtils.md5Encode(nginxLogInfo.getOrigLog());
                            boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.COST_TIME_REDIS_LOG_MD5_KEY + md5Log);
                            if (isMember) {
                                return false;
                            } else {
                                RedisClusterUtils.setValue(CONSTANTS.COST_TIME_REDIS_LOG_MD5_KEY + md5Log, CONSTANTS.COST_TIME_REDIS_LOG_MD5_KEY);
                                RedisClusterUtils.setExpire(CONSTANTS.COST_TIME_REDIS_LOG_MD5_KEY + md5Log, CONSTANTS.COST_TIME_REDIS_LOG_KEY_EXPIRE_SECONDS);
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }).map((MapFunction<NginxLogInfo, NginxLogCostTime>) nginxLogInfo -> {
            NginxLogCostTime nginxLogCostTime = new NginxLogCostTime();
            nginxLogCostTime.setIp(nginxLogInfo.getIp());
            nginxLogCostTime.setEvent(nginxLogInfo.getEvent());
            nginxLogCostTime.setTime(nginxLogInfo.getTime());
            nginxLogCostTime.setCostTime(nginxLogInfo.getCostTime());
            return nginxLogCostTime;
        }).returns(TypeInformation.of(new TypeHint<NginxLogCostTime>() {})).setParallelism(1)
        .keyBy(new KeySelector<NginxLogCostTime, String>() {
            @Override
            public String getKey(NginxLogCostTime nginxLogCostTime) {
                return nginxLogCostTime.getEvent();
            }
        }).timeWindow(Time.seconds(parameterTool.getLong(CONFIGS.COST_TIME_TUMBLING_WINDOW_SIZE)));;

        DataStream<NginxLogCostTime> reduceRes = timeWindowRes.reduce((ReduceFunction<NginxLogCostTime>) (n1, n2) -> {
            n1.setCostTime(n2.getCostTime());
            return n1;
        });

        reduceRes.addSink(new NginxLogCostTimeSink()).name(NginxLogCostTimeSink.class.getSimpleName());

        env.execute(ClassName);

    }

}

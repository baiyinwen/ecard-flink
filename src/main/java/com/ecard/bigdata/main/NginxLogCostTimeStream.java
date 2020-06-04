package com.ecard.bigdata.main;

import com.ecard.bigdata.bean.NginxLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.NginxLogCostTime;
import com.ecard.bigdata.schemas.NginxLogSchema;
import com.ecard.bigdata.sink.NginxLogCostTimeSink;
import com.ecard.bigdata.utils.*;
import com.ecard.bigdata.waterMarkers.NginxLogCostTimeWatermark;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 9:24
 * @Version 1.0
 **/
public class NginxLogCostTimeStream {

    private static Logger logger = LoggerFactory.getLogger(NginxLogCostTimeStream.class);

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangXueDong
     * @Date 2020/4/10 9:24
     **/
    public static void main(String[] args) throws Exception {

        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, CONFIGS.COST_TIME_KAFKA_TOPIC);
        String topic  = parameterTool.get(CONFIGS.COST_TIME_KAFKA_TOPIC);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);

        NginxLogSchema nginxLogSchema = new NginxLogSchema();
        FlinkKafkaConsumer010<NginxLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, nginxLogSchema, props);
        consumer.setStartFromLatest();//设置从最新位置开始消费
        DataStreamSource<NginxLogInfo> data = env.addSource(consumer);

        int reParallelism = (int) Math.ceil(parameterTool.getDouble(CONFIGS.STREAM_PARALLELISM)/2.0);

        List<String> events = new ArrayList<>();
        List<String> costTimeEventCodePost1 = Arrays.asList(ConfigUtils.getString(CONFIGS.COST_TIME_EVENT_CODE_POST1).split(","));
        List<String> costTimeEventCodePost2 = Arrays.asList(ConfigUtils.getString(CONFIGS.COST_TIME_EVENT_CODE_POST2).split(","));;
        List<String> costTimeEventCodeGet = Arrays.asList(ConfigUtils.getString(CONFIGS.COST_TIME_EVENT_CODE_GET).split(","));
        events.addAll(costTimeEventCodePost1);
        events.addAll(costTimeEventCodePost2);
        events.addAll(costTimeEventCodeGet);

        DataStream<NginxLogCostTime> mapRes = data.filter((FilterFunction<NginxLogInfo>) nginxLogInfo -> {
            if (null != nginxLogInfo) {
                String code = nginxLogInfo.getCode();
                if (CONSTANTS.NGINX_STATUS_SUCCESS_VALUE.equals(code)) {
                    for (String event: events) {
                        if (!event.trim().isEmpty() && event.trim().equals(nginxLogInfo.getEvent().trim())) {
                            String md5Log = Md5Utils.encodeMd5(nginxLogInfo.getOrigLog());
                            boolean isMember = RedisUtils.isMember(CONSTANTS.COST_TIME_REDIS_LOG_MD5_KEY, md5Log);
                            if (isMember) {
                                return false;
                            } else {
                                RedisUtils.setSet(CONSTANTS.COST_TIME_REDIS_LOG_MD5_KEY, md5Log);
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
        }).returns(TypeInformation.of(new TypeHint<NginxLogCostTime>() {})).setParallelism(reParallelism);

        DataStream<NginxLogCostTime> reduceRes = mapRes.assignTimestampsAndWatermarks(new NginxLogCostTimeWatermark())
                .timeWindowAll(Time.seconds(parameterTool.getLong(CONFIGS.COST_TIME_TUMBLING_WINDOW_SIZE)))
                .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.COST_TIME_MAX_ALLOWED_LATENESS)))
                .reduce((ReduceFunction<NginxLogCostTime>) (d1, d2) -> {
                    NginxLogCostTime nginxLogCostTime = new NginxLogCostTime();
                    nginxLogCostTime.setTime(d2.getTime());
                    return nginxLogCostTime;
                }).returns(TypeInformation.of(new TypeHint<NginxLogCostTime>() {}));

        reduceRes.addSink(new NginxLogCostTimeSink());

        env.execute("NginxLogCostTimeStream");

    }

}

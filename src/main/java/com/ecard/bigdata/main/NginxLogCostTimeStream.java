package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.NginxLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.DataAnalysisSignMin;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.model.NginxLogCostTime;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.schemas.NginxLogSchema;
import com.ecard.bigdata.utils.DateTimeUtils;
import com.ecard.bigdata.utils.ExecutionEnvUtils;
import com.ecard.bigdata.utils.KafkaConfigUtils;
import com.ecard.bigdata.utils.ParameterUtils;
import com.ecard.bigdata.waterMarkers.DataAnalysisSignWatermark;
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

        DataStream<NginxLogCostTime> mapRes = data.filter((FilterFunction<NginxLogInfo>) nginxLogInfo -> {
            String code = nginxLogInfo.getCode();
            if (CONSTANTS.NGINX_STATUS_SUCCESS_VALUE.equals(code)) {
                return true;
            }
            return false;
        }).map((MapFunction<NginxLogInfo, NginxLogCostTime>) nginxLogInfo -> {
            NginxLogCostTime nginxLogCostTime = new NginxLogCostTime();
            nginxLogCostTime.setIp(nginxLogInfo.getIp());
            nginxLogCostTime.setEvent(nginxLogInfo.getEvent());
            nginxLogCostTime.setTime(nginxLogInfo.getTime());
            nginxLogCostTime.setCostTime(nginxLogInfo.getCostTime());
            return nginxLogCostTime;
        }).returns(TypeInformation.of(new TypeHint<NginxLogCostTime>() {})).setParallelism(1);

        /*DataStream<DataAnalysisSignMin> reduceRes = mapRes.assignTimestampsAndWatermarks(new DataAnalysisSignWatermark())
                .timeWindowAll(Time.seconds(parameterTool.getLong(CONFIGS.COST_TIME_TUMBLING_WINDOW_SIZE)))
                .allowedLateness(Time.seconds(parameterTool.getLong(CONFIGS.COST_TIME_MAX_ALLOWED_LATENESS)))
                .reduce((ReduceFunction<DataAnalysisSignMin>) (d1, d2) -> {
                    DataAnalysisSignMin dataAnalysisSignMin = new DataAnalysisSignMin();
                    dataAnalysisSignMin.setCollectTime(d1.getCollectTime());
                    dataAnalysisSignMin.setTransferTimes(d1.getTransferTimes() + d2.getTransferTimes());
                    return dataAnalysisSignMin;
                }).returns(TypeInformation.of(new TypeHint<DataAnalysisSignMin>() {}));*/

        //reduceRes.addSink(new NginxLogCostTimeSink());

        env.execute();

    }

}

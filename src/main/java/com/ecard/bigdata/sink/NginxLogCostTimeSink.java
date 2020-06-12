package com.ecard.bigdata.sink;

import com.ecard.bigdata.model.NginxLogCostTime;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.DateTimeUtils;
import com.ecard.bigdata.utils.PushToFalconUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/6/3 09:59
 * @Version 1.0
 **/
public class NginxLogCostTimeSink extends RichSinkFunction<NginxLogCostTime> {

    private static Logger logger = LoggerFactory.getLogger(NginxLogCostTimeSink.class);

    private PushToFalconUtils pushToFalconUtils;

    private static String endpoint;
    private static int step;
    private static String counterType;
    private static String tags;

    @Override
    public void open(Configuration parameters) throws Exception {
        pushToFalconUtils = new PushToFalconUtils();

        endpoint = ConfigUtils.getString(CONFIGS.COST_TIME_OPEN_FALCON_ENDPOINT);
        step = ConfigUtils.getInteger(CONFIGS.COST_TIME_OPEN_FALCON_STEP);
        counterType = ConfigUtils.getString(CONFIGS.COST_TIME_OPEN_FALCON_COUNTER_TYPE);
        tags = ConfigUtils.getString(CONFIGS.COST_TIME_OPEN_FALCON_TAGS);

        logger.info("调用open --- ");
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        logger.info("调用close --- ");
        super.close();
    }

    @Override
    public void invoke(NginxLogCostTime nginxLogCostTime, Context context) {

        pushNginxLogCostTime(nginxLogCostTime);
    }

    private void pushNginxLogCostTime(NginxLogCostTime nginxLogCostTime) {

        nginxLogCostTime.setTime(DateTimeUtils.getIntervalBasicTime(nginxLogCostTime.getTime()).getTime());
        //目前统一服务器，以后会分nginx服务器，即:IP+endpoint
        String pushEndpoint = endpoint;
        String metric = nginxLogCostTime.getEvent();
        long timestamp = nginxLogCostTime.getTime();
        float value = nginxLogCostTime.getCostTime();
        logger.info("ready to push -- " + nginxLogCostTime.toString());
        pushToFalconUtils.sendInfoToFalcon(pushEndpoint, metric, timestamp, step, value, counterType, tags);
    }

}

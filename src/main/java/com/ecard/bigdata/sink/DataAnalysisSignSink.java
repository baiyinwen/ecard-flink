package com.ecard.bigdata.sink;

import com.ecard.bigdata.model.DataAnalysisSignMin;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.DateTimeUtils;
import com.ecard.bigdata.utils.PushToFalconUtils;
import com.ecard.bigdata.utils.TBaseUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/24 14:59
 * @Version 1.0
 **/
public class DataAnalysisSignSink extends RichSinkFunction<DataAnalysisSignMin> {

    private static Logger logger = LoggerFactory.getLogger(DataAnalysisSignSink.class);

    private TBaseUtils tBaseUtils;
    private PushToFalconUtils pushToFalconUtils;

    private static String endpoint;
    private static int step;
    private static String counterType;
    private static String tags;

    @Override
    public void open(Configuration parameters) throws Exception {
        tBaseUtils = TBaseUtils.getInstance();
        pushToFalconUtils = new PushToFalconUtils();

        endpoint = ConfigUtils.getString(CONFIGS.SIGN_OPEN_FALCON_ENDPOINT);
        step = ConfigUtils.getInteger(CONFIGS.SIGN_OPEN_FALCON_STEP);
        counterType = ConfigUtils.getString(CONFIGS.SIGN_OPEN_FALCON_COUNTER_TYPE);
        tags = ConfigUtils.getString(CONFIGS.SIGN_OPEN_FALCON_TAGS);

        logger.info("调用open --- ");
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        logger.info("调用close --- ");
        super.close();
    }

    @Override
    public void invoke(DataAnalysisSignMin dataAnalysisSignMin, Context context) {

        saveDataAnalysisSignMin(dataAnalysisSignMin);
        pushDataAnalysisSignMin(dataAnalysisSignMin);
    }

    private void saveDataAnalysisSignMin(DataAnalysisSignMin dataAnalysisSignMin) {

        dataAnalysisSignMin.setCollectTime(DateTimeUtils.getIntervalBasicTime(dataAnalysisSignMin.getCollectTime().getTime()));
        String sql = "INSERT INTO data_analysis_sign_min(COLLECT_TIME, TRANSFER_TIMES)" +
                " VALUES(?, ?) ";
        Object[] params = new Object[]{
                dataAnalysisSignMin.getCollectTime(),
                dataAnalysisSignMin.getTransferTimes()};
        logger.info("ready to save -- " + dataAnalysisSignMin.toString());
        tBaseUtils.executeUpdate(sql, params);
    }

    private void pushDataAnalysisSignMin(DataAnalysisSignMin dataAnalysisSignMin) {

        String metric = CONSTANTS.EVENT_ESSC_LOG_SIGN;
        long timestamp = dataAnalysisSignMin.getCollectTime().getTime();
        float value = dataAnalysisSignMin.getTransferTimes();
        logger.info("ready to push -- " + dataAnalysisSignMin.toString());
        pushToFalconUtils.sendInfoToFalcon(endpoint, metric, timestamp, step, value, counterType, tags);
    }

}

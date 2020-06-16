package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.DataAnalysisSignAmount;
import com.ecard.bigdata.utils.PushToFalconUtils;
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
public class SignAmountAlterSink extends RichSinkFunction<DataAnalysisSignAmount> {

    private static Logger logger = LoggerFactory.getLogger(SignAmountAlterSink.class);

    private PushToFalconUtils pushToFalconUtils;

    private static String endpoint = "endpoint_data_sign_alter";
    private static int step = 60;
    private static String counterType = "GAUGE";
    private static String tags = "type=sign,value=amount";

    @Override
    public void open(Configuration parameters) throws Exception {
        pushToFalconUtils = new PushToFalconUtils();
        logger.info("调用open --- ");
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        logger.info("调用close --- ");
        super.close();
    }

    @Override
    public void invoke(DataAnalysisSignAmount dataAnalysisSignAmount, Context context) {

        pushDataAnalysisSignMin(dataAnalysisSignAmount);
    }

    private void pushDataAnalysisSignMin(DataAnalysisSignAmount dataAnalysisSignAmount) {

        String metric = CONSTANTS.EVENT_ESSC_LOG_SIGN;
        long timestamp = dataAnalysisSignAmount.getCollectTime().getTime();
        float value = dataAnalysisSignAmount.getTransferTimes();
        logger.info("ready to push -- " + dataAnalysisSignAmount.toString());
        pushToFalconUtils.sendInfoToFalcon(endpoint, metric, timestamp, step, value, counterType, tags);
    }

}

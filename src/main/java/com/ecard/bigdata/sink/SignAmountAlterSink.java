package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.SignAmount;
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
 * @Date 2020/4/24 14:59
 * @Version 1.0
 **/
public class SignAmountAlterSink extends RichSinkFunction<SignAmount> {

    private static Logger logger = LoggerFactory.getLogger(SignAmountAlterSink.class);

    private PushToFalconUtils pushToFalconUtils;

    private static String endpoint = "endpoint_data_sign_alter";
    private static int step = 60;
    private static String counterType = "GAUGE";
    private static String tags = "type=sign_alter,value=amount";

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
    public void invoke(SignAmount signAmount, Context context) {

        pushSignAmountAlter(signAmount);
    }

    private void pushSignAmountAlter(SignAmount signAmount) {

        signAmount.setCollectTime(DateTimeUtils.getIntervalBasicTime(signAmount.getCollectTime().getTime(), ConfigUtils.getLong(CONFIGS.SIGN_ALTER_TUMBLING_WINDOW_SIZE)));
        String metric = CONSTANTS.EVENT_ESSC_LOG_SIGN;
        long timestamp = signAmount.getCollectTime().getTime();
        float value = signAmount.getTransferTimes();
        logger.info("ready to push -- " + signAmount.toString());
        pushToFalconUtils.sendInfoToFalcon(endpoint, metric, timestamp, step, value, counterType, tags);
    }

}

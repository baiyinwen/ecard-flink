package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.InfoVerifyAmount;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.DateTimeUtils;
import com.ecard.bigdata.utils.TBaseUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/12/3 16:44
 * @Version 1.0
 **/
public class InfoVerifyAmountCountSink extends RichSinkFunction<InfoVerifyAmount> {

    private static Logger logger = LoggerFactory.getLogger(InfoVerifyAmountCountSink.class);

    private TBaseUtils tBaseUtils;

    @Override
    public void open(Configuration parameters) throws Exception {
        tBaseUtils = TBaseUtils.getInstance();
        logger.info("调用open --- ");
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        logger.info("调用close --- ");
        super.close();
    }

    @Override
    public void invoke(InfoVerifyAmount infoVerifyAmount, Context context) {

        saveInfoVerifyAmountCount(infoVerifyAmount);
    }

    private void saveInfoVerifyAmountCount(InfoVerifyAmount infoVerifyAmount) {

        infoVerifyAmount.setCollectTime(DateTimeUtils.getIntervalBasicTime(infoVerifyAmount.getCollectTime().getTime(), ConfigUtils.getLong(CONFIGS.INFO_VERIFICATION_COUNT_TUMBLING_WINDOW_SIZE)));
        String sql = "INSERT INTO info_verification_count_min(COLLECT_TIME, EVENT, APP_KEY, TRANSFER_TIMES)" +
                " VALUES(?, ?, ?, ?) ";
        Object[] params = new Object[]{
                infoVerifyAmount.getCollectTime(),
                infoVerifyAmount.getEvent(),
                infoVerifyAmount.getAppKey(),
                infoVerifyAmount.getTransferTimes()};
        logger.info("ready to save -- " + infoVerifyAmount.toString());
        tBaseUtils.executeUpdate(sql, params);
    }

}

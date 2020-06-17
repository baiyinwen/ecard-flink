package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.SignAmount;
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
 * @Date 2020/4/24 14:59
 * @Version 1.0
 **/
public class SignAmountCountSink extends RichSinkFunction<SignAmount> {

    private static Logger logger = LoggerFactory.getLogger(SignAmountCountSink.class);

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
    public void invoke(SignAmount signAmount, Context context) {

        saveSignAmountCount(signAmount);
    }

    private void saveSignAmountCount(SignAmount signAmount) {

        signAmount.setCollectTime(DateTimeUtils.getIntervalBasicTime(signAmount.getCollectTime().getTime(), ConfigUtils.getLong(CONFIGS.SIGN_COUNT_TUMBLING_WINDOW_SIZE)));
        String sql = "INSERT INTO data_analysis_sign_min(COLLECT_TIME, CHANNEL_NO, CARD_REGION_CODE, TRANSFER_TIMES)" +
                " VALUES(?, ?, ?, ?) ";
        Object[] params = new Object[]{
                signAmount.getCollectTime(),
                signAmount.getChannelNo(),
                signAmount.getCardRegionCode(),
                signAmount.getTransferTimes()};
        logger.info("ready to save -- " + signAmount.toString());
        tBaseUtils.executeUpdate(sql, params);
    }

}

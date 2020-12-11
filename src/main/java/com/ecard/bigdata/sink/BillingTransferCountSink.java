package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.BillingTransfer;
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
 * @Date 2020/9/14 15:10
 * @Version 1.0
 **/
public class BillingTransferCountSink extends RichSinkFunction<BillingTransfer> {

    private static Logger logger = LoggerFactory.getLogger(BillingTransferCountSink.class);

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
    public void invoke(BillingTransfer billingTransfer, Context context) {

        saveBillingTransferCount(billingTransfer);
    }

    private void saveBillingTransferCount(BillingTransfer billingTransfer) {

        billingTransfer.setCollectTime(DateTimeUtils.getIntervalBasicTime(billingTransfer.getCollectTime().getTime(), ConfigUtils.getLong(CONFIGS.BILLING_TRANSFER_TUMBLING_WINDOW_SIZE)));
        String sql = "INSERT INTO billing_transfer_count_min(COLLECT_TIME, EVENT, CHANNEL_NO, TRANSFER_TIMES) VALUES(?, ?, ?, ?) ";
        Object[] params = new Object[]{
                billingTransfer.getCollectTime(),
                billingTransfer.getEvent(),
                billingTransfer.getChannelNo(),
                billingTransfer.getTransferTimes()};
        logger.info("ready to save -- " + billingTransfer.toString());
        tBaseUtils.executeUpdate(sql, params);
    }

}

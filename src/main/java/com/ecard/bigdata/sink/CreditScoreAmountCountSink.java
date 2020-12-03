package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.CreditScoreAmount;
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
 * @Date 2020/12/1 16:44
 * @Version 1.0
 **/
public class CreditScoreAmountCountSink extends RichSinkFunction<CreditScoreAmount> {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreAmountCountSink.class);

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
    public void invoke(CreditScoreAmount creditScoreAmount, Context context) {

        saveCreditScoreAmountCount(creditScoreAmount);
    }

    private void saveCreditScoreAmountCount(CreditScoreAmount creditScoreAmount) {

        creditScoreAmount.setCollectTime(DateTimeUtils.getIntervalBasicTime(creditScoreAmount.getCollectTime().getTime(), ConfigUtils.getLong(CONFIGS.CREDIT_SCORE_COUNT_TUMBLING_WINDOW_SIZE)));
        String sql = "INSERT INTO credit_score_count_min(COLLECT_TIME, EVENT, APP_KEY, TRANSFER_TIMES)" +
                " VALUES(?, ?, ?, ?) ";
        Object[] params = new Object[]{
                creditScoreAmount.getCollectTime(),
                creditScoreAmount.getEvent(),
                creditScoreAmount.getAppKey(),
                creditScoreAmount.getTransferTimes()};
        logger.info("ready to save -- " + creditScoreAmount.toString());
        tBaseUtils.executeUpdate(sql, params);
    }

}

package com.ecard.bigdata.sink;

import com.ecard.bigdata.model.DataAnalysisSignAmount;
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
public class DataAnalysisSignSink extends RichSinkFunction<DataAnalysisSignAmount> {

    private static Logger logger = LoggerFactory.getLogger(DataAnalysisSignSink.class);

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
    public void invoke(DataAnalysisSignAmount dataAnalysisSignAmount, Context context) {

        saveDataAnalysisSignMin(dataAnalysisSignAmount);
    }

    private void saveDataAnalysisSignMin(DataAnalysisSignAmount dataAnalysisSignAmount) {

        dataAnalysisSignAmount.setCollectTime(DateTimeUtils.getIntervalBasicTime(dataAnalysisSignAmount.getCollectTime().getTime()));
        String sql = "INSERT INTO data_analysis_sign_min(COLLECT_TIME, CHANNEL_NO, CARD_REGION_CODE, TRANSFER_TIMES)" +
                " VALUES(?, ?, ?, ?) ";
        Object[] params = new Object[]{
                dataAnalysisSignAmount.getCollectTime(),
                dataAnalysisSignAmount.getChannelNo(),
                dataAnalysisSignAmount.getCardRegionCode(),
                dataAnalysisSignAmount.getTransferTimes()};
        logger.info("ready to save -- " + dataAnalysisSignAmount.toString());
        tBaseUtils.executeUpdate(sql, params);
    }

}

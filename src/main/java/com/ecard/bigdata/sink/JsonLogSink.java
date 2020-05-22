package com.ecard.bigdata.sink;

import com.ecard.bigdata.bean.DataAnalysisSignMin;
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
public class JsonLogSink extends RichSinkFunction<DataAnalysisSignMin> {

    private static Logger logger = LoggerFactory.getLogger(JsonLogSink.class);

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
    public void invoke(DataAnalysisSignMin dataAnalysisSignMin, Context context) {

        dataAnalysisSignMin.setCollectTime(DateTimeUtils.getIntervalBasicTime(dataAnalysisSignMin.getCollectTime()));
        String sql = "INSERT INTO data_analysis_sign_min(COLLECT_TIME, TRANSFER_TIMES)" +
                " VALUES(?, ?) ";
        Object[] params = new Object[]{
                dataAnalysisSignMin.getCollectTime(),
                dataAnalysisSignMin.getTransferTimes()};
        logger.info("保存数据到TBase -- " + dataAnalysisSignMin.toString());
        tBaseUtils.executeUpdate(sql, params);
    }

}

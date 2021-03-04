package com.ecard.bigdata.sink;

import com.ecard.bigdata.model.SignDetail;
import com.ecard.bigdata.utils.TBaseUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author Wanghongdong
 * @Date 2021/02/07 14:59implements SinkFunction<SignDetail>
 * @Version 1.0
 **/
public class SignDetailSink extends RichSinkFunction<SignDetail> {

    private static Logger logger = LoggerFactory.getLogger(SignDetailSink.class);

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
    public void invoke(SignDetail signDetail, Context context) {

        saveSignDetail(signDetail);
    }

    private void saveSignDetail(SignDetail signDetail) {

        String sql = "INSERT INTO data_analysis_sign_detail(COLLECT_TIME, CHANNEL_NO, CARD_REGION_CODE, aac002)" +
                " VALUES(?, ?, ?, ?) ";
        Object[] params = new Object[]{
                signDetail.getCollectTime(),
                signDetail.getChannelNo(),
                signDetail.getCardRegionCode(),
                signDetail.getAac002()
        };
        logger.info("ready to save -- " + signDetail.toString());
        tBaseUtils.executeUpdate(sql, params);
    }
}
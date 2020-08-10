package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.CreditScore;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.HBaseUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/7/31 18:11
 * @Version 1.0
 **/
public class CreditScoreMoveSink extends RichSinkFunction<List<CreditScore>> {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreMoveSink.class);
    private static HBaseUtils hBaseUtils;

    @Override
    public void open(Configuration parameters) throws Exception {

        logger.info("调用open --- ");
        super.open(parameters);
        String tableName = ConfigUtils.getString(CONFIGS.CREDIT_SCORE_HBASE_TABLE);
        hBaseUtils = new HBaseUtils(tableName);
    }

    @Override
    public void close() throws Exception {

        logger.info("调用close --- ");
        super.close();
        hBaseUtils.close();
    }

    @Override
    public void invoke(List<CreditScore> list, Context context) {

        try {
            String family = "data";
            for (CreditScore creditScore: list) {
                String id = creditScore.getCreditID() == null ? "" : creditScore.getCreditID();
                String jssjc = creditScore.getTime() == null ? "" : creditScore.getTime();
                String score = creditScore.getScore() == null ? "" : creditScore.getScore();
                Put put = new Put(id.getBytes());
                put.addColumn(family.getBytes(), "jssjc".getBytes(), jssjc.getBytes());
                put.addColumn(family.getBytes(), "score".getBytes(), score.getBytes());
                hBaseUtils.putData(put);
            }
            hBaseUtils.flush();
            logger.info("save data to hbase");
        } catch (Exception e) {
            logger.error(list.toString());
            e.printStackTrace();
        }
    }

}

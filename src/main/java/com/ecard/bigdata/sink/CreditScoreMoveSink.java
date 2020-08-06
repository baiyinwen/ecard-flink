package com.ecard.bigdata.sink;

import com.ecard.bigdata.model.CreditScore;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/7/31 18:11
 * @Version 1.0
 **/
public class CreditScoreMoveSink extends RichSinkFunction<CreditScore> {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreMoveSink.class);
    private static org.apache.hadoop.conf.Configuration configuration;
    private static Connection connection = null;
    private static BufferedMutator mutator;

    private static boolean IS_END = false;

    private static final long WRITE_SIZE = 20 * 1024 * 1024;

    private static final int MAX_COUNT = 300000;
    private static int count = 0;

    @Override
    public void open(Configuration parameters) throws Exception {

        logger.info("调用open --- ");
        super.open(parameters);

        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "172.29.7.38,172.29.7.39,172.29.7.40");
        configuration.set("zookeeper.znode.parent", "/hbase-unsecure");
        // 认证参数
        configuration.set("hbase.security.authentication.tbds.secureid", "HbdxHiaEXI1fcvRg42RfhYUs0OjFjyyWZzlI");
        configuration.set("hbase.security.authentication.tbds.securekey", "R4G24ndP30OM5NJJn0fhbV45efU9ZoSa");

        connection = ConnectionFactory.createConnection(configuration);

        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf("creditScore:jbx_test"));
        params.writeBufferSize(WRITE_SIZE);
        mutator = connection.getBufferedMutator(params);
    }

    @Override
    public void close() throws Exception {

        logger.info("调用close --- ");
        super.close();

        if (null != mutator) {
            mutator.close();
        }
        if (null != connection) {
            connection.close();
        }
    }

    @Override
    public void invoke(CreditScore creditScore, Context context) {

        try {
            String family = "data";
            String id = creditScore.getCreditID() == null ? "" : creditScore.getCreditID();
            String jssjc = creditScore.getTime() == null ? "" : creditScore.getTime();
            String score = creditScore.getScore() == null ? "" : creditScore.getScore();
            if (!"end".equals(id)) {
                Put put = new Put(id.getBytes());
                put.addColumn(family.getBytes(), "jssjc".getBytes(), jssjc.getBytes());
                put.addColumn(family.getBytes(), "score".getBytes(), score.getBytes());
                mutator.mutate(put);
            } else {
                IS_END = true;
            }
            count = count + 1;
            //满足条件刷新一下数据
            if (count >= MAX_COUNT || IS_END){
                mutator.flush();
                count = 0;
                IS_END = false;
                logger.info("save data to hbase");
            }
            if (IS_END) {
                logger.info("信用评分数据迁移结束！");
            }
        } catch (IOException e) {
            logger.error(creditScore.toString());
            e.printStackTrace();
        }
    }

}

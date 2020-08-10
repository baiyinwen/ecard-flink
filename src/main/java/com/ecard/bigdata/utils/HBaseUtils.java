package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/8/7 11:17
 * @Version 1.0
 **/
public class HBaseUtils {

    private static Configuration configuration;
    private static final long WRITE_SIZE = 20 * 1024 * 1024;

    private Connection connection = null;
    private BufferedMutator mutator;

    static {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", ConfigUtils.getString(CONFIGS.ZOOKEEPER_SERVERS));
        configuration.set("zookeeper.znode.parent", ConfigUtils.getString(CONFIGS.HBASE_ZK_ZNODE_PARENT));
        // 认证参数
        if (ConfigUtils.getBoolean(CONFIGS.HBASE_TBDS_SECURE_ENABLE)) {
            configuration.set("hbase.security.authentication.tbds.secureid", ConfigUtils.getString(CONFIGS.HBASE_TBDS_SECURE_ID));
            configuration.set("hbase.security.authentication.tbds.securekey", ConfigUtils.getString(CONFIGS.HBASE_TBDS_SECURE_KEY));
        }
    }

    public HBaseUtils(String tableName) {

        try {
            connection = ConnectionFactory.createConnection(configuration);
            BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tableName));
            params.writeBufferSize(WRITE_SIZE);
            mutator = connection.getBufferedMutator(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putData(Put put) {
        try {
            mutator.mutate(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            mutator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (null != mutator) {
                mutator.close();
            }
            if (null != connection) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

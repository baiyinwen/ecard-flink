package com.ecard.bigdata.utils;


import com.alibaba.druid.pool.DruidDataSource;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description mysql操作工具类
 * @Author WangXuedong
 * @Date 2019/9/20 10:50
 * @Version 1.0
 **/
public class TBaseUtils {

    private static Logger logger = LoggerFactory.getLogger(TBaseUtils.class);

    private static DruidDataSource druidDataSource = new DruidDataSource();
    private static TBaseUtils instance = null;

    private static String TBASE_JDBC_URL;
    private static String TBASE_JDBC_USER;
    private static String TBASE_JDBC_PASSWORD;

    private static int TBASE_JDBC_INITIAL_SIZE = 10;
    private static int TBASE_JDBC_MAX_ACTIVE = 16;
    private static int TBASE_JDBC_MIN_IDLE = 5;
    private static int TBASE_JDBC_MAX_WAIT = 100;

    static {
        try {
            Class.forName(CONSTANTS.TBASE_JDBC_DRIVER);

            TBASE_JDBC_URL= ConfigUtils.getString(CONFIGS.TBASE_JDBC_URL);
            TBASE_JDBC_USER = ConfigUtils.getString(CONFIGS.TBASE_JDBC_USER);
            TBASE_JDBC_PASSWORD = ConfigUtils.getString(CONFIGS.TBASE_JDBC_PD);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

    private TBaseUtils() {

        druidDataSource.setUrl(TBASE_JDBC_URL);
        druidDataSource.setUsername(TBASE_JDBC_USER);
        druidDataSource.setPassword(TBASE_JDBC_PASSWORD);

        druidDataSource.setInitialSize(TBASE_JDBC_INITIAL_SIZE);
        druidDataSource.setMaxActive(TBASE_JDBC_MAX_ACTIVE);
        druidDataSource.setMinIdle(TBASE_JDBC_MIN_IDLE);
        druidDataSource.setMaxWait(TBASE_JDBC_MAX_WAIT);
    }

    public static synchronized TBaseUtils getInstance() {

        if(instance == null) {
            instance = new TBaseUtils();
        }
        return instance;
    }

    private synchronized Connection getConnection() {

        Connection connection = null;
        try {
            connection = druidDataSource.getConnection();
//                if (null == connection) {
////                Thread.sleep(30);
//                    connection.wait(30);
//                    connection = druidDataSource.getConnection();
//                }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
//        logger.info("get connection -- " + connection);
        return connection;
    }

    private static void closeConnection(Connection connection) {

        try {
            if (null != connection) {
                connection.close();
            }
        } catch (SQLException throwables) {
            logger.error(throwables.getMessage());
        }
    }

    public void executeQuery(String sql, Object[] params, QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            if (conn != null){
             pst = conn.prepareStatement(sql);
            if(params != null && params.length > 0 && pst !=null) {
                if (paramsEnough(sql, params)){
                    for(int i = 0; i < params.length; i++) {
                        pst.setObject(i + 1, params[i]);
                    }
                }
            }
            rs = pst.executeQuery();
            }
            callback.process(rs);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
            if(conn != null) {
                closeConnection(conn);
            }
        }
        logger.info("executeQuery --- " + sql);
    }

    public int executeUpdate(String sql, Object[] params) {
        int rtn = 0;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = getConnection();
            if (conn != null){
                conn.setAutoCommit(false);
                pst = conn.prepareStatement(sql);
                if(params != null && params.length > 0 && pst != null) {
                    if (paramsEnough(sql, params)) {
                        for(int i = 0; i < params.length; i++) {
                            pst.setObject(i + 1, params[i]);
                        }
                    }
                }
            }
            rtn = pst.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
            if(conn != null) {
                closeConnection(conn);
            }
        }
        logger.info("executeUpdate --- " + sql + "; result --- " + rtn);
        return rtn;
    }

    public int[] executeBatch(String sql, List<Object[]> paramsList) {
        int[] rtn = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = getConnection();
            if (conn != null){
                conn.setAutoCommit(false);
                pst = conn.prepareStatement(sql);
            }
            if(paramsList != null && paramsList.size() > 0 && pst != null) {
                for(Object[] params : paramsList) {
                    if(params != null && params.length > 0) {
                        if (paramsEnough(sql, params)) {
                            for(int i = 0; i < params.length; i++) {
                                pst.setObject(i + 1, params[i]);
                            }
                            pst.addBatch();
                        }
                    }
                }
            }
            rtn = pst.executeBatch();
            conn.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
            if(conn != null) {
                closeConnection(conn);
            }
        }
        logger.info("executeBatch --- " + sql + "; result --- " + rtn);
        return rtn;
    }

    private boolean paramsEnough(String sql, Object[] params) {

        int count = sql.split("\\?").length - 1;
        return count == params.length;
    }

    public interface QueryCallback {

        void process(ResultSet rs) throws Exception;
    }

}

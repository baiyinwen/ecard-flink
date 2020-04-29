package com.ecard.bigdata.utils;


import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description mysql操作工具类
 * @Author WangXuedong
 * @Date 2019/9/20 10:50
 * @Version 1.0
 **/
public class TBaseUtils {

    private static Logger logger = LoggerFactory.getLogger(TBaseUtils.class);
    private LinkedList<Connection> dataSource = new LinkedList<Connection>();
    private static TBaseUtils instance = null;

    static {
        try {
            Class.forName(CONSTANTS.TBASE_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private TBaseUtils() {

        int dataSourceSize = ConfigUtil.getInteger(CONFIGS.TBASE_JDBC_DATASOURCE_SIZE);
        for(int i = 0; i < dataSourceSize; i++) {
            String url = ConfigUtil.getString(CONFIGS.TBASE_JDBC_URL);
            String user = ConfigUtil.getString(CONFIGS.TBASE_JDBC_USER);
            String password = ConfigUtil.getString(CONFIGS.TBASE_JDBC_PASSWORD);
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                dataSource.push(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static TBaseUtils getInstance() {

        if(instance == null) {
            synchronized(TBaseUtils.class) {
                if(instance == null) {
                    instance = new TBaseUtils();
                }
            }
        }
        return instance;
    }

    private synchronized Connection getConnection() {
        while(dataSource.size() == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("get connection ---");
        return dataSource.poll();
    }

    public void executeQuery(String sql, Object[] params, QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pst = conn.prepareStatement(sql);
            if(params != null && params.length > 0) {
                if (paramsEnough(sql, params)){
                    for(int i = 0; i < params.length; i++) {
                        pst.setObject(i + 1, params[i]);
                    }
                }
            }
            rs = pst.executeQuery();
            callback.process(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                dataSource.push(conn);
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
            conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            if(params != null && params.length > 0) {
                if (paramsEnough(sql, params)) {
                    for(int i = 0; i < params.length; i++) {
                        pst.setObject(i + 1, params[i]);
                    }
                }
            }
            rtn = pst.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                dataSource.push(conn);
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
            conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            if(paramsList != null && paramsList.size() > 0) {
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
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                dataSource.push(conn);
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

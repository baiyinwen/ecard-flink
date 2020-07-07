package com.ecard.bigdata.constants;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/13 10:42
 * @Version 1.0
 **/
public class CONSTANTS {

    public final static String ECARD_FLINK_CONFIG_FILE = "ecard-flink.properties";
    public final static String APPLICATION_CONFIG_FILE = "application.properties";

    public final static String TBASE_JDBC_DRIVER = "org.postgresql.Driver";

    public final static String DATE_TIME_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_TIME_FORMAT_2 = "yyyyMMddHHmmss";
    public final static String DATE_FORMAT_1 = "yyyy-MM-dd";
    public final static String DATE_FORMAT_2 = "yyyyMMdd";
    public final static String TIME_FORMAT_1 = "HH:mm:ss";
    public final static String TIME_FORMAT_2 = "HHmmss";

    public final static int NUMBER_0 = 0;
    public final static int NUMBER_1 = 1;

    public final static String EVENT_MSG_CODE_KEY = "msgCode";
    public final static String EVENT_MSG_CODE_VALUE = "000000";
    public final static String EVENT_ESSC_LOG_SIGN = "essc_log_sign";//签发日志的event
    public final static String EVENT_ESSC_LOG_SIGN_AAB_301 = "aab301";//签发日志中签发地区行政划分码
    public final static String EVENT_ESSC_LOG_SIGN_SIGN_SEQ = "signSeq";//后六位签发日志中签发地区行政划分码

    public final static String NGINX_STATUS_SUCCESS_VALUE = "200";

    public final static String SIGN_REDIS_LOG_COUNT_MD5_KEY = "signLogCountMd5";
    public final static int SIGN_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS = 86400;
    public final static String SIGN_REDIS_LOG_ALTER_MD5_KEY = "signLogAlterMd5";
    public final static int SIGN_REDIS_LOG_ALTER_KEY_EXPIRE_SECONDS = 86400;
    public final static String COST_TIME_REDIS_LOG_MD5_KEY = "costTimeLogMd5";
    public final static int COST_TIME_REDIS_LOG_KEY_EXPIRE_SECONDS = 86400;

}

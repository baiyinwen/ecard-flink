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

    public final static String TBASE_JDBC_DRIVER = "org.postgresql.Driver";//TBase数据库驱动

    public final static String DATE_TIME_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_TIME_FORMAT_2 = "yyyyMMddHHmmss";
    public final static String DATE_FORMAT_1 = "yyyy-MM-dd";
    public final static String DATE_FORMAT_2 = "yyyyMMdd";
    public final static String TIME_FORMAT_1 = "HH:mm:ss";
    public final static String TIME_FORMAT_2 = "HHmmss";

    public final static int NUMBER_0 = 0;
    public final static int NUMBER_1 = 1;

    public final static String EVENT_ESSC_LOG_SIGN = "essc_log_sign";//ecard日志签发日志的event
    public final static String EVENT_ESSC_LOG2_SIGN_PERSON = "essc_log2_sign_person";//ecard日志签发日志的event
    public final static String EVENT_ESSC_LOG_SIGN_ONE_STEP = "essc_log_sign_add_sign";//一键签发日志的event
    public final static String EVENT_ESSC_LOG2_SIGN_ONE_STEP = "essc_log2_sign_add_sign";//一键签发日志的event
    public final static String EVENT_ESSC_LOG_PHOTO = "essc_log2_service_page_photo";//照片拉取

    public final static String EVENT_MSG_CODE_KEY = "msgCode";//ecard日志调用结果key
    public final static String EVENT_MSG_CODE_VALUE = "000000";//ecard日志调用结果value
    public final static String EVENT_ESSC_LOG_SIGN_AAB_301 = "aab301";//ecard日志签发日志中签发地区行政划分码
    public final static String EVENT_ESSC_LOG_CARD_REGIONNO = "signCardRegionNo";//ecard日志签发日志中签发地区行政划分码
    public final static String EVENT_ESSC_LOG_SIGN_SIGN_SEQ = "signSeq";//ecard日志后六位签发日志中签发地区行政划分码
    public final static String EVENT_ESSC_LOG_SIGN_AAC002 = "aac002";//ecard日志身份证号
    public final static String EVENT_ESSC_LOG_INPUT = "input";//INPUT日志
    public final static String EVENT_ESSC_LOG_OUTPUT = "output";//outPUT日志
    public final static String EVENT_ESSC_LOG_RESULT = "result";//result日志

    public final static String NGINX_STATUS_SUCCESS_VALUE = "200";//nginx日志调用成功码

    /*签发量统计redis去重前缀及过期时间*/
    public final static String SIGN_REDIS_LOG_COUNT_MD5_KEY = "signLogCountMd5";
    public final static int SIGN_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS = 86400;

    /*签发量报警redis去重前缀及过期时间*/
    public final static String SIGN_REDIS_LOG_ALTER_MD5_KEY = "signLogAlterMd5";
    public final static int SIGN_REDIS_LOG_ALTER_KEY_EXPIRE_SECONDS = 86400;

    /*签发明细redis去重前缀及过期时间*/
    public final static String SIGN_REDIS_LOG_DETAIL_MD5_KEY = "signLogDetailMd5";
    public final static int SIGN_REDIS_LOG_DETAIL_KEY_EXPIRE_SECONDS = 3600;

    /*接口调用时长redis去重前缀及过期时间*/
    public final static String COST_TIME_REDIS_LOG_MD5_KEY = "costTimeLogMd5";
    public final static int COST_TIME_REDIS_LOG_KEY_EXPIRE_SECONDS = 86400;

    /*计费调用次数redis去重前缀及过期时间*/
    public final static String BILLING_REDIS_LOG_COUNT_MD5_KEY = "billingTransferCountMd5";
    public final static int BILLING_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS = 86400;

    /*信用分调用次数redis去重前缀阶过期时间*/
    public final static String CREDIT_SCORE_REDIS_LOG_COUNT_MD5_KEY = "creditScoreLogCountMd5";
    public final static int CREDIT_SCORE_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS = 86400;

    /*电子社保卡信息核验调用次数redis去重前缀阶过期时间*/
    public final static String INFO_VERIFICATION_REDIS_LOG_COUNT_MD5_KEY = "infoVerificationLogCountMd5";
    public final static int INFO_VERIFICATION_REDIS_LOG_COUNT_KEY_EXPIRE_SECONDS = 86400;

    /*展验码统计量报警redis去重前缀及过期时间*/
    public final static String QRCODE_REDIS_LOG_ALARM_MD5_KEY = "qrCodeLogAlarmMd5";
    public final static int QRCODE_REDIS_LOG_ALARM_KEY_EXPIRE_SECONDS = 3600;

    /*照片拉取量占比签发量报警redis去重前缀及过期时间*/
    public final static String PHOTO_RATIO_SIGN_REDIS_LOG_ALARM_MD5_KEY = "photoRatioSignLogAlarmMd5";
    public final static int PHOTO_RATIO_SIGN_REDIS_LOG_ALARM_KEY_EXPIRE_SECONDS = 3600;

}

package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONSTANTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description properties配置获取工具类
 * @Author WangXuedong
 * @Date 2019/9/20 10:52
 * @Version 1.0
 **/
public class ConfigUtils {

    private static Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

    private static Properties prop = new Properties();

    static {
        try {
            //读取resources资源文件下配置文件
            InputStream in1 = ConfigUtils.class.getClassLoader().getResourceAsStream(CONSTANTS.ECARD_FLINK_CONFIG_FILE);
            InputStream in2 = ConfigUtils.class.getClassLoader().getResourceAsStream(CONSTANTS.APPLICATION_CONFIG_FILE);
            prop.load(in1);
            prop.load(in2);
            logger.info("load config.properties --- " + prop.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getProperty(String key) {

        if (prop.containsKey(key)) {
            return prop.getProperty(key);
        }
        return "";
    }

    public static boolean haveKey(String key) {

        return prop.containsKey(key);
    }

    public static String getString(String key) {

        return getProperty(key);
    }

    public static Integer getInteger(String key) {

        String value = getProperty(key);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Long getLong(String key) {

        String value = getProperty(key);
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static Double getDouble(String key) {

        String value = getProperty(key);
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0d;
    }

    public static Boolean getBoolean(String key) {

        String value = getProperty(key);
        try {
            return Boolean.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}

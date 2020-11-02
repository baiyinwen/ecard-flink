package com.ecard.bigdata.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/6/22 11:07
 * @Version 1.0
 **/
public class JsonUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static boolean isJsonObject(String str){
        try {
            JSONObject.parseObject(str);
        } catch (Exception e) {
            logger.error(str);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isValidObject(String str){
        try {
            JSONObject.isValidObject(str);
        } catch (Exception e) {
            logger.error(str);
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

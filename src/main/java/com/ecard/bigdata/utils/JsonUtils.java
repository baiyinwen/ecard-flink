package com.ecard.bigdata.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/6/22 11:07
 * @Version 1.0
 **/
public class JsonUtils {

    public static boolean isJsonObject(String str){
        try {
            JSONObject.parseObject(str);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

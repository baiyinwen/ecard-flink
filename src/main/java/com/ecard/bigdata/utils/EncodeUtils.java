package com.ecard.bigdata.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2019/12/17 13:48
 * @Version 1.0
 **/
public class EncodeUtils {

    public static String md5Encode(String data) {

        String result = DigestUtils.md5Hex(data).toUpperCase();
        return result;
    }

    public static String md5Encode(String data, String key) {

        String result = DigestUtils.md5Hex(data + key).toUpperCase();
        return result;
    }

    public static String sha256Encode(String data) {

        String result = DigestUtils.sha256Hex(data).toUpperCase();
        return result;
    }

}

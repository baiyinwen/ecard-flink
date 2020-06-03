package com.ecard.bigdata.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2019/12/17 13:48
 * @Version 1.0
 **/
public class Md5Utils {

    private static Logger logger = LoggerFactory.getLogger(Md5Utils.class);

    public static String encodeMd5_test(String data) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encodeMd5(String data) {

        String result = DigestUtils.md5Hex(data).toUpperCase();
        logger.info(data + "\nencodeMd5: " + result);
        return result;
    }

    public static String encodeMd5(String data, String key) {

        String result = DigestUtils.md5Hex(data + key).toUpperCase();
        return result;
    }

    public static void main(String[] args) {

        System.err.println(encodeMd5("123"));
    }

}

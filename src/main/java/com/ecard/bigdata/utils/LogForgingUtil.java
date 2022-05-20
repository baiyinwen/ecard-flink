package com.ecard.bigdata.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：日志打印-敏感字符处理
 *
 * @author: ydfeng
 * @date: 2022/3/28 15:10
 */
public class LogForgingUtil {

    /**
     * Log Forging漏洞校验
     *
     * @param logs
     * @return
     */
    public static String vaildLog(String logs) {
        List<String> list = new ArrayList<String>();
        list.add("%0d");
        list.add("%0a");
        list.add("%0A");
        list.add("%0D");
        list.add("\r");
        list.add("\n");
        String normalize = Normalizer.normalize(logs, Normalizer.Form.NFKC);
        for (String str : list) {
            normalize = normalize.replace(str, "");
        }
        return normalize;
    }

}

package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.NginxLogInfo;
import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/13 10:49
 * @Version 1.0
 **/
public class NginxLogSchema implements DeserializationSchema<NginxLogInfo>, SerializationSchema<NginxLogInfo> {

    private static Logger logger = LoggerFactory.getLogger(NginxLogSchema.class);

    private static final Gson gson = new Gson();

    private static Pattern ipPattern = Pattern.compile("ipAddress:(.*)$");
    private static Pattern costTimePattern = Pattern.compile("^([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)");
    //private static Pattern timePattern = Pattern.compile("\\[(.*)]");
    private static Pattern eventPattern_post = Pattern.compile("POST(.*)HTTP");
    private static Pattern eventPattern_get = Pattern.compile("GET(.*)\\?");
    private static Pattern codePattern = Pattern.compile("HTTP/\\d{1}.\\d{1}\" (\\d{3}).+");

    @Override
    public NginxLogInfo deserialize(byte[] bytes) {

        String origLog = new String(bytes);
        if (!origLog.isEmpty()) {
            NginxLogInfo nginxLogInfo = parseLogToBean(new String(bytes));
            nginxLogInfo.setOrigLog(origLog);
            return nginxLogInfo;
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(NginxLogInfo nginxLogInfo) {

        return false;
    }

    @Override
    public TypeInformation<NginxLogInfo> getProducedType() {

        return TypeInformation.of(NginxLogInfo.class);
    }

    @Override
    public byte[] serialize(NginxLogInfo nginxLogInfo) {

        return gson.toJson(nginxLogInfo).getBytes(Charset.forName("UTF-8"));
    }

    private NginxLogInfo parseLogToBean(String logLine) {

        String ip = "";
        Matcher matcher = ipPattern.matcher(logLine);
        if (matcher.find() && matcher.groupCount() == 1) {
            ip = matcher.group(1).trim();
        }
        float costTime = -1;
        matcher = costTimePattern.matcher(logLine);
        if (matcher.find() && matcher.groupCount() == 1) {
            costTime = Float.parseFloat(matcher.group(1).trim());
        }
        /*long time = -1;
        matcher = timePattern.matcher(logLine);
        if (matcher.find() && matcher.groupCount() == 1) {
            time = DateTimeUtils.parseDateTime_3(matcher.group(1).trim());
        }*/
        String event = "";
        matcher = eventPattern_post.matcher(logLine);
        if (matcher.find() && matcher.groupCount() == 1) {
            event = matcher.group(1).trim();
        } else {
            matcher = eventPattern_get.matcher(logLine);
            if (matcher.find() && matcher.groupCount() == 1) {
                event = matcher.group(1).trim();
            }
        }
        String code = "";
        matcher = codePattern.matcher(logLine);
        if (matcher.find() && matcher.groupCount() == 1) {
            code = matcher.group(1).trim();
        }
        if ("".equals(ip) || costTime <= 0 || "".equals(event) || "".equals(code)){
            logger.info("got a wrong log --- " + logLine);
            return null;
        }
        NginxLogInfo nginxLogInfo = new NginxLogInfo();
        nginxLogInfo.setIp(ip);
        nginxLogInfo.setEvent(event);
        nginxLogInfo.setCostTime(costTime);
        nginxLogInfo.setCode(code);

        return nginxLogInfo;
    }

}

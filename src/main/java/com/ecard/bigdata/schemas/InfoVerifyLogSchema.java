package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.InfoVerifyLogInfo;
import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/12/03 15:24
 * @Version 1.0
 **/
public class InfoVerifyLogSchema implements DeserializationSchema<InfoVerifyLogInfo>, SerializationSchema<InfoVerifyLogInfo> {

    private static Logger logger = LoggerFactory.getLogger(InfoVerifyLogSchema.class);

    private static final Gson gson = new Gson();

    @Override
    public InfoVerifyLogInfo deserialize(byte[] bytes) {

        String origLog = new String(bytes);
        if (!origLog.isEmpty()) {
            try {
                InfoVerifyLogInfo infoVerifyLogInfo = gson.fromJson(origLog, InfoVerifyLogInfo.class);
                infoVerifyLogInfo.setOrigLog(origLog);
                return infoVerifyLogInfo;
            } catch (Exception e) {
                logger.error("JSON日志转对象异常！" + origLog);
                return null;
            }
        }
        logger.info("is empty");
        return null;
    }

    @Override
    public boolean isEndOfStream(InfoVerifyLogInfo infoVerifyLogInfo) {

        return false;
    }

    @Override
    public TypeInformation<InfoVerifyLogInfo> getProducedType() {

        return TypeInformation.of(InfoVerifyLogInfo.class);
    }

    @Override
    public byte[] serialize(InfoVerifyLogInfo infoVerifyLogInfo) {

        return gson.toJson(infoVerifyLogInfo).getBytes(Charset.forName("UTF-8"));
    }
}

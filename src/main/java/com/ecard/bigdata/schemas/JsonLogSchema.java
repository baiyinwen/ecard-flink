package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.JsonLogInfo;
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
 * @Date 2020/4/13 10:49
 * @Version 1.0
 **/
public class JsonLogSchema implements DeserializationSchema<JsonLogInfo>, SerializationSchema<JsonLogInfo> {

    private static Logger logger = LoggerFactory.getLogger(JsonLogSchema.class);

    private static final Gson gson = new Gson();

    @Override
    public JsonLogInfo deserialize(byte[] bytes) {

        String origLog = new String(bytes);
        if (!origLog.isEmpty()) {
            JsonLogInfo jsonLogInfo = gson.fromJson(origLog, JsonLogInfo.class);
            jsonLogInfo.setOrigLog(origLog);
            return jsonLogInfo;
        }
        logger.info("is empty");
        return null;
    }

    @Override
    public boolean isEndOfStream(JsonLogInfo jsonLogInfo) {

        return false;
    }

    @Override
    public TypeInformation<JsonLogInfo> getProducedType() {

        return TypeInformation.of(JsonLogInfo.class);
    }

    @Override
    public byte[] serialize(JsonLogInfo jsonLogInfo) {

        return gson.toJson(jsonLogInfo).getBytes(Charset.forName("UTF-8"));
    }
}

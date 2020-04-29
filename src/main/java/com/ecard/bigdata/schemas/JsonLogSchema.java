package com.ecard.bigdata.schemas;

import com.ecard.bigdata.model.JsonLog;
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
public class JsonLogSchema implements DeserializationSchema<JsonLog>, SerializationSchema<JsonLog> {

    private static Logger logger = LoggerFactory.getLogger(JsonLogSchema.class);

    private static final Gson gson = new Gson();

    @Override
    public JsonLog deserialize(byte[] bytes) {

        JsonLog jsonLog = gson.fromJson(new String(bytes), JsonLog.class);
        return jsonLog;
    }

    @Override
    public boolean isEndOfStream(JsonLog jsonLog) {

        return false;
    }

    @Override
    public TypeInformation<JsonLog> getProducedType() {

        return TypeInformation.of(JsonLog.class);
    }

    @Override
    public byte[] serialize(JsonLog jsonLog) {

        return gson.toJson(jsonLog).getBytes(Charset.forName("UTF-8"));
    }
}

package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.RiskControlPayLogInfo;
import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/11/24 17:25
 * @Version 1.0
 **/
public class RiskControlPayInSchema implements DeserializationSchema<RiskControlPayLogInfo>, SerializationSchema<RiskControlPayLogInfo> {

    private static final Gson gson = new Gson();

    @Override
    public RiskControlPayLogInfo deserialize(byte[] bytes) {
        String payLog = bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null;
        if (payLog != null) {
            RiskControlPayLogInfo riskControlPayLogInfo = gson.fromJson(payLog, RiskControlPayLogInfo.class);
            return riskControlPayLogInfo;
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(RiskControlPayLogInfo riskControlPayLogInfo) {
        return false;
    }

    @Override
    public byte[] serialize(RiskControlPayLogInfo riskControlPayLogInfo) {
        return gson.toJson(riskControlPayLogInfo).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public TypeInformation<RiskControlPayLogInfo> getProducedType() {
        return TypeInformation.of(RiskControlPayLogInfo.class);
    }
}

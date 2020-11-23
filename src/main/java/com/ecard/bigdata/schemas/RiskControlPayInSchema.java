package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.RiskControlPayLogInfo;
import com.ecard.bigdata.externals.rocketMq.common.serialization.KeyValueDeserializationSchema;
import com.google.gson.Gson;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/11/16 16:25
 * @Version 1.0
 **/
public class RiskControlPayInSchema implements KeyValueDeserializationSchema<RiskControlPayLogInfo> {

    private static final Gson gson = new Gson();

    @Override
    public RiskControlPayLogInfo deserializeKeyAndValue(byte[] key, byte[] value) {

        String k = key != null ? new String(key, StandardCharsets.UTF_8) : null;
        String v = value != null ? new String(value, StandardCharsets.UTF_8) : null;
        if (null != v) {
            RiskControlPayLogInfo riskControlPayLogInfo = gson.fromJson(v, RiskControlPayLogInfo.class);
            riskControlPayLogInfo.setKey(k);
            return riskControlPayLogInfo;
        }
        return null;
    }

    @Override
    public TypeInformation<RiskControlPayLogInfo> getProducedType() {
        return TypeInformation.of(RiskControlPayLogInfo.class);
    }
}

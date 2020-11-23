package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.RiskControlPayLogInfo;
import com.ecard.bigdata.externals.rocketMq.common.serialization.KeyValueSerializationSchema;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/11/16 16:25
 * @Version 1.0
 **/
public class RiskControlPayOutSchema implements KeyValueSerializationSchema<RiskControlPayLogInfo> {

    @Override
    public byte[] serializeKey(RiskControlPayLogInfo riskControlPayLogInfo) {
        return riskControlPayLogInfo.getKey() != null ? riskControlPayLogInfo.getKey().getBytes(StandardCharsets.UTF_8) : null;
    }

    @Override
    public byte[] serializeValue(RiskControlPayLogInfo riskControlPayLogInfo) {
        String ak = riskControlPayLogInfo.getAk() != null ? riskControlPayLogInfo.getAk() : null;
        String essCardNo = riskControlPayLogInfo.getAk() != null ? riskControlPayLogInfo.getEssCardNo() : null;
        String uniformOrderId = riskControlPayLogInfo.getAk() != null ? riskControlPayLogInfo.getUniformOrderId() : null;
        String msg = "{\"ak\":\""+ak+"\", \"essCardNo\":\""+essCardNo+"\", \"uniformOrderId\":\""+uniformOrderId+"\"}";
        return msg.getBytes(StandardCharsets.UTF_8);
    }
}

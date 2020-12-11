package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.RiskControlPayLogInfo;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/11/24 18:25
 * @Version 1.0
 **/
public class RiskControlPayOutSchema implements SerializationSchema<RiskControlPayLogInfo> {

    @Override
    public byte[] serialize(RiskControlPayLogInfo riskControlPayLogInfo) {
        String ak = riskControlPayLogInfo.getAk() != null ? riskControlPayLogInfo.getAk() : "";
        String essCardNo = riskControlPayLogInfo.getAk() != null ? riskControlPayLogInfo.getEssCardNo() : "";
        String uniformOrderId = riskControlPayLogInfo.getAk() != null ? riskControlPayLogInfo.getUniformOrderId() : "";
        String msg = "{\"ak\":\""+ak+"\", \"essCardNo\":\""+essCardNo+"\", \"uniformOrderId\":\""+uniformOrderId+"\"}";
        return msg.getBytes(StandardCharsets.UTF_8);
    }

}

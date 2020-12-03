package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.CreditScoreLogInfo;
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
 * @Date 2020/12/02 10:49
 * @Version 1.0
 **/
public class CreditScoreLogSchema implements DeserializationSchema<CreditScoreLogInfo>, SerializationSchema<CreditScoreLogInfo> {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreLogSchema.class);

    private static final Gson gson = new Gson();

    @Override
    public CreditScoreLogInfo deserialize(byte[] bytes) {

        String origLog = new String(bytes);
        if (!origLog.isEmpty()) {
            CreditScoreLogInfo creditScoreLogInfo = gson.fromJson(origLog, CreditScoreLogInfo.class);
            creditScoreLogInfo.setOrigLog(origLog);
            return creditScoreLogInfo;
        }
        logger.info("is empty");
        return null;
    }

    @Override
    public boolean isEndOfStream(CreditScoreLogInfo creditScoreLogInfo) {

        return false;
    }

    @Override
    public TypeInformation<CreditScoreLogInfo> getProducedType() {

        return TypeInformation.of(CreditScoreLogInfo.class);
    }

    @Override
    public byte[] serialize(CreditScoreLogInfo creditScoreLogInfo) {

        return gson.toJson(creditScoreLogInfo).getBytes(Charset.forName("UTF-8"));
    }
}

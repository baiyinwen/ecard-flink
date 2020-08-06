package com.ecard.bigdata.schemas;

import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.model.CreditScore;
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
public class CreditScoreSchema implements DeserializationSchema<CreditScore>, SerializationSchema<CreditScore> {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreSchema.class);

    @Override
    public CreditScore deserialize(byte[] bytes) {

        String info = new String(bytes);
        if (!info.isEmpty()) {
            String[] data = info.split(",");
            CreditScore creditScore = new CreditScore();
            creditScore.setCreditID(data[0]);
            creditScore.setTime(data[1]);
            creditScore.setScore(data[2]);
            return creditScore;
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(CreditScore creditScore) {

        return false;
    }

    @Override
    public TypeInformation<CreditScore> getProducedType() {

        return TypeInformation.of(CreditScore.class);
    }

    @Override
    public byte[] serialize(CreditScore creditScore) {

        return creditScore.toString().getBytes(Charset.forName("UTF-8"));
    }
}

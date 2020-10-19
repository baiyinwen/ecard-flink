package com.ecard.bigdata.schemas;

import com.ecard.bigdata.model.CreditScore;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/13 10:49
 * @Version 1.0
 **/
public class CreditScoreSchema implements KeyedDeserializationSchema<CreditScore>, SerializationSchema<CreditScore> {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreSchema.class);

    @Override
    public CreditScore deserialize(byte[] keyBytes, byte[] valueBytes, String topic, int partition, long offset) {
        //String key = new String(keyBytes);
        String value = new String(valueBytes);
        if (!value.isEmpty()) {
            String[] data = value.split(",");
            CreditScore creditScore = new CreditScore();
            //creditScore.setTopic(topic);
            //creditScore.setKey(key);
            creditScore.setCreditID(data[0]);
            creditScore.setScore(data[1]);
            //creditScore.setTime(data[2]);
            return creditScore;
        }
        logger.info("is empty");
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

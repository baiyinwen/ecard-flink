package com.ecard.bigdata.waterMarkers;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.SignAmount;
import com.ecard.bigdata.utils.ConfigUtils;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 10:38
 * @Version 1.0
 **/
public class SignAmountAlterWatermark implements AssignerWithPeriodicWatermarks<SignAmount> {

    private static Logger logger = LoggerFactory.getLogger(SignAmountAlterWatermark.class);

    private final long maxOutOfOrder  = ConfigUtils.getLong(CONFIGS.SIGN_ALTER_MAX_OUT_OF_ORDER);
    private long currentTimestamp = CONSTANTS.NUMBER_0;

    @Override
    public long extractTimestamp(SignAmount signAmount, long previousElementTimestamp) {

        long time = signAmount.getCollectTime().getTime();
        currentTimestamp = Math.max(time, currentTimestamp);
        return time;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp - maxOutOfOrder);
    }

}

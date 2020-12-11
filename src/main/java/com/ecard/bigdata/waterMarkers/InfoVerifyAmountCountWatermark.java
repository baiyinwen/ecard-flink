package com.ecard.bigdata.waterMarkers;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.InfoVerifyAmount;
import com.ecard.bigdata.utils.ConfigUtils;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/12/04 10:38
 * @Version 1.0
 **/
public class InfoVerifyAmountCountWatermark implements AssignerWithPeriodicWatermarks<InfoVerifyAmount> {

    private final long maxOutOfOrder  = ConfigUtils.getLong(CONFIGS.INFO_VERIFICATION_COUNT_MAX_OUT_OF_ORDER);
    private long currentTimestamp = CONSTANTS.NUMBER_0;

    @Override
    public long extractTimestamp(InfoVerifyAmount infoVerifyAmount, long previousElementTimestamp) {

        long time = infoVerifyAmount.getCollectTime().getTime();
        currentTimestamp = Math.max(time, currentTimestamp);
        return time;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp - maxOutOfOrder);
    }

}

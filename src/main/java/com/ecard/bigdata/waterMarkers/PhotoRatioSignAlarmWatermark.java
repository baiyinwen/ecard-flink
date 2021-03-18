package com.ecard.bigdata.waterMarkers;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.PhotoRatioSign;
import com.ecard.bigdata.utils.ConfigUtils;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2021/3/17 09:38
 * @Version 1.0
 **/
public class PhotoRatioSignAlarmWatermark implements AssignerWithPeriodicWatermarks<PhotoRatioSign> {

    private final long maxOutOfOrder  = ConfigUtils.getLong(CONFIGS.PHOTO_RATIO_SIGN_ALARM_MAX_OUT_OF_ORDER);
    private long currentTimestamp = CONSTANTS.NUMBER_0;

    @Override
    public long extractTimestamp(PhotoRatioSign photoRatioSign, long previousElementTimestamp) {

        long time = photoRatioSign.getCollectTime().getTime();
        currentTimestamp = Math.max(time, currentTimestamp);
        return time;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp - maxOutOfOrder);
    }

}

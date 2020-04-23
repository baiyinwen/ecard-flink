package com.ecard.bigdata.waterMarkers;

import com.ecard.bigdata.model.JsonLogInfo;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.utils.DateTimeUtils;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 10:38
 * @Version 1.0
 **/
public class KafkaWatermark implements AssignerWithPeriodicWatermarks<JsonLogInfo> {

    private long maxTimeLag = 5000;
    private long currentTimestamp = Long.MIN_VALUE;

    @Override
    public long extractTimestamp(JsonLogInfo jsonLogInfo, long previousElementTimestamp) {
        long time = DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1).getTime();
        if (time > currentTimestamp) {
            this.currentTimestamp = time;
        }
        return currentTimestamp;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - maxTimeLag);

    }

}

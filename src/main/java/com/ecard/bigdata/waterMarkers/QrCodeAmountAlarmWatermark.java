package com.ecard.bigdata.waterMarkers;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.QrCodeAmount;
import com.ecard.bigdata.model.SignAmount;
import com.ecard.bigdata.utils.ConfigUtils;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2021/1/21 15:38
 * @Version 1.0
 **/
public class QrCodeAmountAlarmWatermark implements AssignerWithPeriodicWatermarks<QrCodeAmount> {

    private final long maxOutOfOrder  = ConfigUtils.getLong(CONFIGS.QRCODE_ALARM_MAX_OUT_OF_ORDER);
    private long currentTimestamp = CONSTANTS.NUMBER_0;

    @Override
    public long extractTimestamp(QrCodeAmount qrCodeAmount, long previousElementTimestamp) {

        long time = qrCodeAmount.getCollectTime().getTime();
        currentTimestamp = Math.max(time, currentTimestamp);
        return time;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp - maxOutOfOrder);
    }

}

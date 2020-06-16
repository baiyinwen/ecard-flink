package com.ecard.bigdata.waterMarkers;

import com.ecard.bigdata.model.DataAnalysisSignAmount;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
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
public class DataAnalysisSignWatermark implements AssignerWithPeriodicWatermarks<DataAnalysisSignAmount> {

    private static Logger logger = LoggerFactory.getLogger(DataAnalysisSignWatermark.class);

    private final long maxOutOfOrder  = ConfigUtils.getLong(CONFIGS.SIGN_AMOUNT_MAX_OUT_OF_ORDER);
    private long currentTimestamp = CONSTANTS.NUMBER_0;

    @Override
    public long extractTimestamp(DataAnalysisSignAmount dataAnalysisSignAmount, long previousElementTimestamp) {

        long time = dataAnalysisSignAmount.getCollectTime().getTime();
        currentTimestamp = Math.max(time, currentTimestamp);
        return time;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {

        return new Watermark(currentTimestamp - maxOutOfOrder);
    }

}

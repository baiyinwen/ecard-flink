package com.ecard.bigdata.waterMarkers;

import com.ecard.bigdata.bean.DataAnalysisSignMin;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.utils.ConfigUtil;
import com.ecard.bigdata.utils.ParameterUtils;
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
public class KafkaWatermark implements AssignerWithPeriodicWatermarks<DataAnalysisSignMin> {

    private static Logger logger = LoggerFactory.getLogger(KafkaWatermark.class);

    private final long maxOutOfOrder  = ConfigUtil.getLong(CONFIGS.MAX_OUT_OF_ORDER);
    private long currentTimestamp = CONSTANTS.NUMBER_0;

    @Override
    public long extractTimestamp(DataAnalysisSignMin dataAnalysisSignMin, long previousElementTimestamp) {
        long time = dataAnalysisSignMin.getCollectTime().getTime();
        currentTimestamp = Math.max(time, currentTimestamp);
        return time;
    }

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentTimestamp - maxOutOfOrder);
    }

}

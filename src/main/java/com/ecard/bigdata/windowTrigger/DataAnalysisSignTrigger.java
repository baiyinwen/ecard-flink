package com.ecard.bigdata.windowTrigger;

import com.ecard.bigdata.bean.JsonLogInfo;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/27 9:27
 * @Version 1.0
 **/
public class DataAnalysisSignTrigger extends Trigger<JsonLogInfo, TimeWindow> {

    @Override
    public TriggerResult onElement(JsonLogInfo jsonLogInfo, long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
        return null;
    }

    @Override
    public TriggerResult onProcessingTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
        return null;
    }

    @Override
    public TriggerResult onEventTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
        return null;
    }

    @Override
    public void clear(TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

    }

}

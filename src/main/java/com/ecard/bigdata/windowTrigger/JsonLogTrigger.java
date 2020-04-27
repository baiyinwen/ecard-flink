package com.ecard.bigdata.windowTrigger;

import com.ecard.bigdata.model.JsonLog;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/27 9:27
 * @Version 1.0
 **/
public class JsonLogTrigger extends Trigger<JsonLog, TimeWindow> {

    @Override
    public TriggerResult onElement(JsonLog jsonLog, long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
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

package com.ecard.bigdata.windowTrigger;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/27 9:27
 * @Version 1.0
 **/
public class SignAmountCountTrigger<JsonLogInfo> extends Trigger<JsonLogInfo, TimeWindow> {

    private final ReducingStateDescriptor<Long> countStateDescriptor = new ReducingStateDescriptor<Long>("counter", new Sum(), LongSerializer.INSTANCE);

    @Override
    public TriggerResult onElement(JsonLogInfo jsonLogInfo, long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

        ReducingState<Long> countReducingState = triggerContext.getPartitionedState(countStateDescriptor);
        countReducingState.add(1L);
        if (timeWindow.maxTimestamp() <= triggerContext.getCurrentWatermark()) {
            countReducingState.clear();
            return TriggerResult.FIRE;
        } else {
            triggerContext.registerEventTimeTimer(timeWindow.maxTimestamp());
            return TriggerResult.CONTINUE;
        }
    }

    @Override
    public TriggerResult onProcessingTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

        ReducingState<Long> countReducingState = triggerContext.getPartitionedState(countStateDescriptor);
        if (l >= timeWindow.maxTimestamp() && countReducingState.get()!= null && countReducingState.get() > 0) {
            countReducingState.clear();
            return TriggerResult.FIRE_AND_PURGE;
        } else if (l >= timeWindow.maxTimestamp() && countReducingState.get()!= null && countReducingState.get() == 0) {
            return TriggerResult.PURGE;
        } else {
            return TriggerResult.CONTINUE;
        }
    }

    @Override
    public void clear(TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {

        triggerContext.deleteEventTimeTimer(timeWindow.maxTimestamp());
    }

    @Override
    public void onMerge(TimeWindow timeWindow, OnMergeContext ctx) {
        long windowMaxTimestamp = timeWindow.maxTimestamp();
        if (windowMaxTimestamp > ctx.getCurrentWatermark()) {
            ctx.registerEventTimeTimer(windowMaxTimestamp);
        }
    }

    class Sum implements ReduceFunction<Long> {
        @Override
        public Long reduce(Long l1, Long l2) throws Exception {
            return l1 + l2;
        }
    }

}

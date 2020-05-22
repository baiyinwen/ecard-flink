package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/10 10:25
 * @Version 1.0
 **/
public class ExecutionEnvUtils {

    public static StreamExecutionEnvironment prepare(ParameterTool parameterTool) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度
        env.setParallelism(parameterTool.getInt(CONFIGS.STREAM_PARALLELISM));
        env.getConfig().disableSysoutLogging();
        //设置重试机制：重试次数，重试间隔
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(
                parameterTool.getInt(CONFIGS.RESTART_ATTEMPTS), parameterTool.getInt(CONFIGS.DELAY_BETWEEN_ATTEMPTS)));
        //设置checkpoint
        if (parameterTool.getBoolean(CONFIGS.STREAM_CHECKPOINT_ENABLE)) {
            env.enableCheckpointing(parameterTool.getInt(CONFIGS.STREAM_CHECKPOINT_INTERVAL), CheckpointingMode.AT_LEAST_ONCE);
        }
        env.getConfig().setGlobalJobParameters(parameterTool);
        //设置流的时间(IngestionTime:数据进入流的时间，ProcessingTime:处理数据的时间，EventTime:数据自带的时间戳)
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        return env;
    }

}

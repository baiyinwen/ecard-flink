package com.ecard.bigdata.main;

import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.SignDetail;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.sink.SignDetailSink;
import com.ecard.bigdata.utils.*;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @Description 签发量明细数据
 * @Author WangHongDong
 * @Date 2021/2/05 9:24
 * @Version 1.0
 **/
public class SignDetailStream {

    private static Logger logger = LoggerFactory.getLogger(SignDetailStream.class);
    private static final String ClassName = SignDetailStream.class.getSimpleName();
    private static long STREAM_CHECKPOINT_INTERVAL = 60000;
    private static CheckpointingMode STREAM_CHECKPOINT_MODE = CheckpointingMode.EXACTLY_ONCE;

    /**
     * @Description
     * @Param args --key value
     * @Return void
     * @Author WangHongDong
     * @Date 2021/2/05 9:24
     **/
    public static void main(String[] args) throws Exception {

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic = parameterTool.get(CONFIGS.SIGN_ALTER_KAFKA_TOPIC);
        final String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.enableCheckpointing(STREAM_CHECKPOINT_INTERVAL, STREAM_CHECKPOINT_MODE);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        //Flink从topic中最新的数据开始消费
        consumer.setStartFromLatest();
        DataStreamSource<JsonLogInfo> data = env.addSource(consumer);

        DataStream<JsonLogInfo> filterRes = data.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String event = jsonLogInfo.getEvent();
                if (CONSTANTS.EVENT_ESSC_LOG_SIGN.equals(event) || CONSTANTS.EVENT_ESSC_LOG_SIGN_ONE_STEP.equals(event) || CONSTANTS.EVENT_ESSC_LOG2_SIGN_PERSON.equals(event) || CONSTANTS.EVENT_ESSC_LOG2_SIGN_ONE_STEP.equals(event)) {
                    String outputStr = jsonLogInfo.getOutput().toString();
                    JSONObject outputJson;
                    try {
                        if (jsonLogInfo.getOutput() instanceof Map) {
                            outputStr = JSONObject.toJSONString(jsonLogInfo.getOutput());
                        }
                        outputJson = JSONObject.parseObject(outputStr);
                    } catch (Exception e) {
                        logger.error(jsonLogInfo.toString() + " --- 日志解析异常" + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                    if (outputJson != null) {
                        if (CONSTANTS.EVENT_MSG_CODE_VALUE.equals(outputJson.getString(CONSTANTS.EVENT_MSG_CODE_KEY))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });

        DataStream<SignDetail> distinctRes = filterRes.filter((FilterFunction<JsonLogInfo>) jsonLogInfo -> {
            if (null != jsonLogInfo) {
                String md5Log = EncodeUtils.md5Encode(jsonLogInfo.getOrigLog());
                boolean isMember = RedisClusterUtils.isExistsKey(CONSTANTS.SIGN_REDIS_LOG_DETAIL_MD5_KEY + md5Log);
                if (isMember) {
                    return false;
                } else {
                    RedisClusterUtils.setValue(CONSTANTS.SIGN_REDIS_LOG_DETAIL_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_DETAIL_MD5_KEY);
                    RedisClusterUtils.setExpire(CONSTANTS.SIGN_REDIS_LOG_DETAIL_MD5_KEY + md5Log, CONSTANTS.SIGN_REDIS_LOG_DETAIL_KEY_EXPIRE_SECONDS);
                }
                return true;
            }
            return false;
        }).map((MapFunction<JsonLogInfo, SignDetail>) jsonLogInfo -> {
            JSONObject Obj = null;
            Obj = JSONObject.parseObject(JSONObject.toJSONString(jsonLogInfo));
            JSONObject inputObj = Obj.getJSONObject(CONSTANTS.EVENT_ESSC_LOG_INPUT);
            JSONObject outputObj = Obj.getJSONObject(CONSTANTS.EVENT_ESSC_LOG_OUTPUT);

            SignDetail signDetail = new SignDetail();
            signDetail.setAac002(EncodeUtils.md5Encode(inputObj.getString(CONSTANTS.EVENT_ESSC_LOG_SIGN_AAC002)));
            signDetail.setCollectTime(DateTimeUtils.toTimestamp(jsonLogInfo.getTime(), CONSTANTS.DATE_TIME_FORMAT_1));
            signDetail.setChannelNo(jsonLogInfo.getChannelNo());
            String cardRegionCode = inputObj.getString(CONSTANTS.EVENT_ESSC_LOG_SIGN_AAB_301);
            if (null == cardRegionCode || cardRegionCode.trim().isEmpty()) {
                //取essc_log_sign的区划码
                cardRegionCode = inputObj.getString(CONSTANTS.EVENT_ESSC_LOG_SIGN_SIGN_SEQ);
                if (null != cardRegionCode && !cardRegionCode.trim().isEmpty()) {
                    cardRegionCode = cardRegionCode.substring(cardRegionCode.length()-6);
                }else{
                    //取essc_log2_sign_person日志的区划码
                    cardRegionCode =outputObj.getJSONObject(CONSTANTS.EVENT_ESSC_LOG_RESULT).getString(CONSTANTS.EVENT_ESSC_LOG_CARD_REGIONNO);
                }
            }
            signDetail.setCardRegionCode(cardRegionCode);
            return signDetail;
        }).returns(TypeInformation.of(new TypeHint<SignDetail>() {}));
        //distinctRes.print();
        distinctRes.addSink(new SignDetailSink()).name(SignDetailSink.class.getSimpleName());
        env.execute(ClassName);
    }
}

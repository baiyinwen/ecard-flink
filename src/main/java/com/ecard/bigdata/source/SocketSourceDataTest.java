package com.ecard.bigdata.source;

import com.ecard.bigdata.constants.CONSTANTS;
import com.ecard.bigdata.model.JsonLog;
import com.ecard.bigdata.utils.DateTimeUtils;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/26 15:09
 * @Version 1.0
 **/
public class SocketSourceDataTest implements SourceFunction<JsonLog> {

    private volatile boolean Running = true;
    static String name[] = {"zhao", "qian", "sun", "li", "zhou"};

    @Override
    public void run(SourceContext<JsonLog> ctx) throws Exception {

        String [] channelNos = {"9140241001", "9140241002", "9140241003"};
        String [] events = {"essc_log_sign", "essc_log_sign", "pay", "front"};
        String [] isSuccess = {"0", "1"};
        String [] msgCodes = {"000000", "000000", "000000", "111111", "222222", "333333"};
        Map<String, Integer> map = new HashMap<>();
        Random random = new Random();
        while (Running) {
            Thread.sleep(1000);

            String channelNo = channelNos[random.nextInt(channelNos.length)];
            String event = "essc_log_sign";//events[random.nextInt(events.length)];
            String flag = isSuccess[random.nextInt(isSuccess.length)];
            String msgCode = msgCodes[random.nextInt(msgCodes.length)];
            String currentTime = DateTimeUtils.customDateTime(0, CONSTANTS.DATE_TIME_FORMAT_1);

            JsonLog jsonLog = new JsonLog();
            jsonLog.setChannelNo(channelNo);
            jsonLog.setCostTime("21ms");
            jsonLog.setEvent(event);
            jsonLog.setInput("{\"channelNo\":\""+channelNo+"\",\"isSuccess\":\""+flag+"\"}");
            jsonLog.setOutput("{\"channelNo\":\""+channelNo+"\",\"msgCode\":\""+msgCode+"\"}");
            jsonLog.setTime(currentTime);
            jsonLog.setType("track");
            jsonLog.setVersion("1.0");

            ctx.collect(jsonLog);
        }
    }

    @Override
    public void cancel() {

    }

}

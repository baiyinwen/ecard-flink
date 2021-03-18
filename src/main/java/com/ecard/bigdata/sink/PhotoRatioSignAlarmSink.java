package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.PhotoRatioSign;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.DateTimeUtils;
import com.ecard.bigdata.utils.PushToFalconUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2021/3/17 09:59
 * @Version 1.0
 **/
public class PhotoRatioSignAlarmSink extends RichSinkFunction<PhotoRatioSign> {

    private static Logger logger = LoggerFactory.getLogger(PhotoRatioSignAlarmSink.class);

    private PushToFalconUtils pushToFalconUtils;

    private static String endpoint = "endpoint_data_photo_ratio_sign_alarm";
    private static int step = 60;
    private static String counterType = "GAUGE";
    private static String tags = "type=photoRatioSign,value=amount";
    private static String metric = "photo_ratio_sign_result";

    @Override
    public void open(Configuration parameters) throws Exception {
        pushToFalconUtils = new PushToFalconUtils();
        logger.info("调用open --- ");
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        logger.info("调用close --- ");
        super.close();
    }

    @Override
    public void invoke(PhotoRatioSign photoRatioSign, Context context) {

        pushPhotoAmountAlarm(photoRatioSign);
    }

    private void pushPhotoAmountAlarm(PhotoRatioSign photoRatioSign) {

        photoRatioSign.setCollectTime(DateTimeUtils.getIntervalBasicTime(photoRatioSign.getCollectTime().getTime(), ConfigUtils.getLong(CONFIGS.PHOTO_RATIO_SIGN_ALARM_TUMBLING_WINDOW_SIZE)));
        long timestamp = photoRatioSign.getCollectTime().getTime();
        float value = photoRatioSign.getPhotoCount().floatValue()/photoRatioSign.getSignCount().floatValue();
        photoRatioSign.setResult(value);
        logger.info("ready to push -- " + photoRatioSign.toString());
        pushToFalconUtils.sendInfoToFalcon(endpoint, metric, timestamp, step, value, counterType, tags);
    }

}

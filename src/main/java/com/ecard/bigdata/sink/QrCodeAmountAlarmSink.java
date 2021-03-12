package com.ecard.bigdata.sink;

import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.model.QrCodeAmount;
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
 * @Date 2021/1/21 16:59
 * @Version 1.0
 **/
public class QrCodeAmountAlarmSink extends RichSinkFunction<QrCodeAmount> {

    private static Logger logger = LoggerFactory.getLogger(QrCodeAmountAlarmSink.class);

    private PushToFalconUtils pushToFalconUtils;

    private static String endpoint = "endpoint_data_qrcode_alter";
    private static int step = 60;
    private static String counterType = "GAUGE";
    private static String tags = "type=qrcode,value=amount";

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
    public void invoke(QrCodeAmount qrCodeAmount, Context context) {

        pushQrCodeAmountAlarm(qrCodeAmount);
    }

    private void pushQrCodeAmountAlarm(QrCodeAmount qrCodeAmount) {

        qrCodeAmount.setCollectTime(DateTimeUtils.getIntervalBasicTime(qrCodeAmount.getCollectTime().getTime(), ConfigUtils.getLong(CONFIGS.QRCODE_ALARM_TUMBLING_WINDOW_SIZE)));
        String metric = qrCodeAmount.getEvent();
        long timestamp = qrCodeAmount.getCollectTime().getTime();
        float value = qrCodeAmount.getTransferTimes();
        logger.info("ready to push -- " + qrCodeAmount.toString());
        pushToFalconUtils.sendInfoToFalcon(endpoint, metric, timestamp, step, value, counterType, tags);
    }

}

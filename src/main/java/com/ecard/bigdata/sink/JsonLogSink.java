package com.ecard.bigdata.sink;

import com.ecard.bigdata.bean.DataAnalysisSignMin;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/24 14:59
 * @Version 1.0
 **/
public class JsonLogSink extends RichSinkFunction<DataAnalysisSignMin> {

    private static Logger logger = LoggerFactory.getLogger(JsonLogSink.class);

    @Override
    public void open(Configuration parameters) throws Exception {
        logger.info("调用open---");
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        logger.info("调用close---");
        super.close();
    }

    @Override
    public void invoke(DataAnalysisSignMin dataAnalysisSignMin, Context context) {

        dataAnalysisSignMin.setStatus("1");
        logger.info("保存数据---" + dataAnalysisSignMin.toString());
    }

}

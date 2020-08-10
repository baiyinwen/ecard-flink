package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONSTANTS;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/13 10:16
 * @Version 1.0
 **/
public class ParameterUtils {

    public static ParameterTool createParameterTool() {
        try {
            InputStream in1 = ExecutionEnvUtils.class.getClassLoader().getResourceAsStream(CONSTANTS.ECARD_FLINK_CONFIG_FILE);
            InputStream in2 = ExecutionEnvUtils.class.getClassLoader().getResourceAsStream(CONSTANTS.APPLICATION_CONFIG_FILE);
            return ParameterTool
                    .fromPropertiesFile(in1)
                    .mergeWith(ParameterTool.fromPropertiesFile(in2))
                    .mergeWith(ParameterTool.fromSystemProperties());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ParameterTool.fromSystemProperties();
    }

}

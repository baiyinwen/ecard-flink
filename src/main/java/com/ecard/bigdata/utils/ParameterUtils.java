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
        InputStream in1 = null;
        InputStream in2 = null;
        try {
            in1 = ExecutionEnvUtils.class.getClassLoader().getResourceAsStream(CONSTANTS.ECARD_FLINK_CONFIG_FILE);
            in2 = ExecutionEnvUtils.class.getClassLoader().getResourceAsStream(CONSTANTS.APPLICATION_CONFIG_FILE);
            return ParameterTool
                    .fromPropertiesFile(in1)
                    .mergeWith(ParameterTool.fromPropertiesFile(in2))
                    .mergeWith(ParameterTool.fromSystemProperties());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in1 != null) {
                    in1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (in2 != null) {
                    in2.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ParameterTool.fromSystemProperties();
    }

}

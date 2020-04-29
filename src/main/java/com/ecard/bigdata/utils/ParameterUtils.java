package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONSTANTS;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/13 10:16
 * @Version 1.0
 **/
public class ParameterUtils {

    public static final ParameterTool CONFIG_PARAMETERS = createParameterTool();

    public static ParameterTool createParameterTool() {
        try {
            return ParameterTool
                    .fromPropertiesFile(ExecutionEnvUtils.class.getClassLoader().getResourceAsStream(CONSTANTS.ECARD_FLINK_CONFIG_FILE))
                    .mergeWith(ParameterTool.fromSystemProperties());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ParameterTool.fromSystemProperties();
    }

}

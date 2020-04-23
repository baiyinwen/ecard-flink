package com.ecard.bigdata.utils;

import com.ecard.bigdata.constants.CONFIGS;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/4/13 10:16
 * @Version 1.0
 **/
public class ConfigUtils {

    public static final ParameterTool CONFIG_PARAMETERS = createParameterTool();

    public static ParameterTool createParameterTool(final String[] args) throws Exception {

        if (args.length > 0) {
            CONFIG_PARAMETERS.mergeWith(ParameterTool.fromArgs(args));
        }
        return CONFIG_PARAMETERS;
    }

    public static ParameterTool createParameterTool() {
        try {
            return ParameterTool
                    .fromPropertiesFile(ExecutionEnvUtils.class.getClassLoader().getResourceAsStream(CONFIGS.PROPERTIES_FILE_NAME))
                    .mergeWith(ParameterTool.fromSystemProperties());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ParameterTool.fromSystemProperties();
    }

}

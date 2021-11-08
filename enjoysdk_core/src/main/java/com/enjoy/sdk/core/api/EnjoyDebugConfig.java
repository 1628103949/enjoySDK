package com.enjoy.sdk.core.api;

import android.content.Context;

import com.enjoy.sdk.framework.common.TUitls;

import java.io.File;
import java.util.Properties;

public class EnjoyDebugConfig {

    private static final String CONFIG_FILE_NAME = "enjoy" + File.separator + "enjoy_debug.ini";

    private boolean debugSwitch = false;

    private EnjoyDebugConfig(Context ctx) {
        Properties properties = TUitls.readAssetsConfig(ctx, CONFIG_FILE_NAME);
        if (properties == null) {
            return;
        }
        String switchStr = properties.getProperty("switch");
        if (switchStr.equals("1")) {
            debugSwitch = true;
        }
    }
    public static EnjoyDebugConfig init(Context ctx) {
        return new EnjoyDebugConfig(ctx);
    }

    public boolean getDebugSwitch() {
        return debugSwitch;
    }
}

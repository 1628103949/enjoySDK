package com.enjoy.sdk.core.sdk.config;

import android.content.Context;
import android.text.TextUtils;

import com.enjoy.sdk.framework.common.TUitls;
import com.enjoy.sdk.core.api.EnjoyConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Properties;
//读取SDK配置
public class SDKConfig {
    private static final String CONFIG_FILE_NAME = "enjoy" + File.separator + "enjoy_sdk.ini";

    private String gameId;
    private String pId;
    private String refer;
    //是否为横屏，0为竖屏，1为横屏
    private boolean isLand;

    private SDKConfig(Context ctx) {
        Properties properties = TUitls.readAssetsConfig(ctx, CONFIG_FILE_NAME);
        if (properties == null) {
            throw new RuntimeException("enjoy sdk exception. enjoy/enjoy_sdk.ini config file read fail");
        }
        gameId = properties.getProperty("game_id");
        pId = properties.getProperty("pid");
        refer = properties.getProperty("refer");

        String isLandValue = null;
        if (properties.containsKey("isLand")) {
            isLandValue = properties.getProperty("is_land");
        }
        if (TextUtils.isEmpty(isLandValue)) {
            isLand = true;
        } else {
            if (isLandValue.equals("1")) {
                isLand = true;
            } else {
                isLand = false;
            }
        }

        if (TextUtils.isEmpty(gameId)) {
            throw new RuntimeException("enjoy sdk exception. enjoy/enjoy_sdk.ini config error, game_id read exception, please check enjoy/enjoy_sdk.ini.");
        }

        if (TextUtils.isEmpty(pId)) {
            throw new RuntimeException("enjoy sdk exception. enjoy/enjoy_sdk.ini config error, pid read exception, please check enjoy/enjoy_sdk.ini.");
        }

        if (TextUtils.isEmpty(refer)) {
            throw new RuntimeException("enjoy sdk exception. enjoy/enjoy_sdk.ini config error, refer read exception, please check enjoy/enjoy_sdk.ini.");
        }
    }
    public static SDKConfig init(Context ctx) {
        return new SDKConfig(ctx);
    }

    public String getGameId() {
        return gameId;
    }

    public String getPtId() {
        return pId;
    }

    public String getRefer() {
        return refer;
    }

    public boolean isLand() {
        return isLand;
    }

    @Override
    public String toString() {
        try {
            JSONObject dataJson = new JSONObject();
            dataJson.put("game_id", gameId);
            dataJson.put("pid", pId);
            dataJson.put("refer", refer);
            dataJson.put("is_land", isLand);
            dataJson.put("sdk_version", EnjoyConstants.SDK_VERSION);
            return dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "null";
    }
}

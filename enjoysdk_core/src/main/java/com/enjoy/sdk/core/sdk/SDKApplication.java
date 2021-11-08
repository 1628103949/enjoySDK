package com.enjoy.sdk.core.sdk;

import android.app.Application;
import android.content.Context;

import com.enjoy.sdk.framework.utils.ReflectUtils;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.own.EnjoyPlatformAPP;
import com.enjoy.sdk.core.platform.OPlatformBean;
import com.enjoy.sdk.core.sdk.config.PlatformConfig;
import com.enjoy.sdk.core.sdk.config.SDKConfig;

public class SDKApplication {
    private static final String[] TN_PT_ID = {"1"};

    private static SDKConfig sdkConfig;
    private static PlatformConfig platformConfig;
    private IPlatformAPP platformApp;

    public static SDKConfig getSdkConfig() {
        return sdkConfig;
    }

    public static PlatformConfig getPlatformConfig() {
        return platformConfig;
    }

    //判断是否自营平台
    public static boolean isTNPlatform() {
        String ptId = sdkConfig.getPtId();
        for (String id : TN_PT_ID) {
            if (ptId.equals(id)) {
                return true;
            }
        }
        return false;
    }
    public void proxyOnCreate(Application application) {
        SDKData.cleanSDKData();
        SDKData.setSdkGID(sdkConfig.getGameId());
        SDKData.setSdkPID(sdkConfig.getPtId());
        SDKData.setSdkRefer(sdkConfig.getRefer());
        SDKData.setSdkVer(EnjoyConstants.SDK_VERSION);
        platformApp.onCreate(application);
    }
    public void proxyAttachBaseContext(Context base) {
        sdkConfig = SDKConfig.init(base);
        platformConfig = PlatformConfig.init(base);
        if (isTNPlatform()) {
            this.platformApp = (IPlatformAPP) ReflectUtils.reflect(EnjoyPlatformAPP.class).newInstance().get();
        } else {
            this.platformApp = (IPlatformAPP) ReflectUtils.reflect(platformConfig.getAppClass()).newInstance(OPlatformBean.init(SDKApplication.getSdkConfig(), SDKApplication.getPlatformConfig())).get();
        }
        platformApp.attachBaseContext(base);
        OaidHelper.init(base);
    }

    public void setLogSwitch(boolean logSwitch) {
        SDKData.setLogSwitch(logSwitch);
    }
}

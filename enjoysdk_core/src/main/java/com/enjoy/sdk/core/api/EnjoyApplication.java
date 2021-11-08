package com.enjoy.sdk.core.api;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.enjoy.sdk.framework.TNFramework;
import com.enjoy.sdk.framework.utils.SDCardUtils;
import com.enjoy.sdk.core.sdk.SDKApplication;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class EnjoyApplication extends Application {

    public static EnjoyDebugConfig enjoyDebugConfig;
    private SDKApplication sdkApp;

    public EnjoyApplication(){
        this.sdkApp = new SDKApplication();
    }

    public static boolean getDebugConfig() {
        //获取SDCard Debug配置
        String sdCardDebugSwitch = "0";
        //Log.e("EnjoySDKtest",SDCardUtils.getSDCardPath());
        if (!TextUtils.isEmpty(SDCardUtils.getSDCardPath())) {
            String loggerFileDirPath = SDCardUtils.getSDCardPath() + "Enjoy_Debug.ini";
            try {
                File loggerFile = new File(loggerFileDirPath);
                if (loggerFile.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(loggerFile);
                    Properties properties = new Properties();
                    properties.load(fileInputStream);
                    sdCardDebugSwitch = properties.getProperty("debugSwitch");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sdCardDebugSwitch.equals("1") || enjoyDebugConfig.getDebugSwitch()) {
            return true;
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TNFramework.globalReady(EnjoyApplication.this, getDebugConfig());
        sdkApp.proxyOnCreate(EnjoyApplication.this);
        sdkApp.setLogSwitch(getDebugConfig());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        enjoyDebugConfig = EnjoyDebugConfig.init(base);
        sdkApp = new SDKApplication();
        sdkApp.proxyAttachBaseContext(base);
    }
}

package com.enjoy.sdk.core.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.enjoy.sdk.core.sdk.SDKApplication;
import com.enjoy.sdk.framework.TNFramework;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;
import com.enjoy.sdk.framework.view.common.ConfirmDialog;
import com.enjoy.sdk.core.api.callback.EnjoyCallback;
import com.enjoy.sdk.core.api.callback.EnjoyLogoutCallback;
import com.enjoy.sdk.core.api.callback.EnjoyPayCallback;
import com.enjoy.sdk.core.sdk.SDKCore;

import static com.enjoy.sdk.core.api.EnjoyApplication.getDebugConfig;

public class EnjoySdkApi implements IEnjoySdk {

    private static final String TAG = "api";
    static TNLog logger = LogFactory.getLog(TAG, true);

    private SDKCore mSDKCore;
    public EnjoySdkApi() {
        mSDKCore = new SDKCore();
    }

    public static EnjoySdkApi getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final EnjoySdkApi INSTANCE = new EnjoySdkApi();
    }

    @Override
    public void sdkInit(Activity mainAct, String appKey, EnjoyCallback callback) {
        logger.print("sdkInit called.");
        mSDKCore.sdkInit(mainAct, appKey, callback);
    }

    @Override
    public void setLogoutCallback(EnjoyLogoutCallback callback) {
        mSDKCore.setLogoutCallback(callback);
    }

    @Override
    public void sdkLogin(Activity mainAct, EnjoyCallback callback) {
        logger.print("sdkLogin called.");
        mSDKCore.sdkLogin(mainAct, callback);
    }

    @Override
    public void sdkLogout(Activity mainAct) {
        logger.print("sdkLogout called.");
        mSDKCore.sdkLogout(mainAct);
    }

    @Override
    public boolean isLogined() {
        logger.print("isLogined called.");
        return mSDKCore.isLogined();
    }

    @Override
    public void sdkPay(final Activity mainAct, final EnjoyPayInfo payInfo, final EnjoyPayCallback callback) {
        logger.print("sdkPay called.");
        payInfo.debugParam(mainAct, new ConfirmDialog.ConfirmCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (payInfo.checkParam()) {
                    mSDKCore.sdkPay(mainAct, payInfo, callback);
                } else {
                    callback.onFinish(EnjoyConstants.Status.SDK_ERR, "param is illegal, please check!");
                }

            }
        });
    }

    @Override
    public void sdkGameExit(Activity mainAct, EnjoyCallback callback) {
        logger.print("sdkGameExit called.");
        mSDKCore.sdkGameExit(mainAct, callback);
    }

    @Override
    public void sdkSubmitInfo(final Activity mainAct, final EnjoySubmitInfo submitInfo, final EnjoyCallback callback) {
        logger.print("sdkSubmitInfo called.");
        submitInfo.debugParam(mainAct, new ConfirmDialog.ConfirmCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (submitInfo.checkParam()) {
                    mSDKCore.sdkSubmitInfo(mainAct, submitInfo, callback);
                } else {
                    callback.onFail(EnjoyConstants.Status.SDK_ERR, "param is illegal, please check!");
                }
            }
        });
    }

    @Override
    public String sdkGetConfig(Activity mainAct) {
        logger.print("sdkGetConfig called.");
        return mSDKCore.sdkGetConfig(mainAct);
    }

    @Override
    public void sdkOnCreate(Activity mainAct) {
        logger.print("sdkOnCreate called.");
        mSDKCore.sdkOnCreate(mainAct);
    }

    @Override
    public void sdkOnStart(Activity mainAct) {
        logger.print("sdkOnStart called.");
        mSDKCore.sdkOnStart(mainAct);
    }

    @Override
    public void sdkOnRestart(Activity mainAct) {
        logger.print("sdkOnRestart called.");
        mSDKCore.sdkOnRestart(mainAct);
    }

    @Override
    public void sdkOnResume(Activity mainAct) {
        logger.print("sdkOnResume called.");
        mSDKCore.sdkOnResume(mainAct);
    }

    @Override
    public void sdkOnPause(Activity mainAct) {
        logger.print("sdkOnPause called.");
        mSDKCore.sdkOnPause(mainAct);
    }

    @Override
    public void sdkOnStop(Activity mainAct) {
        logger.print("sdkOnStop called.");
        mSDKCore.sdkOnStop(mainAct);
    }

    @Override
    public void sdkOnDestroy(Activity mainAct) {
        logger.print("sdkOnDestroy called.");
        mSDKCore.sdkOnDestroy(mainAct);
    }

    @Override
    public void sdkOnActivityResult(Activity mainAct, int requestCode, int resultCode, Intent data) {
        logger.print("sdkOnActivityResult called.");
        mSDKCore.sdkOnActivityResult(mainAct, requestCode, resultCode, data);
    }

    @Override
    public void sdkOnNewIntent(Activity mainAct, Intent data) {
        logger.print("sdkOnNewIntent called.");
        mSDKCore.sdkOnNewIntent(mainAct, data);
    }

    @Override
    public void sdkOnConfigurationChanged(Activity mainAct, Configuration newConfig) {
        logger.print("sdkOnConfigurationChanged called.");
        mSDKCore.sdkOnConfigurationChanged(mainAct, newConfig);
    }

    @Override
    public void sdkOnRequestPermissionsResult(Activity mainAct, int requestCode, String[] permissions, int[] grantResults) {
        logger.print("sdkOnRequestPermissionsResult called.");
        mSDKCore.sdkOnRequestPermissionsResult(mainAct, requestCode, permissions, grantResults);
    }


}

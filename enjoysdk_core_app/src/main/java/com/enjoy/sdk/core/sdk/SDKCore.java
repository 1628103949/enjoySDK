package com.enjoy.sdk.core.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.api.EnjoyPayInfo;
import com.enjoy.sdk.core.api.EnjoySubmitInfo;
import com.enjoy.sdk.core.api.IEnjoySdk;
import com.enjoy.sdk.core.api.callback.EnjoyCallback;
import com.enjoy.sdk.core.api.callback.EnjoyLogoutCallback;
import com.enjoy.sdk.core.api.callback.EnjoyPayCallback;
import com.enjoy.sdk.core.platform.event.GOnResume;
import com.enjoy.sdk.core.platform.event.OnPause;
import com.enjoy.sdk.core.sdk.event.EvInit;
import com.enjoy.sdk.core.sdk.event.EvLogin;
import com.enjoy.sdk.core.sdk.event.EvPay;
import com.enjoy.sdk.core.sdk.event.EvSubmit;
import com.enjoy.sdk.core.sdk.event.OnActivityResult;
import com.enjoy.sdk.core.sdk.flow.MActiveFlow;
import com.enjoy.sdk.core.sdk.netstate.NetCheck;
import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;
import com.enjoy.sdk.framework.permission.PermissionConstants;
import com.enjoy.sdk.framework.permission.PermissionUtils;
import com.enjoy.sdk.framework.utils.AppUtils;
import com.enjoy.sdk.framework.utils.FileUtils;
import com.enjoy.sdk.framework.view.common.ConfirmDialog;
import com.enjoy.sdk.framework.view.common.ViewUtils;
import com.enjoy.sdk.framework.xbus.Bus;

import java.util.List;

public class SDKCore implements IEnjoySdk {

    private static final int ENJOY_PHONE_PERMISSION = 1222;
    private static final int ENJOY_SDCARD_PERMISSION = 1333;
    private static final String TAG = "sdk";
    //API调用间隔，300毫秒
//    private static final int API_CALL_INTERVAL = 300;
    public static TNLog logger = LogFactory.getLog(TAG, true);
    private static boolean sdkInitialized = false;
    private static boolean sdkLogined = false;
    private static boolean sdkShouldDoLogin = false;
    //application context
    @SuppressLint("StaticFieldLeak")
    private static Activity mMainAct;
    private GCallback gCallback;
    private PlatformHelper platformHelper;
    private MActiveFlow mInitFlow;
    private Activity currentLoginActivity;
    private EnjoyCallback currentLoginCallback;

    private boolean haveShowRationale = false;

    public SDKCore(){
        gCallback = new GCallback(SDKCore.this);
        Bus.getDefault().register(gCallback);
        platformHelper = new PlatformHelper(SDKCore.this);

    }

    public static synchronized boolean isSdkInitialized() {
        return sdkInitialized;
    }

    public static synchronized void setSdkInitialized(boolean initial) {
        sdkInitialized = initial;
    }

    public static synchronized boolean isSdkLogined() {
        return sdkLogined;
    }

    public static synchronized void setSdkLogined(boolean logined) {
        sdkLogined = logined;
    }

    public static synchronized boolean getSdkShouldLogin() {
        return sdkShouldDoLogin;
    }

    public static synchronized void setSdkShouldLogin(boolean shouldLogin) {
        sdkShouldDoLogin = shouldLogin;
    }
    public static Activity getMainAct() {
        return mMainAct;
    }

    @Override
    public void sdkInit(Activity mainAct, String appKey, EnjoyCallback callback) {
        logger.print("sdkInit called.");
        if (mainAct == null || callback == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkInit api.");
        }

        mMainAct = mainAct;
        gCallback.setInitCallback(callback);
        SDKData.setSdkAppKey(appKey);

        if (SDKData.getSdkFirstActive()) {
            //发起请求获取oaid
            try {
                if (TextUtils.isEmpty(SDKData.getSdkOaid())) {
                    OaidHelper.start(mainAct);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获取 Android ID
            if (TextUtils.isEmpty(SDKData.getSdkAndroidId())) {
                String androidId = Settings.System.getString(getMainAct().getContentResolver(), Settings.System.ANDROID_ID);
                SDKData.setSdkAndroidId(androidId);
            }
        }

        try {
            if (!TextUtils.isEmpty(SDKData.getUpdateApkVersion())) {
                String currentApkVersion = AppUtils.getAppVersionName(mainAct.getPackageName());
                if (!SDKData.getUpdateApkVersion().equals(currentApkVersion)) {
                    //删除旧安装文件
                    FileUtils.deleteFile(SDKData.getUpdateApkPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        NetCheck.checkNet(mainAct, new NetCheck.NetFlowCallback() {
            @Override
            public void onSuccess() {
                //doInitFlow();
                requestPhonePermission();
            }

            @Override
            public void onFail() {
                Bus.getDefault().post(EvInit.getFail(EnjoyConstants.Status.HTTP_ERR, "network error. please check."));
            }
        });
    }

    @Override
    public void setLogoutCallback(EnjoyLogoutCallback callback) {
        logger.print("setLogoutCallback called.");
        if (callback == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check setLogoutCallback api.");
        }
        gCallback.setLogoutCallback(callback);
    }

    @Override
    public void sdkLogin(final Activity mainAct, EnjoyCallback callback) {
        logger.print("sdkLogin called.");
        if (mainAct == null || callback == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkLogin api.");
        }
        if (!isSdkInitialized()) {
            Bus.getDefault().post(EvLogin.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not initial."));
            sdkShouldDoLogin = true;
            currentLoginActivity = mainAct;
            currentLoginCallback = callback;
            return;
        }
        sdkShouldDoLogin = false;
        gCallback.setLoginCallback(callback);

        //调用登录，注销登录状态
        setSdkLogined(false);

        NetCheck.checkNet(mainAct, new NetCheck.NetFlowCallback() {
            @Override
            public void onSuccess() {
                platformHelper.login(mainAct);
            }

            @Override
            public void onFail() {
                Bus.getDefault().post(EvLogin.getFail(EnjoyConstants.Status.HTTP_ERR, "network error. please check."));
            }
        });
    }

    private void requestPhonePermission() {
        PermissionUtils.permission(PermissionConstants.PHONE_STATE)
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(final ShouldRequest shouldRequest) {
                        Log.e("enjoydebug","PHONE"+shouldRequest.toString());
                        ViewUtils.showConfirmDialog(mMainAct,
                                mMainAct.getString(ResUtil.getStringID("enjoy_request_permission", mMainAct)),
                                mMainAct.getString(ResUtil.getStringID("enjoy_device_permission_rationale_message", mMainAct)),
                                false,
                                new ConfirmDialog.ConfirmCallback() {
                                    @Override
                                    public void onCancel() {
                                        shouldRequest.again(true);
                                    }

                                    @Override
                                    public void onConfirm() {
                                        shouldRequest.again(true);
                                    }
                                });
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        Log.e("enjoydebug","PHONEonGranted"+"");
                        requestSDCardPermission();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        Log.e("enjoydebug","PHONEonDenied"+permissionsDeniedForever.toString()+permissionsDenied.toString());
                        if (permissionsDeniedForever != null && !permissionsDeniedForever.isEmpty()) {
                            ViewUtils.showConfirmDialog(mMainAct,
                                    mMainAct.getString(ResUtil.getStringID("enjoy_request_permission", mMainAct)),
                                    mMainAct.getString(ResUtil.getStringID("enjoy_device_permission_rationale_message", mMainAct)),
                                    false,
                                    new ConfirmDialog.ConfirmCallback() {
                                        @Override
                                        public void onCancel() {
                                            mMainAct.finish();
                                        }

                                        @Override
                                        public void onConfirm() {
                                            PermissionUtils.launchAppDetailsSettings(mMainAct, ENJOY_PHONE_PERMISSION);
                                        }
                                    });
                        } else {
                            requestPhonePermission();
                        }
                    }
                })
                .request();
    }
    private void requestSDCardPermission() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(final ShouldRequest shouldRequest) {
                        ViewUtils.showConfirmDialog(mMainAct,
                                mMainAct.getString(ResUtil.getStringID("enjoy_request_permission", mMainAct)),
                                mMainAct.getString(ResUtil.getStringID("enjoy_sdcard_permission_rationale_message", mMainAct)),
                                false,
                                new ConfirmDialog.ConfirmCallback() {
                                    @Override
                                    public void onCancel() {
                                        shouldRequest.again(true);
                                    }

                                    @Override
                                    public void onConfirm() {
                                        shouldRequest.again(true);
                                    }
                                });
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        doInitFlow();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (permissionsDeniedForever != null && !permissionsDeniedForever.isEmpty()) {
                            ViewUtils.showConfirmDialog(mMainAct,
                                    mMainAct.getString(ResUtil.getStringID("enjoy_request_permission", mMainAct)),
                                    mMainAct.getString(ResUtil.getStringID("enjoy_sdcard_permission_rationale_message", mMainAct)),
                                    false,
                                    new ConfirmDialog.ConfirmCallback() {
                                        @Override
                                        public void onCancel() {
                                            mMainAct.finish();
                                        }

                                        @Override
                                        public void onConfirm() {
                                            PermissionUtils.launchAppDetailsSettings(mMainAct, ENJOY_SDCARD_PERMISSION);
                                        }
                                    });
                        } else {
                            requestSDCardPermission();
                        }
                    }
                })
                .request();
    }
    private void doInitFlow() {

        if (mInitFlow == null) {
            mInitFlow = new MActiveFlow();
        }
        mInitFlow.doMInit(mMainAct, new MActiveFlow.MInitCallback() {
            @Override
            public void onFinish() {
                platformHelper.init(mMainAct);
            }
        });
    }
    void doInsideLogin() {
        if (currentLoginActivity != null && currentLoginCallback != null) {
            //sdkLogin(currentLoginActivity, currentLoginCallback);
        }
    }

    @Override
    public void sdkLogout(Activity mainAct) {
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkLogout api.");
        }
        //设置当前登录状态为false
        SDKCore.setSdkLogined(false);
        //清除帐号信息
        SDKData.cleanUserData();
        platformHelper.logout(mainAct);
    }

    @Override
    public boolean isLogined() {
        return isSdkLogined();
    }

    @Override
    public void sdkPay(final Activity mainAct, final EnjoyPayInfo payInfo, EnjoyPayCallback callback) {
        logger.print("sdkPay called.");
        if (mainAct == null || payInfo == null || callback == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkPay api.");
        }

        gCallback.setPayCallback(callback);

        if (!isSdkInitialized()) {
            Bus.getDefault().post(EvPay.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not initial."));
            return;
        }

        if (!isSdkLogined()) {
            Bus.getDefault().post(EvPay.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not login."));
            return;
        }

        NetCheck.checkNetNotExit(mainAct, new NetCheck.NetFlowCallback() {
            @Override
            public void onSuccess() {
                platformHelper.pay(mainAct, payInfo.toHash());
            }

            @Override
            public void onFail() {
                Bus.getDefault().post(EvPay.getFail(EnjoyConstants.Status.HTTP_ERR, "network error. please check."));
            }
        });
    }

    @Override
    public void sdkGameExit(Activity mainAct, EnjoyCallback callback) {
        logger.print("sdkGameExit called.");
        if (mainAct == null || callback == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkGameExit api.");
        }
        gCallback.setExitCallback(callback);

        platformHelper.exitGame(mainAct);
    }

    @Override
    public void sdkSubmitInfo(Activity mainAct, EnjoySubmitInfo submitInfo, EnjoyCallback callback) {
        logger.print("sdkSubmitInfo called.");
        if (mainAct == null || submitInfo == null) {
            throw new RuntimeException("sdk exception. param contain null, please check sdkSubmitInfo api.");
        }
        gCallback.setSubmitCallback(callback);

        if (!isSdkInitialized()) {
            Bus.getDefault().post(EvSubmit.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not initial."));
            return;
        }

        if (!isSdkLogined()) {
            Bus.getDefault().post(EvSubmit.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not login."));
            return;
        }

        platformHelper.submitInfo(mainAct, submitInfo.toHash());
    }

    @Override
    public String sdkGetConfig(Activity mainAct) {
        logger.print("sdkGetConfig called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkGetConfig api.");
        }
        return SDKApplication.getSdkConfig().toString();
    }

    @Override
    public void sdkOnCreate(Activity mainAct) {
        logger.print("sdkOnCreate called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnCreate api.");
        }
        platformHelper.onCreate(mainAct);
    }

    @Override
    public void sdkOnStart(Activity mainAct) {
        logger.print("sdkOnStart called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnStart api.");
        }
        platformHelper.onStart(mainAct);
    }

    @Override
    public void sdkOnRestart(Activity mainAct) {
        logger.print("sdkOnRestart called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnRestart api.");
        }
        platformHelper.onRestart(mainAct);
    }

    @Override
    public void sdkOnResume(Activity mainAct) {
        logger.print("sdkOnResume called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnResume api.");
        }
        platformHelper.onResume(mainAct);
        Bus.getDefault().post(new GOnResume(mainAct));
    }

    @Override
    public void sdkOnPause(Activity mainAct) {
        logger.print("sdkOnPause called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnPause api.");
        }
        platformHelper.onPause(mainAct);
        Bus.getDefault().post(new OnPause(mainAct));
    }

    @Override
    public void sdkOnStop(Activity mainAct) {
        logger.print("sdkOnStop called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnStop api.");
        }
        platformHelper.onStop(mainAct);
        if (mainAct != null && mainAct.isFinishing() && gCallback != null) {
            gCallback.destroyCallback();
            Bus.getDefault().unregister(gCallback);
        }
    }

    @Override
    public void sdkOnDestroy(Activity mainAct) {
        logger.print("sdkOnDestroy called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnDestroy api.");
        }
        platformHelper.onDestroy(mainAct);
        if (mainAct != null && mainAct.isFinishing() && gCallback != null) {
            gCallback.destroyCallback();
            Bus.getDefault().unregister(gCallback);
        }
    }

    @Override
    public void sdkOnActivityResult(Activity mainAct, int requestCode, int resultCode, Intent data) {
        logger.print("sdkOnActivityResult called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnActivityResult api.");
        }

        if (requestCode == ENJOY_PHONE_PERMISSION) {
            requestPhonePermission();
            return;
        }

        if (requestCode == ENJOY_SDCARD_PERMISSION) {
            requestSDCardPermission();
            return;
        }

        Bus.getDefault().post(new OnActivityResult(requestCode, resultCode, data, mainAct));
        platformHelper.onActivityResult(mainAct, requestCode, resultCode, data);
    }

    @Override
    public void sdkOnNewIntent(Activity mainAct, Intent data) {
        logger.print("sdkOnNewIntent called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnNewIntent api.");
        }
        platformHelper.onNewIntent(mainAct, data);
    }

    @Override
    public void sdkOnConfigurationChanged(Activity mainAct, Configuration newConfig) {
        logger.print("sdkOnConfigurationChanged called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnConfigurationChanged api.");
        }
        platformHelper.onConfigurationChanged(mainAct, newConfig);
    }

    @Override
    public void sdkOnRequestPermissionsResult(Activity mainAct, int requestCode, String[] permissions, int[] grantResults) {
        logger.print("sdkOnRequestPermissionsResult called.");
        if (mainAct == null) {
            throw new RuntimeException("enjoy sdk exception. param contain null, please check sdkOnRequestPermissionsResult api.");
        }
        platformHelper.onRequestPermissionsResult(mainAct, requestCode, permissions, grantResults);
    }
}

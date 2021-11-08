package com.enjoy.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.platform.OPlatformBean;
import com.enjoy.sdk.core.platform.OPlatformSDK;
import com.enjoy.sdk.core.platform.OPlatformUtils;
import com.enjoy.sdk.core.platform.event.OInitEv;
import com.enjoy.sdk.core.platform.event.OLoginEv;
import com.enjoy.sdk.core.platform.event.OLogoutEv;
import com.enjoy.sdk.core.platform.event.OPayEv;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;
import com.enjoy.sdk.framework.xbus.Bus;
import com.wancms.sdk.WancmsSDKManager;
import com.wancms.sdk.domain.LoginErrorMsg;
import com.wancms.sdk.domain.LogincallBack;
import com.wancms.sdk.domain.LogoutErrorMsg;
import com.wancms.sdk.domain.LogoutcallBack;
import com.wancms.sdk.domain.OnLoginListener;
import com.wancms.sdk.domain.OnLogoutListener;
import com.wancms.sdk.domain.OnPaymentListener;
import com.wancms.sdk.domain.PaymentCallbackInfo;
import com.wancms.sdk.domain.PaymentErrorMsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class YH extends OPlatformSDK {
    private static final String TAG = "YH";
    private static TNLog logger = LogFactory.getLog(TAG, true);
    private Context mContext;
    private boolean isLandScape = true;

    public YH(OPlatformBean pBean) {
        super(pBean);
    }

    @Override
    public void init(Activity activity) {
        mContext = activity;
        Bus.getDefault().post(OInitEv.getSucc());
    }
    private OnLogoutListener onLogoutListener = new OnLogoutListener() {
        @Override
        public void logoutSuccess(LogoutcallBack logoutcallBack) {
            Bus.getDefault().post(new OLogoutEv());
        }

        @Override
        public void logoutError(LogoutErrorMsg logoutErrorMsg) {

        }
    };
    @Override
    public void login(final Activity activity) {
        WancmsSDKManager.getInstance(activity).showLogin(activity, false, new OnLoginListener() {
            @Override
            public void loginSuccess(LogincallBack logincallBack) {
                WancmsSDKManager.getInstance(activity).showFloatView(onLogoutListener);
                login2RSService(logincallBack.username,logincallBack.logintime,logincallBack.sign);
            }

            @Override
            public void loginError(LoginErrorMsg loginErrorMsg) {
                Bus.getDefault().post(OLoginEv.getFail(loginErrorMsg.code,loginErrorMsg.msg));
            }
        });
    }

    //渠道登录成功后到服务器验证
    private void login2RSService(String uid,long logintime,String token) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("puid", uid);
            dataJson.put("logintime", logintime+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OPlatformUtils.loginToServer(token, dataJson.toString());
    }
    @Override
    public void logout(Activity mainAct) {

    }
    /**
     * 支付接口
     * payParams：服务器回传数据
     */
    @Override
    public void pay(Activity activity, HashMap<String, String> payParams) {
        Log.e("guoinfo",payParams.toString());
        WancmsSDKManager.getInstance(activity).showPay(activity
                , payParams.get(EnjoyConstants.PAY_ROLE_ID)
                , payParams.get(EnjoyConstants.PAY_MONEY)
                , payParams.get(EnjoyConstants.PAY_SERVER_ID)
                , payParams.get(EnjoyConstants.PAY_ORDER_NAME)
                , payParams.get(EnjoyConstants.PAY_ORDER_NAME)
                , payParams.get(EnjoyConstants.PAY_M_ORDER_ID)
                , new OnPaymentListener() {
                    @Override
                    public void paymentSuccess(PaymentCallbackInfo paymentCallbackInfo) {
                        Bus.getDefault().post(OPayEv.getSucc(paymentCallbackInfo.msg));
                    }

                    @Override
                    public void paymentError(PaymentErrorMsg paymentErrorMsg) {
                        Bus.getDefault().post(OPayEv.getFail(paymentErrorMsg.code,paymentErrorMsg.msg));
                    }
                });
    }


    @Override
    public void exitGame(Activity activity) {
        WancmsSDKManager.getInstance(activity).showdialog();
    }

    @Override
    public void submitInfo(Activity mainAct, HashMap<String, String> submitInfo) {
        if (submitInfo.get(EnjoyConstants.SUBMIT_TYPE).equals(EnjoyConstants.SUBMIT_TYPE_ENTER)) {
            WancmsSDKManager.getInstance(mainAct).setRoleDate(mainAct
                    ,submitInfo.get(EnjoyConstants.SUBMIT_ROLE_ID)
                    ,submitInfo.get(EnjoyConstants.SUBMIT_ROLE_NAME)
                    ,submitInfo.get(EnjoyConstants.SUBMIT_ROLE_LEVEL)
                    ,submitInfo.get(EnjoyConstants.SUBMIT_SERVER_ID)
                    ,submitInfo.get(EnjoyConstants.SUBMIT_SERVER_NAME)
                    ,null
                    );

        }

    }




    @Override
    public void onDestroy(Activity mainAct) {
        super.onDestroy(mainAct);

    }

    @Override
    public void onResume(Activity mainAct) {
        super.onResume(mainAct);
        WancmsSDKManager.getInstance(mainAct).showFloatView(onLogoutListener);
    }

    @Override
    public void onStop(Activity mainAct) {
        super.onStop(mainAct);
        WancmsSDKManager.getInstance(mainAct).removeFloatView();
    }
}

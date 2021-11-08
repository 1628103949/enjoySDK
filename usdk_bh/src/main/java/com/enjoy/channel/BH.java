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
import com.enjoy.sdk.core.platform.event.OPayEv;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;
import com.enjoy.sdk.framework.xbus.Bus;
import com.game.sdk.HuosdkManager;
import com.game.sdk.domain.CustomPayParam;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.RoleInfo;
import com.game.sdk.domain.SubmitRoleInfoCallBack;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.listener.OnPaymentListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BH extends OPlatformSDK {
    private static final String TAG = "BH";
    private static TNLog logger = LogFactory.getLog(TAG, true);
    private Context mContext;
    private boolean isLandScape = true;

    public BH(OPlatformBean pBean) {
        super(pBean);
    }

    @Override
    public void init(Activity activity) {
        mContext = activity;
        HuosdkManager.getInstance().initSdk(activity, new OnInitSdkListener() {
            @Override
            public void initSuccess(String s, String s1) {
                Bus.getDefault().post(OInitEv.getSucc());
                HuosdkManager.getInstance().addLoginListener(new OnLoginListener() {
                    @Override
                    public void loginSuccess(LogincallBack logincallBack) {
                        login2RSService(logincallBack.mem_id,logincallBack.user_token);
                    }

                    @Override
                    public void loginError(LoginErrorMsg loginErrorMsg) {
                        Bus.getDefault().post(OLoginEv.getFail(EnjoyConstants.Status.USER_CANCEL,loginErrorMsg.msg));
                    }
                });
            }

            @Override
            public void initError(String s, String s1) {
                Bus.getDefault().post(OInitEv.getFail(EnjoyConstants.Status.USER_CANCEL,s));
            }
        });
    }

    @Override
    public void login(Activity activity) {
        HuosdkManager.getInstance().showLogin(true);
    }

    //渠道登录成功后到服务器验证
    private void login2RSService(String uid,String token) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("puid", uid);
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
        //Log.e("guoinfo11",payParams.toString());
        try {
            JSONObject data = new JSONObject(payParams.get(EnjoyConstants.PAY_M_DATA));

            RoleInfo roleInfo = new RoleInfo();
            roleInfo.setRole_type(1);
            roleInfo.setServer_id(payParams.get(EnjoyConstants.PAY_SERVER_ID));
            roleInfo.setServer_name(payParams.get(EnjoyConstants.PAY_SERVER_NAME));
            roleInfo.setRole_id(payParams.get(EnjoyConstants.PAY_ROLE_ID));
            roleInfo.setRole_name(payParams.get(EnjoyConstants.PAY_ROLE_NAME));
            roleInfo.setParty_name("");
            roleInfo.setRole_level(Integer.parseInt(payParams.get(EnjoyConstants.PAY_ROLE_LEVEL)));
            roleInfo.setRole_vip(Integer.parseInt(payParams.get(EnjoyConstants.PAY_ROLE_VIP)));
            roleInfo.setRole_balence(0f);
            roleInfo.setRolelevel_ctime(System.currentTimeMillis()+"");
            roleInfo.setRolelevel_mtime(System.currentTimeMillis()+"");
            CustomPayParam customPayParam = new CustomPayParam();
            customPayParam.setCp_order_id(payParams.get(EnjoyConstants.PAY_M_ORDER_ID));
            float money = Float.parseFloat(payParams.get(EnjoyConstants.PAY_MONEY));
            customPayParam.setProduct_price(money);
            customPayParam.setProduct_count(1);
            customPayParam.setProduct_id(data.getInt("productId")+"");
            customPayParam.setProduct_name(data.getString("productName"));
            customPayParam.setProduct_desc(data.getString("productDesc"));
            customPayParam.setExchange_rate(Integer.parseInt(payParams.get(EnjoyConstants.PAY_RATE)));
            customPayParam.setCurrency_name("元宝");
            customPayParam.setExt("");
            customPayParam.setRoleinfo(roleInfo);
            HuosdkManager.getInstance().showPay(customPayParam, new OnPaymentListener() {
                @Override
                public void paymentSuccess(PaymentCallbackInfo paymentCallbackInfo) {
                    Bus.getDefault().post(OPayEv.getSucc(paymentCallbackInfo.msg));
                }

                @Override
                public void paymentError(PaymentErrorMsg paymentErrorMsg) {
                    Bus.getDefault().post(OPayEv.getFail(EnjoyConstants.Status.USER_CANCEL,paymentErrorMsg.msg));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void exitGame(Activity activity) {

    }

    @Override
    public void submitInfo(Activity mainAct, HashMap<String, String> submitInfo) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRole_type(1);
        roleInfo.setServer_id(submitInfo.get(EnjoyConstants.SUBMIT_SERVER_ID));
        roleInfo.setServer_name(submitInfo.get(EnjoyConstants.SUBMIT_SERVER_NAME));
        roleInfo.setRole_id(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_ID));
        roleInfo.setRole_name(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_NAME));
        roleInfo.setParty_name("");
        roleInfo.setRole_level(Integer.parseInt(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_LEVEL)));
        roleInfo.setRole_vip(Integer.parseInt(submitInfo.get(EnjoyConstants.SUBMIT_VIP)));
        roleInfo.setRole_balence(0f);
        roleInfo.setRolelevel_ctime(System.currentTimeMillis()+"");
        roleInfo.setRolelevel_mtime(System.currentTimeMillis()+"");
        HuosdkManager.getInstance().setRoleInfo(roleInfo, new SubmitRoleInfoCallBack() {
            @Override
            public void submitSuccess() {

            }

            @Override
            public void submitFail(String s) {

            }
        });
    }


    @Override
    public void onDestroy(Activity mainAct) {
        super.onDestroy(mainAct);
        HuosdkManager.getInstance().recycle();
    }

    @Override
    public void onResume(Activity mainAct) {
        super.onResume(mainAct);
        //HuosdkManager.getInstance().showFloatView();
    }

    @Override
    public void onStop(Activity mainAct) {
        super.onStop(mainAct);
        //HuosdkManager.getInstance().removeFloatView();
    }
}

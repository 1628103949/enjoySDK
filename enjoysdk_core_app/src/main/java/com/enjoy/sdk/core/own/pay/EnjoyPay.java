package com.enjoy.sdk.core.own.pay;

import android.app.Activity;

import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.own.event.EnjoyPayEv;
import com.enjoy.sdk.core.sdk.EnjoyUtils;
import com.enjoy.sdk.core.sdk.SDKApplication;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.core.sdk.event.EvPay;
import com.enjoy.sdk.framework.common.Dev;
import com.enjoy.sdk.framework.utils.AppUtils;
import com.enjoy.sdk.framework.utils.EncryptUtils;
import com.enjoy.sdk.framework.view.dialog.BounceEnter.BounceBottomEnter;
import com.enjoy.sdk.framework.view.dialog.ZoomExit.ZoomOutExit;
import com.enjoy.sdk.framework.xbus.Bus;

import java.util.HashMap;

/**
 * Data：19/01/2019-10:38 AM
 * Author: ranger
 */
public class EnjoyPay {
    private Activity payActivity;
    private HashMap<String, String> payParams;
    private PayDialog payDialog;
    private PayDialog.PayCallback payCallback = new PayDialog.PayCallback() {
        @Override
        public void onFinish() {
            //支付完成，查询订单，显示公告
            if(SDKApplication.isTNPlatform()){
                Bus.getDefault().post(EnjoyPayEv.getFail(EnjoyConstants.Status.PAY_UNKNOWN, "支付状态未知，请游戏服务端按需发货！"));
            }else{
                //切支付，需要回调全局
                Bus.getDefault().post(EvPay.getFail(EnjoyConstants.Status.PAY_UNKNOWN, "支付状态未知，请游戏服务端按需发货！"));
            }
        }
    };

    public void doPay(Activity act, HashMap<String, String> payParams) {
        this.payActivity = act;
        this.payParams = payParams;
        dealPay();
    }

    private void dealPay() {
        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put("pid", SDKData.getSdkPID());
        urlParams.put("gid", SDKData.getSdkGID());
        urlParams.put("refer", SDKData.getSdkRefer());
        urlParams.put("duid", SDKData.getSDKDuid());
        urlParams.put("version", AppUtils.getAppVersionName(SDKCore.getMainAct().getPackageName()));
        urlParams.put("sdkver", SDKData.getSdkVer());
        String time = System.currentTimeMillis() / 1000 + "";
        urlParams.put("time", time);
        urlParams.put("token", SDKData.getSdkUserToken());
        urlParams.put("uid", SDKData.getSdkUserId());
        urlParams.put("uname", SDKData.getSdkUserName());
        urlParams.put("doid", payParams.get(EnjoyConstants.PAY_ORDER_ID));
        urlParams.put("dsid", payParams.get(EnjoyConstants.PAY_SERVER_ID));
        urlParams.put("dsname", payParams.get(EnjoyConstants.PAY_SERVER_NAME));
        urlParams.put("dext", payParams.get(EnjoyConstants.PAY_EXT));
        urlParams.put("drid", payParams.get(EnjoyConstants.PAY_ROLE_ID));
        urlParams.put("drname", payParams.get(EnjoyConstants.PAY_ROLE_NAME));
        urlParams.put("drlevel", payParams.get(EnjoyConstants.PAY_ROLE_LEVEL));
        urlParams.put("dmoney", payParams.get(EnjoyConstants.PAY_MONEY));
        urlParams.put("dradio", payParams.get(EnjoyConstants.PAY_RATE));
        urlParams.put("money", payParams.get(EnjoyConstants.PAY_MONEY));
        urlParams.put("moid", payParams.get(EnjoyConstants.PAY_M_ORDER_ID));
        urlParams.put("mac", Dev.getMacAddress(SDKCore.getMainAct()));
        urlParams.put("imei", Dev.getPhoneIMEI(SDKCore.getMainAct()));

        String preSign = SDKData.getSdkPID() + SDKData.getSdkGID() + time + SDKData.getSdkAppKey() + SDKData.getSdkUserId() + payParams.get(EnjoyConstants.PAY_M_ORDER_ID);
        urlParams.put("sign", EncryptUtils.encryptMD5ToString(preSign).toLowerCase());

        String payUrl = EnjoyUtils.buildUrlParams(payParams.get(EnjoyConstants.PAY_M_URL), urlParams);
        showPayDialog(payUrl);
    }

    private void showPayDialog(String payUrl) {
        if (payDialog != null) {
            if (payDialog.isShowing()) {
                payDialog.dismiss();
            }
        }
        payDialog = null;
        payDialog = new PayDialog(payActivity, payUrl, payCallback);
        payDialog.showAnim(new BounceBottomEnter()).dismissAnim(new ZoomOutExit()).dimEnabled(true).show();
    }

    public void destroy() {

    }

}

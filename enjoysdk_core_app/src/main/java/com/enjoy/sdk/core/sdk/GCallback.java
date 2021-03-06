package com.enjoy.sdk.core.sdk;

import android.text.TextUtils;

import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.params.MPayQueryParam;
import com.enjoy.sdk.core.sdk.common.NoticeDialog;
import com.enjoy.sdk.core.sdk.event.EvExit;
import com.enjoy.sdk.core.sdk.event.EvInit;
import com.enjoy.sdk.core.sdk.event.EvLogin;
import com.enjoy.sdk.core.sdk.event.EvLogout;
import com.enjoy.sdk.core.sdk.event.EvPay;
import com.enjoy.sdk.core.sdk.event.EvSubmit;
import com.enjoy.sdk.core.sdk.flow.MActiveFlow;
import com.enjoy.sdk.framework.utils.ReflectUtils;
import com.enjoy.sdk.framework.xbus.annotation.BusReceiver;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.x;

import org.json.JSONException;
import org.json.JSONObject;

public class GCallback {
    private SDKCore sdkCore;
    private Object initCallback;
    private Object loginCallback;
    private Object payCallback;
    private Object exitCallback;
    private Object logoutCallback;
    private Object submitCallback;
    private Object eventSubmitCallback;

    private NoticeDialog payNoticeDialog;

    GCallback(SDKCore sdkCore) {
        this.sdkCore = sdkCore;
    }

    void setInitCallback(Object initCallback) {
        this.initCallback = null;
        this.initCallback = initCallback;
    }

    void setLogoutCallback(Object logoutCallback) {
        this.logoutCallback = null;
        this.logoutCallback = logoutCallback;
    }

    void setLoginCallback(Object loginCallback) {
        this.loginCallback = null;
        this.loginCallback = loginCallback;
    }

    void setPayCallback(Object payCallback) {
        this.payCallback = null;
        this.payCallback = payCallback;
    }

    void setExitCallback(Object exitCallback) {
        this.exitCallback = null;
        this.exitCallback = exitCallback;
    }
    void setEventSubmitCallback(Object eventSubmitCallback) {
        this.eventSubmitCallback = null;
        this.eventSubmitCallback = eventSubmitCallback;
    }
    void setSubmitCallback(Object submitCallback) {
        this.submitCallback = null;
        this.submitCallback = submitCallback;
    }

    public void destroyCallback() {
        initCallback = null;
        logoutCallback = null;
        loginCallback = null;
        payCallback = null;
        exitCallback = null;
        submitCallback = null;
        if (payNoticeDialog != null && payNoticeDialog.isShowing()) {
            payNoticeDialog.dismiss();
        }
        sdkCore = null;
    }

    @BusReceiver
    public void onInitEvent(EvInit evInit) {
        SDKCore.logger.print("onInitEvent --> " + evInit.toString());
        if (initCallback == null) {
            return;
        }
        MActiveFlow.setInitLock(false);
        if (evInit.getRet() == EvInit.SUCCESS) {
            //success
            SDKCore.setSdkInitialized(true);
            ReflectUtils.reflect(initCallback).method("onSuccess", evInit.getInitInfo());
            if (SDKCore.getSdkShouldLogin()) {
                if (sdkCore != null) {
                    sdkCore.doInsideLogin();
                    SDKCore.setSdkShouldLogin(false);
                }
            }
        } else {
            //fail
            ReflectUtils.reflect(initCallback).method("onFail", evInit.getInitCode(), evInit.getInitMsg());
        }
    }

    @BusReceiver
    public void onLoginEvent(EvLogin evLogin) {
        SDKCore.logger.print("onLoginEvent --> " + evLogin.toString());
        if (loginCallback == null) {
            return;
        }

        if (evLogin.getRet() == EvLogin.SUCCESS) {
            //success
            SDKCore.setSdkLogined(true);
            ReflectUtils.reflect(loginCallback).method("onSuccess", evLogin.getUserInfo());
        } else {
            //fail
            ReflectUtils.reflect(loginCallback).method("onFail", evLogin.getLoginCode(), evLogin.getLoginMsg());
        }
    }

    @BusReceiver
    public void onLogoutEvent(EvLogout evLogout) {
        SDKCore.logger.print("onLogoutEvent");

        if (logoutCallback == null) {
            return;
        }
        //??????????????????
        SDKCore.setSdkLogined(false);
        ReflectUtils.reflect(logoutCallback).method("onLogout");
    }

    @BusReceiver
    public void onPayEvent(final EvPay evPay) {
        SDKCore.logger.print("onPayEvent --> " + evPay.toString());

        if (payCallback == null) {
            return;
        }

        //?????????????????????????????????
        MPayQueryParam mPayQueryParam = new MPayQueryParam();
        x.http().post(mPayQueryParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(EnjoyResponse response) {
                try {
                    JSONObject dataJson = new JSONObject(response.data);

                    try {
                        //??????????????????
                        String money = dataJson.getString("money");
                        String payWay = dataJson.getString("pway");
                        String goodsName = dataJson.getString("goodsname");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (dataJson.has("nurl")) {
                        String noticeUrl = EnjoyUtils.buildCommonWebUrl(dataJson.getString("nurl"), true);
                        if (!TextUtils.isEmpty(noticeUrl)) {
                            if (payNoticeDialog != null && payNoticeDialog.isShowing()) {
                                payNoticeDialog.dismiss();
                            }
                            payNoticeDialog = null;
                            payNoticeDialog = new NoticeDialog(SDKCore.getMainAct(), noticeUrl, new NoticeDialog.NoticeCallback() {
                                @Override
                                public void onFinish() {
                                    dealPayCallback(evPay);
                                }
                            });
                            payNoticeDialog.show();
                        }
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dealPayCallback(evPay);
            }

            @Override
            public void onError(final Throwable ex, boolean isOnCallback) {
                //?????????
                dealPayCallback(evPay);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void dealPayCallback(EvPay evPay) {
        if (evPay.getRet() == EvPay.SUCCESS) {
            //success
            ReflectUtils.reflect(payCallback).method("onFinish", EnjoyConstants.Status.PAY_SUCC, evPay.getPayInfo());
        } else {
            //fail
            ReflectUtils.reflect(payCallback).method("onFinish", evPay.getPayCode(), evPay.getPayMsg());
        }
    }

    @BusReceiver
    public void onExitEvent(EvExit evExit) {
        SDKCore.logger.print("onExitEvent --> " + evExit.toString());

        if (exitCallback == null) {
            if (SDKCore.getMainAct() != null) {
                SDKCore.getMainAct().finish();
                System.exit(1);
            }
            return;
        }

        if (evExit.getRet() == EvExit.SUCCESS) {
            //success
            ReflectUtils.reflect(exitCallback).method("onSuccess", evExit.getExitInfo());
        } else {
            //fail
            ReflectUtils.reflect(exitCallback).method("onFail", evExit.getExitCode(), evExit.getExitMsg());
        }
    }

    @BusReceiver
    public void onSubmitEvent(EvSubmit evSubmit) {
        SDKCore.logger.print("onSubmitEvent --> " + evSubmit.toString());
        if (submitCallback == null) {
            return;
        }

        if (evSubmit.getRet() == EvSubmit.SUCCESS) {
            //success
            ReflectUtils.reflect(submitCallback).method("onSuccess", evSubmit.getSubmitInfo());
        } else {
            //fail
            ReflectUtils.reflect(submitCallback).method("onFail", evSubmit.getSubmitCode(), evSubmit.getSubmitMsg());
        }
    }


}

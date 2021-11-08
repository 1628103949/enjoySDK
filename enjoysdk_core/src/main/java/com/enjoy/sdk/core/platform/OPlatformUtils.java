package com.enjoy.sdk.core.platform;

import android.text.TextUtils;


import com.enjoy.sdk.framework.view.common.ViewUtils;
import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.x;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.exception.EnjoyServerException;
import com.enjoy.sdk.core.http.params.MVerifyParam;
import com.enjoy.sdk.core.platform.event.OLoginEv;
import com.enjoy.sdk.core.sdk.EnjoyUtils;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.core.sdk.common.NoticeDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description: something help for channel compatibly.
 * Data：27/09/2018-11:21 AM
 * Author: ranger
 */
public class OPlatformUtils {
    private static NoticeDialog loginNoticeDialog;
    public static void loginToServer(String token) {
        loginToServer(token, null);
    }
    public static void loginToServer(String token, String data) {
        loginToServer(token, null, null, data);
    }

    public static void loginToServer(String token, String uid, String name, String data) {
        MVerifyParam mVerifyParam = new MVerifyParam(token, uid, name, data);
        x.http().post(mVerifyParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(EnjoyResponse enjoyResponse) {
                dealReqSuccess(enjoyResponse);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof EnjoyServerException) {
                    ViewUtils.sdkShowTips(SDKCore.getMainAct(),((EnjoyServerException) ex).getServerMsg());
                } else {
                    ViewUtils.sdkShowTips(SDKCore.getMainAct(),"网络异常，发送失败，请重试！");
                }
                dealReqFail(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private static void dealReqFail(Throwable ex) {
        if (ex instanceof EnjoyServerException) {
            //服务端错误
            Bus.getDefault().post(OLoginEv.getFail(EnjoyConstants.Status.SERVER_ERR, ((EnjoyServerException) ex).getServerMsg()));
        } else {
            Bus.getDefault().post(OLoginEv.getFail(EnjoyConstants.Status.HTTP_ERR, ex.getMessage()));
        }
    }

    private static void dealReqSuccess(EnjoyResponse response) {
        try {
            if (TextUtils.isEmpty(response.data) || response.data.equals("[]")) {
                Bus.getDefault().post(OLoginEv.getFail(EnjoyConstants.Status.SERVER_ERR, "server data error."));
                return;
            }
            JSONObject dataJson = new JSONObject(response.data);
            String userId = dataJson.getString("uid");
            String userName = dataJson.getString("uname");
            String userToken = dataJson.getString("token");

            //储存进本地
            SDKData.setSdkUserId(userId);
            SDKData.setSdkUserName(userName);
            SDKData.setSdkUserToken(userToken);
            dealNotice(dataJson, userId, userName, userToken);
        } catch (Exception e) {
            e.printStackTrace();
            Bus.getDefault().post(OLoginEv.getFail(EnjoyConstants.Status.SERVER_ERR, "server data error, parse data exception."));
        }
    }

    private static void dealNotice(JSONObject dataJson, final String userId, final String userName, final String userToken) {
        try {
            if (dataJson.has("nurl")) {
                String noticeUrl = EnjoyUtils.buildCommonWebUrl(dataJson.getString("nurl"), true);
                if (!TextUtils.isEmpty(noticeUrl)) {
                    if (loginNoticeDialog != null && loginNoticeDialog.isShowing()) {
                        loginNoticeDialog.dismiss();
                    }
                    loginNoticeDialog = null;
                    loginNoticeDialog = new NoticeDialog(SDKCore.getMainAct(), noticeUrl, new NoticeDialog.NoticeCallback() {
                        @Override
                        public void onFinish() {
                            dealCallback(userId, userName, userToken);
                        }
                    });
                    loginNoticeDialog.show();
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dealCallback(userId, userName, userToken);
    }

    private static void dealCallback(String userId, String userName, String userToken) {
        //回调
        String retString = toOpenUser(userId, userName, userToken);
        Bus.getDefault().post(OLoginEv.getSucc(retString));
    }

    private static String toOpenUser(String userId, String userName, String token) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(token)) {
            return "no user data, please contact technical.";
        }
        try {
            JSONObject userJson = new JSONObject();
            userJson.put("userId", userId);
            userJson.put("userName", userName);
            userJson.put("token", token);
            return userJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

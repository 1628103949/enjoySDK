package com.enjoy.sdk.core.own.account;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.enjoy.sdk.core.own.account.login.EnjoyNotiDialog;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.framework.view.common.ViewUtils;
import com.enjoy.sdk.framework.view.dialog.BounceEnter.BounceBottomEnter;
import com.enjoy.sdk.framework.view.dialog.FadeEnter.FadeEnter;
import com.enjoy.sdk.framework.view.dialog.FadeExit.FadeExit;
import com.enjoy.sdk.framework.view.dialog.ZoomExit.ZoomOutExit;
import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.image.ImageOptions;
import com.enjoy.sdk.framework.xutils.x;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.exception.EnjoyServerException;
import com.enjoy.sdk.core.own.account.login.AutoLoginDialog;
import com.enjoy.sdk.core.own.account.login.LoginDialog;
import com.enjoy.sdk.core.own.account.user.User;
import com.enjoy.sdk.core.own.account.user.UserUtils;
import com.enjoy.sdk.core.own.common.RealNameDialog;
import com.enjoy.sdk.core.own.event.EnjoyLoginEv;
import com.enjoy.sdk.core.own.fw.EnjoyBallEv;
import com.enjoy.sdk.core.sdk.EnjoyUtils;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.core.sdk.common.NoticeDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class EnjoyAccount {
    public static final int AUTO_LOGIN = 1;
    public static final int ACCOUNT_LOGIN = 2;
    public static final int ACCOUNT_REG = 3;
    public static final int PHONE_REG = 4;

    private static boolean isLogout = false;

    private Activity loginActivity;

    private User currentUser;

    private LoginDialog loginDialog;

    private AutoLoginDialog autoLoginDialog;

    private NoticeDialog loginNoticeDialog;

    public static synchronized boolean isLogout() {
        return isLogout;
    }

    public static synchronized void setIsLogout(boolean logout) {
        isLogout = logout;
    }

    public void doLogin(Activity act) {
        this.loginActivity = act;
        if (isLogout()) {
            //?????? Logout ????????????????????????
            //??????????????????
            showLoginDialog();
        } else {
            //??????????????????
            User lastUser = UserUtils.getLastUser();
            if (lastUser != null) {
                //????????????????????????????????????????????????
                autoLogin(lastUser);
            } else {
                //??????????????????
                showLoginDialog();
            }
        }
    }

    private void showLoginDialog() {
        if (loginDialog != null) {
            if (loginDialog.isShowing()) {
                loginDialog.dismiss();
            }
        }
        loginDialog = null;

        loginDialog = new LoginDialog(loginActivity, EnjoyAccount.this);
        loginDialog.showAnim(new BounceBottomEnter()).dismissAnim(new ZoomOutExit()).dimEnabled(true).show();
    }

    public void onLoginViewClose() {
        //??????????????????
        Bus.getDefault().post(EnjoyLoginEv.getFail(EnjoyConstants.Status.USER_CANCEL, "?????????????????????"));
        dismissLoginDialog();
    }

    private void autoLogin(final User user) {
        if (TextUtils.isEmpty(user.getUserId()) || TextUtils.isEmpty(user.getUserName()) || TextUtils.isEmpty(user.getUserPwd())) {
            showLoginDialog();
            return;
        }
        //????????????????????????
        dismissAutoDialog();
        String showName = user.getUserName();
        autoLoginDialog = new AutoLoginDialog(loginActivity, EnjoyAccount.this, showName, user.getUserPwd(), new AutoLoginDialog.AutoCallback() {
            @Override
            public void onSwitchAccount() {
                showLoginDialog();
            }
        });
        autoLoginDialog.showAnim(new FadeEnter()).dismissAnim(new FadeExit()).dimEnabled(true).show();
    }
    public void dealLoginSuccResult(int loginType, EnjoyResponse ret, String uPwd) {
        try {
            String data = ret.data;
            //Log.e("guoquwan",ret.toString());
            if (ret.state == 0) {
                //???????????????????????????

                if (loginType == AUTO_LOGIN) {
                    //???????????????
                    dismissAutoDialog();
                    showLoginDialog();
                } else {
                    //??????????????????
                    onLoginFail(EnjoyConstants.Status.SERVER_ERR, ret.msg);
//                    dismissAutoDialog();
//                    showLoginDialog();
                }
                return;
            }
            //????????????
            JSONObject dataJson = new JSONObject(data);
            String userId = dataJson.getString("uid");
            String userName = dataJson.getString("uname");
            String userToken = dataJson.getString("token");
            String userPhone = null;
            if (dataJson.has("phone")) {
                userPhone = dataJson.getString("phone");
            }
            User user = new User();
            user.setUserId(userId);
            user.setUserName(userName);
            user.setUserToken(userToken);
            switch (loginType) {
                case AUTO_LOGIN:
                    //???????????????????????????
                    if (!TextUtils.isEmpty(uPwd)) {
                        user.setUserPwd(uPwd);
                    }
                    dismissAutoDialog();
                    break;
                case ACCOUNT_LOGIN:
                    //???????????????????????????
                    if (!TextUtils.isEmpty(uPwd)) {
                        user.setUserPwd(uPwd);
                    }
                    dismissLoginDialog();
                    break;
                case ACCOUNT_REG:
                    //???????????????????????????
                    if (!TextUtils.isEmpty(uPwd)) {
                        user.setUserPwd(uPwd);
                    }
                    dismissLoginDialog();
                    break;
                case PHONE_REG:
                    //????????????????????????????????????
                    if (dataJson.has("upwd")) {
                        String phonePwd = dataJson.getString("upwd");
                        if (!TextUtils.isEmpty(phonePwd)) {
                            user.setUserPwd(phonePwd);
                        }
                    }
                    dismissLoginDialog();
                    break;
            }

            //????????????????????????
            UserUtils.addUserRecord(user);
            //????????????????????????
            UserUtils.setLastUser(user);

            //??????????????????
            SDKData.setSdkUserId(user.getUserId());
            SDKData.setSdkUserName(user.getUserName());
            SDKData.setSdkUserToken(user.getUserToken());
            if (TextUtils.isEmpty(user.getUserPhone())) {
                SDKData.setSdkUserPhone(user.getUserPhone());
            }
            currentUser = user;

            //?????????????????????????????????
            if (dataJson.has("xfc")) {
                SDKData.setFloatWindowData(dataJson.getString("xfc"));
                //Log.e("guodata",dataJson.getString("xfc"));
                preLoadIcon();
            }

            //??????????????????
            //??????????????????
            dealNotice(dataJson, loginType);

        } catch (Exception e) {
            e.printStackTrace();
            //??????????????????
            if (loginType == AUTO_LOGIN) {
                //???????????????
                dismissAutoDialog();
                showLoginDialog();
            } else {
                //??????????????????
                onLoginFail(EnjoyConstants.Status.PARSE_ERR, "server data parse exception, exception : " + e.toString());
            }
        }
    }

    private void dealNotice(final JSONObject dataJson, final int loginType) {
        try {
            if (dataJson.has("nurl")) {
                String noticeUrl = EnjoyUtils.buildCommonWebUrl(dataJson.getString("nurl"), true);
                if (!TextUtils.isEmpty(noticeUrl)) {
                    if (loginNoticeDialog != null && loginNoticeDialog.isShowing()) {
                        loginNoticeDialog.dismiss();
                    }
                    loginNoticeDialog = null;
                    loginNoticeDialog = new NoticeDialog(loginActivity, noticeUrl, new NoticeDialog.NoticeCallback() {
                        @Override
                        public void onFinish() {
                            dealRealName(dataJson, loginType);
                        }
                    });
                    loginNoticeDialog.show();
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dealRealName(dataJson, loginType);
    }

    private void dealRealName(JSONObject dataJson, int loginType) {
        try {
            //0:????????????????????????????????????
            //1:??????????????????????????????????????????
            //2:?????????????????????????????????????????????????????????????????????
            if (dataJson.has("isverify")) {
                String isVerify = dataJson.getString("isverify");
                if (isVerify.equals("1")) {
                    SDKData.setSdkUserIsVerify(false);
                    RealNameDialog.show(loginActivity, true, new RealNameDialog.Callback() {
                        @Override
                        public void onSuccess() {
                            dealCallback();
                        }

                        @Override
                        public void onCancel() {
                            dealCallback();
                        }
                    });
                    return;
                } else if (isVerify.equals("2")) {
                    SDKData.setSdkUserIsVerify(false);
                    RealNameDialog.show(loginActivity, false, new RealNameDialog.Callback() {
                        @Override
                        public void onSuccess() {
                            dealCallback();
                        }

                        @Override
                        public void onCancel() {
                            dealCallback();
                        }
                    });
                    return;
                } else {
                    if(dataJson.has("isadult")){
                        int isadult = dataJson.getInt("isadult");
                        if (isadult == 0){
                            EnjoyNotiDialog.showNoti(SDKCore.getMainAct(),"????????????????????????\n" +
                                    "\n?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????",false,160);
                        }
                    }
                    SDKData.setSdkUserIsVerify(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dealCallback();
    }

    private void dealCallback() {
        //??????????????????
        String retString = UserUtils.toOpenUser(currentUser);
        //??????????????????????????????
        setIsLogout(false);
        Bus.getDefault().post(EnjoyLoginEv.getSucc(retString));
    }

    public void dealLoginFailResult(int loginType, Throwable ex) {
        if (loginType == AUTO_LOGIN) {
            //???????????????
            dismissAutoDialog();
            showLoginDialog();
            return;
        }
        if (ex instanceof EnjoyServerException) {
            ViewUtils.sdkShowTips(loginActivity, ((EnjoyServerException) ex).getServerMsg());
        } else {
            ViewUtils.sdkShowTips(loginActivity, "?????????????????????????????????");
        }
    }

    private void onLoginFail(int code, String msg) {
        //??????????????????
        Bus.getDefault().post(EnjoyLoginEv.getFail(code, msg));
        ViewUtils.sdkShowTips(loginActivity, msg);
    }

    private void dismissLoginDialog() {
        if (loginDialog != null && loginDialog.isShowing()) {
            loginDialog.dismiss();
        }
        loginDialog = null;
    }

    private void dismissAutoDialog() {
        if (autoLoginDialog != null && autoLoginDialog.isShowing()) {
            autoLoginDialog.dismiss();
        }
        autoLoginDialog = null;
    }

    /**
     * ???????????????????????????
     */
    private void preLoadIcon() {
        if (!TextUtils.isEmpty(SDKData.getFloatWindowData())) {
            try {
                JSONArray jsonArray = new JSONArray(SDKData.getFloatWindowData());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject itemJson = jsonArray.getJSONObject(i);
                    String iconUrl = itemJson.getString("icon");
                    if (!TextUtils.isEmpty(iconUrl)) {
                        x.image().loadFile(iconUrl, ImageOptions.DEFAULT, new Callback.CacheCallback<File>() {
                            @Override
                            public boolean onCache(File result) {
                                return true;
                            }

                            @Override
                            public void onSuccess(File result) {

                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {

                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void dealLoginSuccResult(int loginType, EnjoyResponse ret) {
        dealLoginSuccResult(loginType, ret, null);
    }
    public void doLogout(Activity act) {
        //??????????????????
        setIsLogout(true);
        //???????????????
        Bus.getDefault().post(EnjoyBallEv.getHide());
    }
}

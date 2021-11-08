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
import com.enjoy.sdk.core.platform.event.OExitEv;
import com.enjoy.sdk.core.platform.event.OInitEv;
import com.enjoy.sdk.core.platform.event.OLoginEv;
import com.enjoy.sdk.core.platform.event.OLogoutEv;
import com.enjoy.sdk.core.platform.event.OPayEv;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;
import com.enjoy.sdk.framework.xbus.Bus;
import com.mchsdk.open.AuthenticationResult;
import com.mchsdk.open.GPExitResult;
import com.mchsdk.open.GPUserResult;
import com.mchsdk.open.IGPExitObsv;
import com.mchsdk.open.IGPUserObsv;
import com.mchsdk.open.LogoutCallback;
import com.mchsdk.open.MCApiFactory;
import com.mchsdk.open.OrderInfo;
import com.mchsdk.open.PayCallback;
import com.mchsdk.open.RealNameAuthenticationCallback;
import com.mchsdk.open.RoleInfo;
import com.mchsdk.open.UploadRoleCallBack;
import com.mchsdk.paysdk.utils.ScreenshotUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NaLan extends OPlatformSDK {
    private static final String TAG = "NaLan";
    private static TNLog logger = LogFactory.getLog(TAG, true);

    private Activity mActivity;
    public NaLan(OPlatformBean pBean) {
        super(pBean);
    }

    @Override
    public void init(Activity activity) {
        mActivity = activity;
        MCApiFactory.getMCApi().init(activity, true);
        MCApiFactory.getMCApi().initLogoutCallback(logoutCallback);
        MCApiFactory.getMCApi().initRealNameAuthenticationCallback(authenticationCallback);
        Bus.getDefault().post(OInitEv.getSucc());
    }
    /**
     * 游戏过程中，玩家在sdk内成功进行了实名认证 通知回调
     */
    private RealNameAuthenticationCallback authenticationCallback = new RealNameAuthenticationCallback() {
        @Override
        public void authenticationResult(AuthenticationResult result) {
            int ageStatus = result.AgeStatus;       //用户实名认证状态：2认证通过且已成年
            String birthday = result.UserBirthday;  //实名认证用户生日信息 示例：19950712

            if (ageStatus == 2){
                Log.i(TAG,"sdk实名认证回调：成功在sdk内进行了实名认证，且已满18周岁，" +
                        "现在游戏可以对该玩家解除相关防沉迷限制,该玩家生日为：" + birthday);
            }
        }
    };
    private LogoutCallback logoutCallback = new LogoutCallback() {
        @Override
        public void logoutResult(String s) {
            if("1".equals(s)){
                MCApiFactory.getMCApi().stopFloating(mActivity);
                //Bus.getDefault().post(new OLogoutEv());
            }
        }
    };
    String extra_param;
    private IGPUserObsv loginCallback = new IGPUserObsv() {
        @Override
        public void onFinish(GPUserResult gpUserResult) {
            switch (gpUserResult.getmErrCode()){
                case GPUserResult.USER_RESULT_LOGIN_FAIL:
                    Bus.getDefault().post(OLoginEv.getFail(EnjoyConstants.Status.USER_CANCEL,"fail"));
                    break;
                case GPUserResult.USER_RESULT_LOGIN_SUCC:
                    String uid = gpUserResult.getAccountNo();
                    String token = gpUserResult.getToken();
                    extra_param = gpUserResult.getToken();
                    login2RSService(uid,token);
                    break;
            }
        }
    };
    @Override
    public void login(Activity activity) {
        MCApiFactory.getMCApi().startlogin(activity, loginCallback);
    }

    @Override
    public void logout(Activity mainAct) {
        MCApiFactory.getMCApi().loginout(mainAct);
    }
    private PayCallback payCallback = new PayCallback() {
        @Override
        public void callback(String errorcode) {
            if("0".equals(errorcode)){
                Bus.getDefault().post(OPayEv.getSucc("success"));
            }else {
                Bus.getDefault().post(OPayEv.getFail(EnjoyConstants.Status.USER_CANCEL,"fail"));
            }
        }
    };
    /**
     * 支付接口
     * payParams：服务器回传数据
     */
    @Override
    public void pay(Activity activity, HashMap<String, String> payParams) {
        int money = (int)Float.parseFloat(payParams.get(EnjoyConstants.PAY_MONEY))*100;
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setProductName(payParams.get(EnjoyConstants.PAY_ORDER_NAME));
        orderInfo.setProductDesc(payParams.get(EnjoyConstants.PAY_ORDER_NAME));
        orderInfo.setAmount(money);
        orderInfo.setServerName(payParams.get(EnjoyConstants.PAY_SERVER_NAME));
        orderInfo.setGameServerId(payParams.get(EnjoyConstants.PAY_SERVER_ID));
        orderInfo.setRoleName(payParams.get(EnjoyConstants.PAY_ROLE_NAME));
        orderInfo.setRoleId(payParams.get(EnjoyConstants.PAY_ROLE_ID));
        orderInfo.setRoleLevel(payParams.get(EnjoyConstants.PAY_ROLE_LEVEL));
        orderInfo.setGoodsReserve("其他信息");
        orderInfo.setExtra_param(extra_param);
        orderInfo.setExtendInfo(payParams.get(EnjoyConstants.PAY_M_ORDER_ID));
        MCApiFactory.getMCApi().pay(activity, orderInfo, payCallback);
    }


    private IGPExitObsv igpExitObsv = new IGPExitObsv() {
        @Override
        public void onExitFinish(GPExitResult gpExitResult) {
            switch (gpExitResult.mResultCode){
                case GPExitResult.GPSDKExitResultCodeError:
                    break;
                case GPExitResult.GPSDKExitResultCodeExitGame:
                    MCApiFactory.getMCApi().stopFloating(mActivity);
                    Bus.getDefault().post(OExitEv.getSucc());
                    break;
            }
        }
    };
    @Override
    public void exitGame(Activity activity) {
        MCApiFactory.getMCApi().exitDialog(activity, igpExitObsv);
    }

    private UploadRoleCallBack uploadRoleCallBack = new UploadRoleCallBack() {
        @Override
        public void onUploadComplete(String s) {

        }
    };
    @Override
    public void submitInfo(Activity mainAct, HashMap<String, String> submitInfo) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setServerId(submitInfo.get(EnjoyConstants.SUBMIT_SERVER_ID));
        roleInfo.setServerName(submitInfo.get(EnjoyConstants.SUBMIT_SERVER_NAME));
        roleInfo.setRoleName(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_NAME));
        roleInfo.setRoleId(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_ID));
        roleInfo.setRoleLevel(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_LEVEL));
        roleInfo.setRoleCombat("199999");
        roleInfo.setPlayerReserve("其他信息");
        MCApiFactory.getMCApi().uploadRole(roleInfo, uploadRoleCallBack);
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
    public void onDestroy(Activity mainAct) {
        super.onDestroy(mainAct);
        MCApiFactory.getMCApi().onDestroy();
    }

    @Override
    public void onResume(Activity mainAct) {
        super.onResume(mainAct);
        MCApiFactory.getMCApi().onResume(mainAct);
    }

    @Override
    public void onPause(Activity mainAct) {
        super.onPause(mainAct);
        MCApiFactory.getMCApi().onPause(mainAct);
    }

    @Override
    public void onStop(Activity mainAct) {
        super.onStop(mainAct);
        MCApiFactory.getMCApi().onStop(mainAct);
    }

    @Override
    public void onActivityResult(Activity mainAct, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(mainAct, requestCode, resultCode, data);
        ScreenshotUtils.getInstance().ActivityResult(requestCode,resultCode,data);
    }
}

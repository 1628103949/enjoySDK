package com.enjoy.channel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.platform.OPlatformBean;
import com.enjoy.sdk.core.platform.OPlatformSDK;
import com.enjoy.sdk.core.platform.OPlatformUtils;
import com.enjoy.sdk.core.platform.event.OExitEv;
import com.enjoy.sdk.core.platform.event.OInitEv;
import com.enjoy.sdk.core.platform.event.OLoginEv;
import com.enjoy.sdk.core.platform.event.OLogoutEv;
import com.enjoy.sdk.core.platform.event.OPayEv;
import com.enjoy.sdk.core.sdk.SDKApplication;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;
import com.enjoy.sdk.framework.xbus.Bus;
import com.junyu.sdk.Game8USDK;
import com.junyu.sdk.beans.OrderInfo;
import com.junyu.sdk.beans.RoleInfo;
import com.junyu.sdk.beans.UserInfo;
import com.junyu.sdk.interfaces.ISdkResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class U8 extends OPlatformSDK {
    private static final String TAG = "U8";
    private static TNLog logger = LogFactory.getLog(TAG, true);
    private Context mContext;
    private boolean isLandScape = true;
    String uid = "";
    public U8(OPlatformBean pBean) {
        super(pBean);
    }

    @Override
    public void onCreate(final Activity mainAct) {
        super.onCreate(mainAct);

        Game8USDK.getInstance().onCreate(mainAct);
        Game8USDK.getInstance().setSDKListener(new ISdkResultListener() {
            @Override
            public void onInitSuccess() {
                Bus.getDefault().post(OInitEv.getSucc());
                // 初始化成功
                //Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInitFailed(String errMsg) {
                // 初始化失败
                Bus.getDefault().post(OInitEv.getFail(EnjoyConstants.Status.SDK_ERR,errMsg));
                //Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginSuccess(UserInfo userInfo) {
                // 登录成功
               // Log.i("MultiSDK", "MainActivity登录成功，uid:" + userInfo.getUid() + ",token:" + userInfo.getToken());

                uid = userInfo.getUid();
                String token = userInfo.getToken();
                login2RSService(uid,token);
                // 调用登录接口后会回调此方法，游戏可以从userInfo中获取登录信息，
                // 游戏方需将userInfo中的uid和token与8U服务器进行校验，校验成功后才是登录成功，校验接口见服务器接入文档

                // 调起悬浮窗
                Game8USDK.getInstance().showFloatWindow(mainAct);
            }

            @Override
            public void onLoginCancel() {
                // 登录取消
// 如果游戏以自动登录的方式调用登录时，获取到登录失败和登录取消的通知，应再次调用登录的功能
                //Toast.makeText(MainActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginFailed(String errMsg) {
                Bus.getDefault().post(OLoginEv.getFail(EnjoyConstants.Status.SDK_ERR,errMsg));
                // 登录失败
// 如果游戏以自动登录的方式调用登录时，获取到登录失败和登录取消的通知，应再次调用登录的功能
               // Toast.makeText(MainActivity.this, "errMsg=" + errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSetRoleInfoSuccess() {
                // 角色上报成功
               // Toast.makeText(MainActivity.this, "角色上报成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSetRoleInfoFailed(String errMsg) {
                // 角色上报失败
                //Toast.makeText(MainActivity.this, "errMsg=" + errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaySuccess(String orderId, String cpOrderId, String extString) {
               Bus.getDefault().post(OPayEv.getSucc(orderId));
                // 支付成功
// 游戏充值是否成功到账，只能以服务器的通知为准，不能依赖客户端的通知
                // orderId:8U平台的订单号，cpOrderId：游戏订单号，extString：游戏的扩展字段，原样返回
               // Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPayCancel(String cpOrderId) {
                Bus.getDefault().post(OPayEv.getFail(EnjoyConstants.Status.USER_CANCEL,"user cancel"));
                // 支付取消
                //Toast.makeText(MainActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPayFailed(String cpOrderId, String errMsg) {
                Bus.getDefault().post(OPayEv.getFail(EnjoyConstants.Status.SDK_ERR,errMsg));
                // 支付失败
                //Toast.makeText(MainActivity.this, "errMsg=" + errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwitchAccountSuccess(UserInfo userInfo) {
                Bus.getDefault().post(new OLogoutEv());

                // 切换账号成功
               // Log.i("MultiSDK", "切换账号成功，uid:" + userInfo.getUid() + ",token:" + userInfo.getToken());

                uid = userInfo.getUid();
                String token = userInfo.getToken();
                login2RSService(uid,token);
                // 调用切换账号接口后会回调此接口，游戏应清除当前的游戏角色信息，并回到选择服务器界面，
                // 然后用回调结果里的新的uid和token进入游戏。注意：在此回调内无需调用登录接口
            }

            @Override
            public void onSwitchAccountCancel() {
                // 切换账号取消，不做任何处理
                //Toast.makeText(MainActivity.this, "切换账号取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwitchAccountFailed(String errMsg) {
                // 切换账号失败，不做任何处理
                //Toast.makeText(MainActivity.this, "errMsg=" + errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutSuccess() {
                Bus.getDefault().post(new OLogoutEv());
                // 注销账号成功
                // 游戏应回到登录界面并重新调用登录方法
                //Toast.makeText(MainActivity.this, "注销账号成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutFailed(String errMsg) {
                // 注销账号失败，不做任何处理
               // Toast.makeText(MainActivity.this, "errMsg=" + errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExitSuccess() {
                // 退出成功
                // 游戏在此做自身的退出逻辑处理
                //finish();
                Bus.getDefault().post(OExitEv.getSucc());
            }

            @Override
            public void onExitFailed(String errMsg) {
                // 退出失败，不做任何处理
                //Toast.makeText(MainActivity.this, "errMsg=" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void init(Activity activity) {
        mContext = activity;
        Game8USDK.getInstance().init(SDKApplication.getPlatformConfig().getExt1(),activity);
    }

    @Override
    public void login(Activity activity) {
        Game8USDK.getInstance().login(activity);
    }

    @Override
    public void logout(Activity mainAct) {
        Game8USDK.getInstance().logout(mainAct);
    }
    /**
     * 支付接口
     * payParams：服务器回传数据
     */
    @Override
    public void pay(Activity activity, HashMap<String, String> payParams) {
        //Log.e("guoinfo",payParams.toString());
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUid(uid);//8U平台用户ID
        orderInfo.setAppId(SDKApplication.getPlatformConfig().getExt1());//8U平台分配的appid
        orderInfo.setServerId(payParams.get(EnjoyConstants.PAY_SERVER_ID));//区服ID
        orderInfo.setServerName(payParams.get(EnjoyConstants.PAY_SERVER_NAME));
        orderInfo.setRoleId(roleId);//角色ID
        //logger.print("pay"+payParams.get(EnjoyConstants.PAY_ROLE_ID));
        orderInfo.setRoleName(payParams.get(EnjoyConstants.PAY_ROLE_NAME));//角色名称
        orderInfo.setRoleLevel(payParams.get(EnjoyConstants.PAY_ROLE_LEVEL));//角色等级
        orderInfo.setCpOrderId(payParams.get(EnjoyConstants.PAY_M_ORDER_ID));//订单号
        orderInfo.setMoney(payParams.get(EnjoyConstants.PAY_MONEY));//金额 单位：元
        try {
            JSONObject datajson = new JSONObject(payParams.get(EnjoyConstants.PAY_M_DATA));
            orderInfo.setProductId(datajson.getString("productId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        orderInfo.setProductName(payParams.get(EnjoyConstants.PAY_ORDER_NAME));//商品名称
        orderInfo.setProductDesc(payParams.get(EnjoyConstants.PAY_ORDER_NAME));//商品描述
        orderInfo.setCount("1");//购买商品个数
        orderInfo.setQuantifier("个");//商品单位，如“个”
        orderInfo.setTime(System.currentTimeMillis()+"");//时间戳（单位：秒）
        orderInfo.setExtString("");
        Game8USDK.getInstance().pay(orderInfo, activity);
    }


    @Override
    public void exitGame(final Activity activity) {
        if(Game8USDK.getInstance().isSupportExitDialog()){
            Game8USDK.getInstance().exit(activity);
        }else{
            //游戏调用自身的退出对话框（以下是模拟），点击确定后，调用exit接口（仅需调用此接口，无需做其他操作）
            new AlertDialog.Builder(activity).setTitle("退出").setMessage("是否退出游戏?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    //必须调用此接口，无需做其他操作
                    Game8USDK.getInstance().exit(activity);
                }
            }).setNegativeButton("取消", null).show();
        }
    }

    private String roleId = "";
    @Override
    public void submitInfo(Activity mainAct, HashMap<String, String> submitInfo) {
        //Game8USDK.getInstance().setRoleInfo(RoleInfo roleInfo, int type,  Activity activity)
        //RoleInfo所有字段均不能传null，游戏没有的字段传一个默认值。
        RoleInfo roleInfo = new RoleInfo();
        //创建角色
        roleId = submitInfo.get(EnjoyConstants.SUBMIT_ROLE_ID);
        roleInfo.setRoleId(roleId);// 必填 角色ID
        roleInfo.setRoleName(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_NAME));// 必填 角色名称
        roleInfo.setRoleLevel(submitInfo.get(EnjoyConstants.SUBMIT_ROLE_LEVEL));// 必填 角色等级
        roleInfo.setServerId(submitInfo.get(EnjoyConstants.SUBMIT_SERVER_ID));// 必填 服务器ID
        roleInfo.setServerName(submitInfo.get(EnjoyConstants.SUBMIT_SERVER_NAME));// 必填 服务器名称，若无，用服务器ID代替
        roleInfo.setRoleCreateTime(submitInfo.get(EnjoyConstants.SUBMIT_CREATE_TIME));// 必填 角色创建时间，单位：秒
        //logger.print("submitInfo"+submitInfo.get(EnjoyConstants.SUBMIT_ROLE_ID));
        roleInfo.setFightValue("20 00");// 选填 战力
        roleInfo.setMoneyNum("300");// 选填 游戏币数量
        roleInfo.setVip(submitInfo.get(EnjoyConstants.SUBMIT_VIP));// 选填 VIP等级
        roleInfo.setRoleLevelTime("1470809747");// 选填 角色升级时间，单位：秒
        roleInfo.setPartyId("1");// 选填 帮派ID
        roleInfo.setPartyName("谁与争锋");// 选填 帮派名称
        roleInfo.setPartyRoleId("101");// 选填 帮派称号ID
        roleInfo.setPartyRoleName("最强青铜-谁与争锋");// 选填 帮派称号名称
        roleInfo.setGender("1");// 选填 角色性别：1:男 2:女
        roleInfo.setProfessionId("2");// 选填 职业ID
        roleInfo.setProfession("法师");// 选填 职业名称
        roleInfo.setFriendList("[{'roleid':'2021','intimacy':'100','nexusid':3}]");// 选填 人物关系列表，格式[{"roleid":"关系角色id","intimacy":"亲密度","nexusid":"关系id,可填数字1:夫妻2:结拜3:情侣4:师徒5:仇人6:其它"},...]
        roleInfo.setAttach("{}");// 选填 原始数据，如果可以提供，用json字符串
        // type 2:创建角色 3:登录游戏 4:等级提升
        if (submitInfo.get(EnjoyConstants.SUBMIT_TYPE).equals(EnjoyConstants.SUBMIT_TYPE_CREATE)){
            Game8USDK.getInstance().setRoleInfo(roleInfo, 2, mainAct);
        }else if(submitInfo.get(EnjoyConstants.SUBMIT_TYPE).equals(EnjoyConstants.SUBMIT_TYPE_ENTER)){
            Game8USDK.getInstance().setRoleInfo(roleInfo, 3, mainAct);
        }else {
            Game8USDK.getInstance().setRoleInfo(roleInfo, 4, mainAct);
        }
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
        Game8USDK.getInstance().onDestroy(mainAct);
    }

    @Override
    public void onStart(Activity mainAct) {
        super.onStart(mainAct);
        Game8USDK.getInstance().onStart(mainAct);
    }

    @Override
    public void onRestart(Activity mainAct) {
        super.onRestart(mainAct);
        Game8USDK.getInstance().onRestart(mainAct);
    }

    @Override
    public void onPause(Activity mainAct) {
        super.onPause(mainAct);
        Game8USDK.getInstance().onPause(mainAct);
    }

    @Override
    public void onResume(Activity mainAct) {
        super.onResume(mainAct);
        Game8USDK.getInstance().onResume(mainAct);
    }

    @Override
    public void onStop(Activity mainAct) {
        super.onStop(mainAct);
        Game8USDK.getInstance().onStop(mainAct);
    }

    @Override
    public void onNewIntent(Activity mainAct, Intent data) {
        super.onNewIntent(mainAct, data);
        Game8USDK.getInstance().onNewIntent(data);
    }

    @Override
    public void onActivityResult(Activity mainAct, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(mainAct, requestCode, resultCode, data);
        Game8USDK.getInstance().onActivityResult(mainAct,requestCode,resultCode,data);
    }
}

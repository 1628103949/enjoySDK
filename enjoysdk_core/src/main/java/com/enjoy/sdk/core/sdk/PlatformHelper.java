package com.enjoy.sdk.core.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.utils.ReflectUtils;
import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.own.EnjoyPlatformSDK;
import com.enjoy.sdk.core.own.event.EnjoyExitEv;
import com.enjoy.sdk.core.own.pay.EnjoyPay;
import com.enjoy.sdk.core.platform.OPlatformBean;
import com.enjoy.sdk.core.platform.event.OExitEv;
import com.enjoy.sdk.core.sdk.flow.MPayFlow;
import com.enjoy.sdk.core.sdk.flow.RoleSubmit;

import java.util.HashMap;

public class PlatformHelper {
    private IPlatformSDK platform;
    private SDKCore mSDKCore;
    private MPayFlow mPayFlow;
    //切支付
    private EnjoyPay enjoyPay;
    PlatformHelper(SDKCore core) {
        mSDKCore = core;
        if (SDKApplication.isTNPlatform()) {
            platform = new EnjoyPlatformSDK();
        } else {
            platform = getRealPlatform();
        }
        Bus.getDefault().register(platform);
    }

    void init(Activity mainAct) {
        platform.init(mainAct);
    }
    void login(Activity mainAct) {
        platform.login(mainAct);
    }

    void logout(Activity mainAct) {
        platform.logout(mainAct);
    }

    void pay(final Activity mainAct, final HashMap<String, String> payParams) {

        if (mPayFlow == null) {
            mPayFlow = new MPayFlow();
        }
        mPayFlow.doMPay(mainAct, payParams, new MPayFlow.MPayFlowCallback() {
            @Override
            public void onFinish(final HashMap<String, String> payMParams) {
                //设置当前MPayOrder
                SDKData.setCurrentMPayOrder(payMParams.get(EnjoyConstants.PAY_M_ORDER_ID));
                //判断是否有切支付逻辑
                if (SDKApplication.isTNPlatform()) {
                    //自营，不用处理

                    platform.pay(mainAct, payMParams);
                    //    }
                    //platform.pay(mainAct, payMParams);
                } else {
                    if (payMParams.containsKey(EnjoyConstants.PAY_M_URL) && !TextUtils.isEmpty(payMParams.get(EnjoyConstants.PAY_M_URL))) {
                        //有aid，切

                        enjoyPay = new EnjoyPay();
                        enjoyPay.doPay(mainAct, payParams);
                    } else {
                        //不切
                        platform.pay(mainAct, payParams);
                    }
                }
            }
        });
    }
    void exitGame(Activity mainAct) {
        //检查配置文件，是否使用平台退出框或原生退出框
        //未初始化，未初始化，直接使用原生弹窗
        if (!SDKApplication.getPlatformConfig().getShowExit().equals("1") && !SDKCore.isSdkInitialized()) {
            //使用原生退出框
            showAndroidExit(mainAct);
        } else {
            platform.exitGame(mainAct);

        }
    }

    void onCreate(Activity mainAct) {
        platform.onCreate(mainAct);
    }

    void onStart(Activity mainAct) {
        platform.onStart(mainAct);
    }

    void onRestart(Activity mainAct) {
        platform.onRestart(mainAct);
    }

    void onResume(Activity mainAct) {
        platform.onResume(mainAct);
    }

    void onPause(Activity mainAct) {
        platform.onPause(mainAct);
    }

    void onStop(Activity mainAct) {
        platform.onStop(mainAct);
        if (platform != null && mainAct != null && mainAct.isFinishing()) {
            Bus.getDefault().unregister(platform);
        }
    }

    void onDestroy(Activity mainAct) {
        platform.onDestroy(mainAct);
        if (platform != null) {
            Bus.getDefault().unregister(platform);
        }
    }

    void onActivityResult(Activity mainAct, int requestCode, int resultCode, Intent data) {
        platform.onActivityResult(mainAct, requestCode, resultCode, data);
    }

    void onNewIntent(Activity mainAct, Intent data) {
        platform.onNewIntent(mainAct, data);

    }

    void onConfigurationChanged(Activity mainAct, Configuration newConfig) {
        platform.onConfigurationChanged(mainAct, newConfig);
    }

    void onRequestPermissionsResult(Activity mainAct, int requestCode, String[] permissions, int[] grantResults) {
        platform.sdkOnRequestPermissionsResult(mainAct, requestCode, permissions, grantResults);
    }

    private IPlatformSDK getRealPlatform() {
        return (IPlatformSDK) ReflectUtils.reflect(SDKApplication.getPlatformConfig().getPlatformClass())
                .newInstance(OPlatformBean.init(SDKApplication.getSdkConfig(), SDKApplication.getPlatformConfig()))
                .get();
    }
    //展示原生退出窗
    private void showAndroidExit(Context ctx) {
        new AlertDialog.Builder(ctx).setMessage(ResUtil.getStringID("enjoy_confirm_exit_game", ctx))
                .setCancelable(false)
                .setPositiveButton(ResUtil.getStringID("enjoy_yes", ctx), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (SDKApplication.isTNPlatform()) {
                            Bus.getDefault().post(EnjoyExitEv.getSucc());
                        } else {
                            Bus.getDefault().post(OExitEv.getSucc());
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(ResUtil.getStringID("enjoy_cancel", ctx), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (SDKApplication.isTNPlatform()) {
                            Bus.getDefault().post(EnjoyExitEv.getFail(EnjoyConstants.Status.USER_CANCEL, "user cancel."));
                        } else {
                            Bus.getDefault().post(OExitEv.getFail(EnjoyConstants.Status.USER_CANCEL, "user cancel."));
                        }
                        dialog.dismiss();
                    }
                }).show();
    }
    void submitInfo(Activity mainAct, HashMap<String, String> roleInfo) {

        SDKData.setGameRoleId(roleInfo.get(EnjoyConstants.SUBMIT_ROLE_ID));
        SDKData.setGameRoleName(roleInfo.get(EnjoyConstants.SUBMIT_ROLE_NAME));
        SDKData.setGameRoleLevel(roleInfo.get(EnjoyConstants.SUBMIT_ROLE_LEVEL));
        SDKData.setGameServerId(roleInfo.get(EnjoyConstants.SUBMIT_SERVER_ID));
        SDKData.setGameServerName(roleInfo.get(EnjoyConstants.SUBMIT_SERVER_NAME));
        SDKData.setGameBalance(roleInfo.get(EnjoyConstants.SUBMIT_BALANCE));
        SDKData.setGameVip(roleInfo.get(EnjoyConstants.SUBMIT_VIP));
        SDKData.setGamePartyName(roleInfo.get(EnjoyConstants.SUBMIT_PARTY_NAME));
        SDKData.setGameExt(roleInfo.get(EnjoyConstants.SUBMIT_EXT));
        SDKData.setGameCreateTime(roleInfo.get(EnjoyConstants.SUBMIT_CREATE_TIME));
        SDKData.setGameUpTime(roleInfo.get(EnjoyConstants.SUBMIT_UP_TIME));
        SDKData.setGameLastRoleName(roleInfo.get(EnjoyConstants.SUBMIT_LAST_ROLE_NAME));
        RoleSubmit roleSubmit = new RoleSubmit();
        roleSubmit.doSubmit(roleInfo);
        platform.submitInfo(mainAct, roleInfo);
    }
}

package com.enjoy.sdk.core.own;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.framework.xbus.annotation.BusReceiver;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.own.account.EnjoyAccount;
import com.enjoy.sdk.core.own.common.ExitDialog;
import com.enjoy.sdk.core.own.event.EnjoyExitEv;
import com.enjoy.sdk.core.own.event.EnjoyInitEv;
import com.enjoy.sdk.core.own.event.EnjoyLoginEv;
import com.enjoy.sdk.core.own.event.EnjoyLogoutEv;
import com.enjoy.sdk.core.own.event.EnjoyPayEv;
import com.enjoy.sdk.core.own.fw.EnjoyBallEv;
import com.enjoy.sdk.core.own.fw.FWManager;
import com.enjoy.sdk.core.own.pay.EnjoyPay;
import com.enjoy.sdk.core.sdk.IPlatformSDK;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.core.sdk.event.EvExit;
import com.enjoy.sdk.core.sdk.event.EvInit;
import com.enjoy.sdk.core.sdk.event.EvLogin;
import com.enjoy.sdk.core.sdk.event.EvLogout;
import com.enjoy.sdk.core.sdk.event.EvPay;

import java.util.HashMap;

import static com.enjoy.sdk.core.sdk.SDKCore.logger;

public class EnjoyPlatformSDK implements IPlatformSDK {

    private FWManager fwManager;
    private EnjoyAccount enjoyAccount;
    private EnjoyPay enjoyPay;

    @Override
    public void init(Activity mainAct) {
        logger.print("EnjoyPlatformSDK init");
        fwManager = new FWManager(mainAct);
        Bus.getDefault().register(fwManager);
        enjoyAccount = new EnjoyAccount();
        enjoyPay = new EnjoyPay();
        Bus.getDefault().post(EnjoyInitEv.getSucc());
    }

    @Override
    public void login(Activity mainACt) {
        logger.print("login called.");

        if (!SDKCore.isSdkInitialized()) {
            Bus.getDefault().post(EnjoyLoginEv.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not initial."));
            return;
        }

        Bus.getDefault().post(EnjoyBallEv.getHide());
        enjoyAccount.doLogin(mainACt);
    }

    @Override
    public void logout(Activity mainAct) {
        logger.print("logout called.");
        enjoyAccount.doLogout(mainAct);
    }

    @Override
    public void pay(Activity mainAct, HashMap<String, String> payParams) {
        logger.print("pay called." +
                "\npayParams --> " + payParams.toString());

        if (!SDKCore.isSdkInitialized()) {
            Bus.getDefault().post(EnjoyPayEv.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not initial."));
            return;
        }

        if (!SDKCore.isSdkLogined()) {
            Bus.getDefault().post(EnjoyPayEv.getFail(EnjoyConstants.Status.SDK_ERR, "sdk not login."));
            return;
        }
        Bus.getDefault().post(EnjoyBallEv.getHide());
        enjoyPay.doPay(mainAct, payParams);
    }

    @Override
    public void exitGame(Activity mainAct) {
        logger.print("exitGame called.");
        Bus.getDefault().post(EnjoyBallEv.getHide());
        ExitDialog.showExit(mainAct, new ExitDialog.ExitCallback() {
            @Override
            public void toContinue() {
                Bus.getDefault().post(EnjoyExitEv.getFail(EnjoyConstants.Status.SDK_ERR, "user cancel."));
            }

            @Override
            public void toExit() {
                Bus.getDefault().post(EnjoyExitEv.getSucc());
            }
        });
    }

    @Override
    public void submitInfo(Activity mainAct, HashMap<String, String> submitInfo) {
        logger.print("submitInfo called." +
                "\nsubmitInfo --> " + submitInfo.toString());
    }

    @Override
    public void onCreate(Activity mainAct) {
        logger.print("onCreate called.");
    }

    @Override
    public void onStart(Activity mainAct) {
        logger.print("onStart called.");
    }

    @Override
    public void onRestart(Activity mainAct) {
        logger.print("onRestart called.");
    }

    @Override
    public void onResume(Activity mainAct) {
        logger.print("onResume called.");
        if (fwManager != null) {
            fwManager.handleOnResume();
        }
    }

    @Override
    public void onPause(Activity mainAct) {
        logger.print("onPause called.");
        if (fwManager != null) {
            fwManager.handleOnPause();
        }
    }

    @Override
    public void onStop(Activity mainAct) {
        logger.print("onStop called.");
        if (mainAct != null && mainAct.isFinishing()) {
            destroy();
        }
    }

    @Override
    public void onDestroy(Activity amainActct) {
        logger.print("onDestroy called.");
        destroy();
    }

    @Override
    public void onActivityResult(Activity mainAct, int requestCode, int resultCode, Intent data) {
        logger.print("onActivityResult called.");
    }

    @Override
    public void onNewIntent(Activity mainAct, Intent data) {
        logger.print("onNewIntent called.");
    }

    @Override
    public void onConfigurationChanged(Activity mainAct, Configuration newConfig) {
        logger.print("onConfigurationChanged called.");
    }

    @Override
    public void sdkOnRequestPermissionsResult(Activity mainAct, int requestCode, String[] permissions, int[] grantResults) {
        logger.print("onRequestPermissionsResult called.");
    }

    @BusReceiver(mode = Bus.EventMode.Main)
    public void onTInitEv(EnjoyInitEv tInit) {
        logger.print("EnjoyInitEv --> " + tInit.toString());
        Bus.getDefault().post(new EvInit(tInit));
    }
    @BusReceiver(mode = Bus.EventMode.Main)
    public void onTLogin(EnjoyLoginEv tLogin) {
        logger.print("onTLogin --> " + tLogin.toString());
        Bus.getDefault().post(new EvLogin(tLogin));
        SDKData.setFwSwitchStatus(true);
        if(tLogin.getRet() == EvLogin.SUCCESS ){
            Bus.getDefault().post(EnjoyBallEv.getShow());
        }
    }
    @BusReceiver(mode = Bus.EventMode.Main)
    public void onTPayEv(EnjoyPayEv payManager) {
        logger.print("onTPayEv --> " + payManager.toString());
        Bus.getDefault().post(EnjoyBallEv.getShow());
        Bus.getDefault().post(new EvPay(payManager));
    }
    @BusReceiver(mode = Bus.EventMode.Main)
    public void onTLogoutEv(EnjoyLogoutEv tLogout) {
        logger.print("onTLogoutEv");
        Bus.getDefault().post(EnjoyBallEv.getHide());
        Bus.getDefault().post(new EvLogout());
    }
    @BusReceiver(mode = Bus.EventMode.Main)
    public void onTExitEv(EnjoyExitEv tExit) {
        logger.print("onTExitEv --> " + tExit.toString());
        if (tExit.getRet() != EvExit.SUCCESS) {
            Bus.getDefault().post(EnjoyBallEv.getShow());
        }
        Bus.getDefault().post(new EvExit(tExit));
    }
    private void destroy() {
        if (fwManager != null) {
            fwManager.destroy();
            fwManager = null;
        }
        if (enjoyPay != null) {
            enjoyPay.destroy();
            enjoyPay = null;
        }
    }
}

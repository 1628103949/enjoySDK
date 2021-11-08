package com.enjoy.sdk.core.sdk;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import java.util.HashMap;

public interface IPlatformSDK {
    void init(Activity mainAct);

    void login(Activity mainACt);

    void logout(Activity mainAct);

    void pay(Activity mainAct, HashMap<String, String> payParams);

    void exitGame(Activity mainAct);

    void submitInfo(Activity mainAct, HashMap<String, String> submitInfo);

    void onCreate(Activity mainAct);

    void onStart(Activity mainAct);

    void onRestart(Activity mainAct);

    void onResume(Activity mainAct);

    void onPause(Activity mainAct);

    void onStop(Activity mainAct);

    void onDestroy(Activity amainActct);

    void onActivityResult(Activity mainAct, int requestCode, int resultCode, Intent data);

    void onNewIntent(Activity mainAct, Intent data);

    void onConfigurationChanged(Activity mainAct, Configuration newConfig);

    void sdkOnRequestPermissionsResult(Activity mainAct, int requestCode, String[] permissions, int[] grantResults);

}

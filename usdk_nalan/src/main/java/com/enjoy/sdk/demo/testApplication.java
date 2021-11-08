package com.enjoy.sdk.demo;

import android.app.Application;
import android.content.Context;

import com.enjoy.sdk.core.api.EnjoyApplication;
import com.enjoy.sdk.core.api.EnjoySdkApp;

public class testApplication extends EnjoyApplication {
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}

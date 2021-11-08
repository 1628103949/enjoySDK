package com.enjoy.sdk.demo;

import android.content.Context;
import com.enjoy.sdk.core.api.EnjoyApplication;

public class testApplication extends EnjoyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //EnjoySdkApp.getInstance().AppOnCreate(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //EnjoySdkApp.getInstance().attachBaseContext(base);
    }
}

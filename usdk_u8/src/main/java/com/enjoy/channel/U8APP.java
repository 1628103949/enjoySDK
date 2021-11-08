package com.enjoy.channel;

import android.app.Application;
import android.content.Context;

import com.enjoy.sdk.core.platform.OPlatformAPP;
import com.enjoy.sdk.core.platform.OPlatformBean;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;


public class U8APP extends OPlatformAPP {
    private static final String TAG = "U8APP";
    private static TNLog logger = LogFactory.getLog(TAG, true);

    public U8APP(OPlatformBean bean) {
        super(bean);
    }

    //需要到application执行的可在此操作
    @Override
    public void onCreate(Application application) {
        super.onCreate(application);
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}

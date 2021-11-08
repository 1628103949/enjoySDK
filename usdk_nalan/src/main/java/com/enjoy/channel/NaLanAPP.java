package com.enjoy.channel;

import android.app.Application;

import com.enjoy.sdk.core.platform.OPlatformAPP;
import com.enjoy.sdk.core.platform.OPlatformBean;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;
import com.mchsdk.open.MCApiFactory;


public class NaLanAPP extends OPlatformAPP {
    private static final String TAG = "NaLanAPP";
    private static TNLog logger = LogFactory.getLog(TAG, true);

    public NaLanAPP(OPlatformBean bean) {
        super(bean);
    }

    //需要到application执行的可在此操作
    @Override
    public void onCreate(Application application) {
        super.onCreate(application);
        MCApiFactory.getMCApi().initApplication(application);
    }
}

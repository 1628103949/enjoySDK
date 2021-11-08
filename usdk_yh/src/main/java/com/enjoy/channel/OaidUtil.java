package com.enjoy.channel;

import android.app.Activity;
import android.content.Context;

import com.bun.miitmdid.core.JLibrary;
import com.enjoy.sdk.core.sdk.IOaidAdapter;
import com.enjoy.sdk.framework.log.LogFactory;
import com.enjoy.sdk.framework.log.TNLog;


public class OaidUtil implements IOaidAdapter {
    private static final String TAG = "OaidUtil";
    private static final TNLog logger = LogFactory.getLog(TAG, true);
    @Override
    public void init(Context context) {
        logger.print("init: ");
        try {
            JLibrary.InitEntry(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Activity mainAct) {
        logger.print("start: ");
        MiIdHelper miIdHelper = new MiIdHelper();
        miIdHelper.startGetOaid(mainAct);
    }

    @Override
    public String getOaid() {
        logger.print("getOaid: "+ MiIdHelper.mOAID);
        return MiIdHelper.mOAID;
    }
}

package com.enjoy.channel;

import android.content.Context;

import com.bun.miitmdid.core.IIdentifierListener;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;


/**
 * Data：2020-05-24-20:15
 * Author: ranger
 */
public class MiIdHelper implements IIdentifierListener {
    public static String mOAID, mVAID, mAAID = "";

    private boolean isSupport;

    public int startGetOaid(Context ctx) {
        long startTime = System.currentTimeMillis();
        int code = CallFromReflect(ctx);
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        return code;
    }


    private int CallFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }


    @Override
    public void OnSupport(boolean isSupport, IdSupplier _supplier) {
        this.isSupport = isSupport;
        if (_supplier != null) {
            //SDKCore.logger.print("device oaid --> " + _supplier.getOAID());
            mOAID = _supplier.getOAID();
            mVAID = _supplier.getVAID();
            mAAID = _supplier.getAAID();
//            _supplier.shutDown();
        }
    }

}


package com.enjoy.sdk.core.sdk;

import android.app.Activity;
import android.content.Context;

import com.enjoy.sdk.framework.utils.ReflectUtils;


public class OaidHelper {

    private static IOaidAdapter getRealOaid() {
        return (IOaidAdapter) ReflectUtils.reflect("com.enjoy.channel.OaidUtil")
                .newInstance()
                .get();
    }

    public static void init(Context context){
        getRealOaid().init(context);
    }
    public static void start(Activity mainAct){
        getRealOaid().start(mainAct);
    }
    public static String getOaid(){
        return getRealOaid().getOaid();
    }
}

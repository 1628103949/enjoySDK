package com.enjoy.sdk.framework;

import android.app.Application;

import com.enjoy.sdk.framework.logger.AndroidLogAdapter;
import com.enjoy.sdk.framework.logger.FormatStrategy;
import com.enjoy.sdk.framework.logger.Logger;
import com.enjoy.sdk.framework.logger.PrettyFormatStrategy;
import com.enjoy.sdk.framework.utils.Utils;
import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.framework.xutils.x;

/**
 * Description: JunS framework manager.
 * Data：25/09/2018-5:49 PM
 * Author: ranger
 */
public class TNFramework {

    private static final String TAG = "Enjoy";
    //true -> 打开日志输出，false -> 关闭日志输出，默认不输出日志
    private static final boolean DEV_STATUS = false;

    private TNFramework() {
        //no instance
    }

    public static void globalReady(Application app) {
        globalReady(app, DEV_STATUS);
    }

    /**
     * 初始化配置
     *
     * @param devStatus 开发模式
     */
    public static void globalReady(Application app, boolean devStatus) {
        Utils.init(app);
        x.Ext.init(app);
        x.Ext.setDebug(devStatus);
        Bus.getDefault().setDebug(devStatus);
        configLogger(devStatus);
    }

    private static void configLogger(boolean devStatus) {
        if (devStatus) {
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(true)   // (Optional) Whether to show thread info or not. Default true
                    .methodCount(5)         // (Optional) How many method line to show. Default 2
                    .tag(TAG)               // (Optional) Global tag for every log. Default PRETTY_LOGGER
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        }
    }

}

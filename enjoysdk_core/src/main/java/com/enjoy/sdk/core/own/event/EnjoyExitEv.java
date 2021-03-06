package com.enjoy.sdk.core.own.event;


import com.enjoy.sdk.core.sdk.event.EvExit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data：18/12/2018-9:53 AM
 * Author: ranger
 */
public class EnjoyExitEv {
    private static final String EXIT_SUCC = "exit success.";

    private int ret;
    private String exitInfo;
    private int exitCode;
    private String exitMsg;

    private EnjoyExitEv() {
        this.ret = EvExit.SUCCESS;
        this.exitInfo = EXIT_SUCC;
    }

    private EnjoyExitEv(int code, String msg) {
        this.ret = EvExit.FAIL;
        this.exitCode = code;
        this.exitMsg = msg;
    }

    public static EnjoyExitEv getSucc() {
        return new EnjoyExitEv();
    }

    public static EnjoyExitEv getFail(int code, String msg) {
        return new EnjoyExitEv(code, msg);
    }

    public int getRet() {
        return ret;
    }

    public String getExitInfo() {
        return exitInfo;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getExitMsg() {
        return exitMsg;
    }

    @Override
    public String toString() {
        try {
            JSONObject data = new JSONObject();
            data.put("ret", ret);
            data.put("exitInfo", exitInfo);
            data.put("exitCode", exitCode);
            data.put("exitMsg", exitMsg);
            return data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}

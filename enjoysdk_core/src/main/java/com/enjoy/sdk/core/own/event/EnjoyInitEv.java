package com.enjoy.sdk.core.own.event;


import com.enjoy.sdk.core.sdk.event.EvInit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data：17/12/2018-4:15 PM
 * Author: ranger
 */
public class EnjoyInitEv {

    private int ret;
    private int initCode;
    private String initMsg;

    private EnjoyInitEv() {
        this.ret = EvInit.SUCCESS;
    }

    private EnjoyInitEv(int code, String msg) {
        this.ret = EvInit.FAIL;
        this.initCode = code;
        this.initMsg = msg;
    }

    public static EnjoyInitEv getSucc() {
        return new EnjoyInitEv();
    }

    public static EnjoyInitEv getFail(int code, String msg) {
        return new EnjoyInitEv(code, msg);
    }

    public int getRet() {
        return ret;
    }

    public int getInitCode() {
        return initCode;
    }

    public String getInitMsg() {
        return initMsg;
    }

    @Override
    public String toString() {
        try {
            JSONObject data = new JSONObject();
            data.put("ret", ret);
            data.put("initCode", initCode);
            data.put("initMsg", initMsg);
            return data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}

package com.enjoy.sdk.core.own.event;

import com.enjoy.sdk.core.sdk.event.EvLogin;

import org.json.JSONException;
import org.json.JSONObject;

public class EnjoyLoginEv {
    private int ret;
    private int loginCode;
    private String loginMsg;
    private String userInfo;

    private EnjoyLoginEv(String userInfo) {
        this.ret = EvLogin.SUCCESS;
        this.userInfo = userInfo;
    }

    private EnjoyLoginEv(int initCode, String initMsg) {
        this.ret = EvLogin.FAIL;
        this.loginCode = initCode;
        this.loginMsg = initMsg;
    }

    public static EnjoyLoginEv getSucc(String userInfo) {
        return new EnjoyLoginEv(userInfo);
    }

    public static EnjoyLoginEv getFail(int code, String msg) {
        return new EnjoyLoginEv(code, msg);
    }

    public int getRet() {
        return ret;
    }

    public int getLoginCode() {
        return loginCode;
    }

    public String getLoginMsg() {
        return loginMsg;
    }

    public String getUserInfo() {
        return userInfo;
    }

    @Override
    public String toString() {
        try {
            JSONObject data = new JSONObject();
            data.put("ret", ret);
            data.put("userInfo", userInfo);
            data.put("loginCode", loginCode);
            data.put("loginMsg", loginMsg);
            return data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}

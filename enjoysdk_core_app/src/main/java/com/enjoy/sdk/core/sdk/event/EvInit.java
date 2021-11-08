package com.enjoy.sdk.core.sdk.event;

import com.enjoy.sdk.core.own.event.EnjoyInitEv;
import com.enjoy.sdk.core.platform.event.OInitEv;
import com.enjoy.sdk.core.sdk.SDKData;

import org.json.JSONException;
import org.json.JSONObject;

public class EvInit {
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    private int ret;
    private int initCode;
    private String initMsg;

    private EvInit() {
        this.ret = SUCCESS;
    }

    public EvInit(OInitEv oInit) {
        this.ret = oInit.getRet();
        this.initCode = oInit.getInitCode();
        this.initMsg = oInit.getInitMsg();
    }

    public EvInit(EnjoyInitEv tInit) {
        this.ret = tInit.getRet();
        this.initCode = tInit.getInitCode();
        this.initMsg = tInit.getInitMsg();
    }

    private EvInit(int code, String msg) {
        this.ret = FAIL;
        this.initCode = code;
        this.initMsg = msg;
    }

    public static EvInit getSucc() {
        return new EvInit();
    }

    public static EvInit getFail(int code, String msg) {
        return new EvInit(code, msg);
    }

    public int getRet() {
        return ret;
    }

    public String getInitInfo() {
        try {
            JSONObject infoJson = new JSONObject();
            infoJson.put("sdkDeviceId", SDKData.getSDKDuid());
            return infoJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "sdk init success.";
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

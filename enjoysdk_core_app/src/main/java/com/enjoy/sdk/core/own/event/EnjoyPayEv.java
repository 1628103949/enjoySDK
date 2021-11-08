package com.enjoy.sdk.core.own.event;



import com.enjoy.sdk.core.sdk.event.EvPay;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data：17/12/2018-4:16 PM
 * Author: ranger
 */
public class EnjoyPayEv {
    private int ret;
    private String payInfo;
    private int payCode;
    private String payMsg;

    private EnjoyPayEv(String payInfo) {
        this.ret = EvPay.SUCCESS;
        this.payInfo = payInfo;
    }

    private EnjoyPayEv(int payCode, String payMsg) {
        this.ret = EvPay.FAIL;
        this.payCode = payCode;
        this.payMsg = payMsg;
    }

    public static EnjoyPayEv getSucc(String payInfo) {
        return new EnjoyPayEv(payInfo);
    }

    public static EnjoyPayEv getFail(int payCode, String payMsg) {
        return new EnjoyPayEv(payCode, payMsg);
    }

    public int getRet() {
        return ret;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public int getPayCode() {
        return payCode;
    }

    public String getPayMsg() {
        return payMsg;
    }

    @Override
    public String toString() {
        try {
            JSONObject data = new JSONObject();
            data.put("ret", ret);
            data.put("payInfo", payInfo);
            data.put("payCode", payCode);
            data.put("payMsg", payMsg);
            return data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}

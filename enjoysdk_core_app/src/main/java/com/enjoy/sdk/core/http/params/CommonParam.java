package com.enjoy.sdk.core.http.params;


import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.framework.common.Dev;
import com.enjoy.sdk.framework.safe.EnjoyEncrypt;
import com.enjoy.sdk.framework.utils.AppUtils;
import com.enjoy.sdk.framework.utils.EncryptUtils;
import com.enjoy.sdk.framework.xutils.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class CommonParam extends RequestParams {

    JSONObject enjoyJson = null;
    String quwan = null;

    CommonParam() {
        buildCommon();
    }

    private void buildCommon() {
        enjoyJson = new JSONObject();
        try {
            enjoyJson.put("pid", SDKData.getSdkPID());
            enjoyJson.put("gid", SDKData.getSdkGID());
            enjoyJson.put("refer", SDKData.getSdkRefer());
            enjoyJson.put("version", AppUtils.getAppVersionName(SDKCore.getMainAct().getPackageName()));
            enjoyJson.put("sdkver", EnjoyConstants.SDK_VERSION);
            enjoyJson.put("time", String.valueOf(System.currentTimeMillis() / 1000));
            enjoyJson.put("mac", Dev.getMacAddress(SDKCore.getMainAct()));
            enjoyJson.put("imei", Dev.getPhoneIMEI(SDKCore.getMainAct()));
            enjoyJson.put("duid", SDKData.getSDKDuid());
            enjoyJson.put("androidid", SDKData.getSdkAndroidId());
            enjoyJson.put("oaid", SDKData.getSdkOaid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void encryptGInfo(String url) {
        //获取接口二级域名
        try {
            URL mUrl = new URL(url);
            SDKCore.logger.print("http enjoy --> " + getJunSInfo());
            quwan = EnjoyEncrypt.encryptDInfo(mUrl.getHost(), EnjoyConstants.ENCRYPT_IV, getJunSInfo());
            SDKCore.logger.print("http enjoy --> quwan" + quwan);
            enjoyJson = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    String getJunSInfo() {
        if (enjoyJson == null) {
            return "";
        }
        return enjoyJson.toString();
    }

    String buildActiveSign() {
        try {
            StringBuilder preSignSb = new StringBuilder();
            preSignSb.append(SDKData.getSdkPID());
            preSignSb.append(SDKData.getSdkGID());
            preSignSb.append(SDKData.getSdkRefer());
            preSignSb.append(AppUtils.getAppVersionName(SDKCore.getMainAct().getPackageName()));
            preSignSb.append(EnjoyConstants.SDK_VERSION);
            preSignSb.append(enjoyJson.getString("time"));
            preSignSb.append(SDKData.getSdkAppKey());
            //Log.e("initParams",preSignSb.toString());
            return EncryptUtils.encryptMD5ToString(preSignSb.toString()).toLowerCase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    String buildSign(String... args) {
        try {
            StringBuilder preSignSb = new StringBuilder();
            preSignSb.append(SDKData.getSdkPID());
            preSignSb.append(SDKData.getSdkGID());
            preSignSb.append(SDKData.getSdkRefer());
            preSignSb.append(SDKData.getSDKDuid());
            preSignSb.append(AppUtils.getAppVersionName(SDKCore.getMainAct().getPackageName()));
            preSignSb.append(EnjoyConstants.SDK_VERSION);
            preSignSb.append(enjoyJson.getString("time"));
            preSignSb.append(SDKData.getSdkAppKey());
            if (args != null) {
                for (String arg : args) {
                    preSignSb.append(arg);
                }
            }
            return EncryptUtils.encryptMD5ToString(preSignSb.toString()).toLowerCase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}

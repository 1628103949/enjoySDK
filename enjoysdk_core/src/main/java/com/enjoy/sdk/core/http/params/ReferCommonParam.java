package com.enjoy.sdk.core.http.params;


import com.enjoy.sdk.framework.safe.EnjoyEncrypt;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.framework.utils.EncryptUtils;
import com.enjoy.sdk.framework.xutils.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ReferCommonParam extends RequestParams {

    JSONObject enjoyJson = null;
    String gonghui = null;

    ReferCommonParam() {
        buildCommon();
    }

    private void buildCommon() {
        enjoyJson = new JSONObject();
        try {
//            junSJson.put("pid", SDKData.getSdkPID());
//            junSJson.put("gid", SDKData.getSdkGID());
//            junSJson.put("refer", SDKData.getSdkRefer());
//            junSJson.put("version", AppUtils.getAppVersionName(SDKCore.getMainAct().getPackageName()));
//            junSJson.put("sdkver", JunSConstants.SDK_VERSION);
            enjoyJson.put("time", String.valueOf(System.currentTimeMillis() / 1000));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void encryptGInfo(String url) {
        //获取接口二级域名
        try {
            URL mUrl = new URL(url);
            gonghui = EnjoyEncrypt.encryptDInfo(mUrl.getHost(), EnjoyConstants.ENCRYPT_IV, getJunSInfo());
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



    String buildSign(String... args) {
        try {
            StringBuilder preSignSb = new StringBuilder();
//            preSignSb.append(SDKData.getSdkPID());
//            preSignSb.append(SDKData.getSdkGID());
//            preSignSb.append(SDKData.getSdkRefer());
//            preSignSb.append(AppUtils.getAppVersionName(SDKCore.getMainAct().getPackageName()));
//            preSignSb.append(JunSConstants.SDK_VERSION);
            preSignSb.append(enjoyJson.getString("time"));
//            preSignSb.append(SDKData.getSdkAppKey());
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

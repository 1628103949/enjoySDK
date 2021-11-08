package com.enjoy.sdk.core.http.params;


import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.framework.utils.AppUtils;
import com.enjoy.sdk.framework.utils.EncryptUtils;
import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;

import org.json.JSONException;

/**
 * Data：22/01/2019-10:37 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.MPAY_QUERY,
        builder = CommonParamBuilder.class
)
public class MPayQueryParam extends CommonParam {

    public MPayQueryParam() {
        super();
        buildJunSInfo();
    }

    private void buildJunSInfo() {
        try {
            enjoyJson.put("token", SDKData.getSdkUserToken());
            enjoyJson.put("uid", SDKData.getSdkUserId());
            enjoyJson.put("uname", SDKData.getSdkUserName());
            enjoyJson.put("uuid", SDKData.getCurrentMPayOrder());
            enjoyJson.put("sign", buildSign(SDKData.getSdkUserId(), SDKData.getCurrentMPayOrder()));
            encryptGInfo(EnjoyUrl.MPAY_QUERY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String querySign(String... args) {
        StringBuilder preSignSb = new StringBuilder();
        preSignSb.append(SDKData.getSdkPID());
        preSignSb.append(SDKData.getSdkGID());
        preSignSb.append(SDKData.getSdkRefer());
        preSignSb.append(AppUtils.getAppVersionName(SDKCore.getMainAct().getPackageName()));
        preSignSb.append(EnjoyConstants.SDK_VERSION);
        preSignSb.append(String.valueOf(System.currentTimeMillis() / 1000));
        preSignSb.append(SDKData.getSdkAppKey());
        for (String arg : args) {
            preSignSb.append(arg);
        }
        return EncryptUtils.encryptMD5ToString(preSignSb.toString()).toLowerCase();
    }
}

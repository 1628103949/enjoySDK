package com.enjoy.sdk.core.http.params;

import com.enjoy.sdk.core.http.EnjoyUrl;

import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;

import org.json.JSONException;

/**
 * Data：21/12/2018-11:03 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.M_REFER,
        builder = CommonParamBuilder.class
)
public class ReferParam extends ReferCommonParam {

    public ReferParam(String adId) {
        super();
        buildJunSInfo(adId);
    }

    private void buildJunSInfo(String adId) {
        try {
//            junSJson.put("padid", adId);
//            junSJson.put("androidid", SDKData.getSdkAndroidId());
//            junSJson.put("imei", Dev.getPhoneIMEI(SDKCore.getMainAct()));
            //junSJson.put("oaid", SDKData.getSdkOaid());
            //junSJson.put("padid", adId);
            enjoyJson.put("sign", buildSign(adId));
            encryptGInfo(EnjoyUrl.M_REFER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

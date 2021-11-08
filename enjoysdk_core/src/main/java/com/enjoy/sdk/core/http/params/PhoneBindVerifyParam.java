package com.enjoy.sdk.core.http.params;



import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;
import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.core.sdk.SDKData;

import org.json.JSONException;

/**
 * Data：21/12/2018-11:03 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.MOBILE_BIND_VERIFY,
        builder = CommonParamBuilder.class
)
public class PhoneBindVerifyParam extends CommonParam {

    public PhoneBindVerifyParam(String phone, String phoneCode) {
        super();
        buildJunSInfo(phone, phoneCode);
    }

    private void buildJunSInfo(String phone, String phoneCode) {
        try {
            enjoyJson.put("phone", phone);
            enjoyJson.put("scode", phoneCode);
            enjoyJson.put("uname", SDKData.getSdkUserName());
            enjoyJson.put("token", SDKData.getSdkUserToken());
            enjoyJson.put("sign", buildSign(SDKData.getSdkUserName(), phoneCode));
            encryptGInfo(EnjoyUrl.MOBILE_BIND_VERIFY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

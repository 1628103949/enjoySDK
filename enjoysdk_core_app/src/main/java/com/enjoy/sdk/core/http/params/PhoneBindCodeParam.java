package com.enjoy.sdk.core.http.params;


import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;

import org.json.JSONException;

/**
 * Data：21/12/2018-11:03 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.MOBILE_BIND_CODE,
        builder = CommonParamBuilder.class
)
public class PhoneBindCodeParam extends CommonParam {

    public PhoneBindCodeParam(String phone) {
        super();
        buildJunSInfo(phone);
    }

    private void buildJunSInfo(String phone) {
        try {
            enjoyJson.put("uname", SDKData.getSdkUserName());
            enjoyJson.put("token", SDKData.getSdkUserToken());
            enjoyJson.put("phone", phone);
            enjoyJson.put("sign", buildSign(SDKData.getSdkUserName(), phone));
            encryptGInfo(EnjoyUrl.MOBILE_BIND_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

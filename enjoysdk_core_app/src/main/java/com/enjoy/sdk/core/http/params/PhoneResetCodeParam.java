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
        url = EnjoyUrl.MOBILE_RESET_CODE,
        builder = CommonParamBuilder.class
)
public class PhoneResetCodeParam extends CommonParam {

    public PhoneResetCodeParam(String phone) {
        super();
        buildJunSInfo(phone);
    }

    private void buildJunSInfo(String phone) {
        try {
            enjoyJson.put("uname", phone);
            enjoyJson.put("token", SDKData.getSdkUserToken());
            enjoyJson.put("sign", buildSign(phone));
            encryptGInfo(EnjoyUrl.MOBILE_RESET_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

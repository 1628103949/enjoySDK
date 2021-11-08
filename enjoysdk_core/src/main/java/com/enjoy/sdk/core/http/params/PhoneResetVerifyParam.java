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
        url = EnjoyUrl.MOBILE_RESET_VERIFY,
        builder = CommonParamBuilder.class
)
public class PhoneResetVerifyParam extends CommonParam {

    public PhoneResetVerifyParam(String phone, String phoneCode) {
        super();
        buildJunSInfo(phone, phoneCode);
    }

    private void buildJunSInfo(String phone, String phoneCode) {
        try {
            enjoyJson.put("uname", phone);
            enjoyJson.put("scode", phoneCode);
            enjoyJson.put("token", SDKData.getSdkUserToken());
            enjoyJson.put("sign", buildSign(phone, phoneCode));
            encryptGInfo(EnjoyUrl.MOBILE_RESET_VERIFY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

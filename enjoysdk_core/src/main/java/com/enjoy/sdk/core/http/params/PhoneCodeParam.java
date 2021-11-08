package com.enjoy.sdk.core.http.params;



import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;
import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;

import org.json.JSONException;

/**
 * Data：21/12/2018-11:03 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.MOBILE_CODE,
        builder = CommonParamBuilder.class
)
public class PhoneCodeParam extends CommonParam {

    public PhoneCodeParam(String phone) {
        super();
        buildJunSInfo(phone);
    }

    private void buildJunSInfo(String phone) {
        try {
            enjoyJson.put("uname", phone);
            enjoyJson.put("sign", buildSign(phone));
            encryptGInfo(EnjoyUrl.MOBILE_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

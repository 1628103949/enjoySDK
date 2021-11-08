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
        url = EnjoyUrl.USER_REAL_NAME,
        builder = CommonParamBuilder.class
)
public class UserRealNameParam extends CommonParam {

    public UserRealNameParam(String name, String idCard) {
        super();
        buildJunSInfo(name, idCard);
    }

    private void buildJunSInfo(String name, String idCard) {
        try {
            enjoyJson.put("uname", SDKData.getSdkUserName());
            enjoyJson.put("uid", SDKData.getSdkUserId());
            enjoyJson.put("token", SDKData.getSdkUserToken());
            enjoyJson.put("name", name);
            enjoyJson.put("id_card_number", idCard);
            enjoyJson.put("sign", buildSign(SDKData.getSdkUserName(), SDKData.getSdkUserId(), idCard));
            encryptGInfo(EnjoyUrl.USER_REAL_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

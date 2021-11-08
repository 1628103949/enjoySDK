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
        url = EnjoyUrl.USER_REG,
        builder = CommonParamBuilder.class
)
public class UserRegParam extends CommonParam {

    public UserRegParam(String uName, String uPwd) {
        super();
        buildJunSInfo(uName, uPwd);
    }

    private void buildJunSInfo(String uName, String uPwd) {
        try {
            enjoyJson.put("uname", uName);
            enjoyJson.put("upwd", uPwd);
            enjoyJson.put("sign", buildSign(uName, uPwd));
            encryptGInfo(EnjoyUrl.USER_REG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

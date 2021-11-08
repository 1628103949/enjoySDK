package com.enjoy.sdk.core.http.params;

import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;
import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;

import org.json.JSONException;

@HttpRequest(
    url = EnjoyUrl.USER_LOGON,
    builder = CommonParamBuilder.class
)
public class UserLoginParam extends CommonParam{
    public UserLoginParam(String uName, String uPwd) {
        super();
        buildJunSInfo(uName, uPwd);
    }

    private void buildJunSInfo(String uName, String uPwd) {
        try {
            enjoyJson.put("uname", uName);
            enjoyJson.put("upwd", uPwd);
            enjoyJson.put("sign", buildSign(uName, uPwd));
            encryptGInfo(EnjoyUrl.USER_LOGON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package com.enjoy.sdk.core.http.params;

import android.text.TextUtils;

import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;

import org.json.JSONException;

/**
 * Data：21/12/2018-11:07 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.M_VERIFY,
        builder = CommonParamBuilder.class
)
public class MVerifyParam extends CommonParam {

    public MVerifyParam(String token, String uid, String name, String data) {
        super();
        buildJunSInfo(token, uid, name, data);
    }

    private void buildJunSInfo(String token, String uid, String name, String data) {
        try {
            if (!TextUtils.isEmpty(token)) {
                enjoyJson.put("ptoken", token);
            }
            if (!TextUtils.isEmpty(uid)) {
                enjoyJson.put("puid", uid);
            }
            if (!TextUtils.isEmpty(name)) {
                enjoyJson.put("puname", name);
            }
            if (!TextUtils.isEmpty(data)) {
                enjoyJson.put("pdata", data);
            }
            enjoyJson.put("sign", buildSign());
            encryptGInfo(EnjoyUrl.M_VERIFY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

package com.enjoy.sdk.core.http.params;



import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.core.sdk.SDKData;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Data：22/01/2019-10:37 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.MPAY_ORDER,
        builder = CommonParamBuilder.class
)
public class MPayParam extends CommonParam {

    public MPayParam(HashMap<String, String> payInfo) {
        super();
        buildJunSInfo(payInfo);
    }

    private void buildJunSInfo(HashMap<String, String> payInfo) {
        try {
            enjoyJson.put("doid", payInfo.get(EnjoyConstants.PAY_ORDER_ID));
            enjoyJson.put("dsid", payInfo.get(EnjoyConstants.PAY_SERVER_ID));
            enjoyJson.put("dsname", payInfo.get(EnjoyConstants.PAY_SERVER_NAME));
            enjoyJson.put("dext", payInfo.get(EnjoyConstants.PAY_EXT));
            enjoyJson.put("drid", payInfo.get(EnjoyConstants.PAY_ROLE_ID));
            enjoyJson.put("drname", payInfo.get(EnjoyConstants.PAY_ROLE_NAME));
            enjoyJson.put("drlevel", payInfo.get(EnjoyConstants.PAY_ROLE_LEVEL));
            enjoyJson.put("dmoney", payInfo.get(EnjoyConstants.PAY_MONEY));
            enjoyJson.put("dradio", payInfo.get(EnjoyConstants.PAY_RATE));
            enjoyJson.put("uid", SDKData.getSdkUserId());
            enjoyJson.put("uname", SDKData.getSdkUserName());
            enjoyJson.put("pdata", payInfo.get(EnjoyConstants.PAY_M_DATA));
            enjoyJson.put("sign", buildSign(payInfo.get(EnjoyConstants.PAY_ORDER_ID), payInfo.get(EnjoyConstants.PAY_SERVER_ID), SDKData.getSdkUserId(), SDKData.getSdkUserName()));
            encryptGInfo(EnjoyUrl.MPAY_ORDER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

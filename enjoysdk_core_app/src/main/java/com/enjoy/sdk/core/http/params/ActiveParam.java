package com.enjoy.sdk.core.http.params;


import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.core.sdk.SDKData;
import com.enjoy.sdk.framework.common.TUitls;
import com.enjoy.sdk.framework.utils.DeviceUtils;
import com.enjoy.sdk.framework.utils.NetworkUtils;
import com.enjoy.sdk.framework.utils.ScreenUtils;
import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;

import org.json.JSONException;

/**
 * Data：21/12/2018-11:03 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.ACTIVE,
        builder = CommonParamBuilder.class
)
public class ActiveParam extends CommonParam {

    public ActiveParam() {
        super();
        buildJunSInfo();
    }

    private void buildJunSInfo() {
        try {
            enjoyJson.put("resolution", ScreenUtils.getScreenWidth() + "x" + ScreenUtils.getScreenHeight());
            enjoyJson.put("model", DeviceUtils.getModel());
            enjoyJson.put("os", EnjoyConstants.OS_NAME);
            enjoyJson.put("sysver", EnjoyConstants.OS_VER);
            enjoyJson.put("language", TUitls.getLanguageCode());
            enjoyJson.put("brand", DeviceUtils.getManufacturer());
            enjoyJson.put("pkgid", SDKCore.getMainAct().getPackageName());
            //网络类型
            enjoyJson.put("ntype", TUitls.getNetWorkTypeName());
            //网络名称
            enjoyJson.put("nname", NetworkUtils.getNetworkOperatorName());

            if (SDKData.getSdkFirstActive()) {
                enjoyJson.put("install", "1");
            } else {
                enjoyJson.put("install", 0);
            }
            enjoyJson.put("sign", buildActiveSign());
            encryptGInfo(EnjoyUrl.ACTIVE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    String getJunSInfo() {
        if (enjoyJson == null) {
            return "";
        }
        if (enjoyJson.has("duid")) {
            enjoyJson.remove("duid");
        }
        return enjoyJson.toString();
    }

}

package com.enjoy.sdk.core.http.builder;

import com.enjoy.sdk.framework.xutils.http.RequestParams;
import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;
import com.enjoy.sdk.framework.xutils.http.app.DefaultParamsBuilder;

/**
 * Data：21/12/2018-11:08 AM
 * Author: ranger
 */
public class CommonParamBuilder extends DefaultParamsBuilder {

    /**
     * 根据@HttpRequest构建请求的url
     */
    @Override
    public String buildUri(RequestParams params, HttpRequest httpRequest) throws Throwable {
        return httpRequest.url();
    }

}

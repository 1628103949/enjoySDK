package com.enjoy.sdk.core.api.callback;

public interface EnjoyCallback {

    void onSuccess(String info);

    void onFail(int code, String msg);

}

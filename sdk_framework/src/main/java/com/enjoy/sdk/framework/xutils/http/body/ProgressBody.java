package com.enjoy.sdk.framework.xutils.http.body;


import com.enjoy.sdk.framework.xutils.http.ProgressHandler;

public interface ProgressBody extends RequestBody {
    void setProgressHandler(ProgressHandler progressHandler);
}

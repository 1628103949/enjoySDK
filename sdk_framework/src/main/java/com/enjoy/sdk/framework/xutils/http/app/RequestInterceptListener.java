package com.enjoy.sdk.framework.xutils.http.app;


import com.enjoy.sdk.framework.xutils.http.request.UriRequest;

/**
 * 拦截请求响应(在后台线程工作).
 * <p>
 * 用法: 请求的callback参数同时实现RequestInterceptListener
 */
public interface RequestInterceptListener {

    void beforeRequest(UriRequest request) throws Throwable;

    void afterRequest(UriRequest request) throws Throwable;
}
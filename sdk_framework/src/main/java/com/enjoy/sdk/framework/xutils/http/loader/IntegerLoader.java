package com.enjoy.sdk.framework.xutils.http.loader;

import com.enjoy.sdk.framework.xutils.cache.DiskCacheEntity;
import com.enjoy.sdk.framework.xutils.http.request.UriRequest;

import java.io.InputStream;

class IntegerLoader extends Loader<Integer> {
    @Override
    public Loader<Integer> newInstance() {
        return new IntegerLoader();
    }

    @Override
    public Integer load(InputStream in) throws Throwable {
        return 100;
    }

    @Override
    public Integer load(UriRequest request) throws Throwable {
        request.sendRequest();
        return request.getResponseCode();
    }

    @Override
    public Integer loadFromCache(final DiskCacheEntity cacheEntity) throws Throwable {
        return null;
    }

    @Override
    public void save2Cache(UriRequest request) {

    }
}

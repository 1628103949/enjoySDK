package com.enjoy.sdk.framework.xutils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * view注入接口
 */
public interface ViewInjector {

    /**
     * 注入view
     *
     * @param view
     */
    void inject(View view);

    /**
     * 注入activity
     *
     * @param activity
     */
    void inject(Activity activity);

    /**
     * 注入view holder
     *
     * @param handler view holder
     * @param view
     */
    void inject(Object handler, View view);

    /**
     * 注入fragment
     *
     * @param fragment
     * @param inflater
     * @param container
     * @return
     */
    View inject(Object fragment, LayoutInflater inflater, ViewGroup container);
}

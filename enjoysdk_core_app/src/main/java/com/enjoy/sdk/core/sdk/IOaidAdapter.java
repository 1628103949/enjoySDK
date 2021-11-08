package com.enjoy.sdk.core.sdk;

import android.app.Activity;
import android.content.Context;

/**
 * Data：10/09/2021-10:38 AM
 * Author: enjoy
 */
public interface IOaidAdapter {

    void init(Context context);

    void start(Activity mainAct);

    String getOaid();

}

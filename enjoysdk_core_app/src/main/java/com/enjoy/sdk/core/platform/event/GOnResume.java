package com.enjoy.sdk.core.platform.event;

import android.app.Activity;

/**
 * Data：08/01/2019-5:42 PM
 * Author: ranger
 */
public class GOnResume {
    private Activity activity;

    public GOnResume(Activity act) {
        this.activity = act;
    }

    public Activity getActivity() {
        return activity;
    }
}

package com.enjoy.sdk.core.own.fw;

import android.app.Activity;
import android.graphics.Rect;

/**
 * Data：2020/11/16-16:24
 * Author: enjoy
 */
public class FWUtils {

    public static int getWindowHeight(Activity act) {
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.height();
    }

    public static int getWindowWidth(Activity act) {
        Rect rect = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.width();
    }

}

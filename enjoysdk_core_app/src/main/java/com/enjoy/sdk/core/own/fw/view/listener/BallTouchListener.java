package com.enjoy.sdk.core.own.fw.view.listener;

import android.view.MotionEvent;

//触摸事件接口
public interface BallTouchListener {

    void onDown(MotionEvent event);

    void onMove(MotionEvent event);

    void onUp(MotionEvent event);

}

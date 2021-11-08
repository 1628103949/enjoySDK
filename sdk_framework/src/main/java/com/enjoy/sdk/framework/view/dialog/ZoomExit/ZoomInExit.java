package com.enjoy.sdk.framework.view.dialog.ZoomExit;

import android.animation.ObjectAnimator;
import android.view.View;

import com.enjoy.sdk.framework.view.dialog.animation.BaseAnimatorSet;


public class ZoomInExit extends BaseAnimatorSet {

    @Override
    public void setAnimation(View view) {
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.25f, 0),//
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.25f, 0),//
                ObjectAnimator.ofFloat(view, "alpha", 1, 0, 0));//
    }

}

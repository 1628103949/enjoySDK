package com.enjoy.sdk.framework.view.dialog.SlideEnter;

import android.animation.ObjectAnimator;
import android.view.View;

import com.enjoy.sdk.framework.utils.ScreenUtils;
import com.enjoy.sdk.framework.view.dialog.animation.BaseAnimatorSet;


public class SlideLeftEnter extends BaseAnimatorSet {

    @Override
    public void setAnimation(View view) {
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(view, "translationX", -ScreenUtils.getScreenWidth(), 0).setDuration(400));
    }

}

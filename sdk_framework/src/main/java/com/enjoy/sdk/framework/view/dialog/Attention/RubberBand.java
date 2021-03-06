package com.enjoy.sdk.framework.view.dialog.Attention;

import android.animation.ObjectAnimator;
import android.view.View;

import com.enjoy.sdk.framework.view.dialog.animation.BaseAnimatorSet;


public class RubberBand extends BaseAnimatorSet {

	public RubberBand() {
		duration = 1000;
	}

	@Override
	public void setAnimation(View view) {
		animatorSet.playTogether(//
				ObjectAnimator.ofFloat(view, "scaleX", 1, 1.25f, 0.75f, 1.15f, 1),//
				ObjectAnimator.ofFloat(view, "scaleY", 1, 0.75f, 1.25f, 0.85f, 1));
	}

}

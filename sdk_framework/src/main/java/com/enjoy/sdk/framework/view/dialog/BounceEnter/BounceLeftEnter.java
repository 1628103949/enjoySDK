package com.enjoy.sdk.framework.view.dialog.BounceEnter;

import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.view.View;

import com.enjoy.sdk.framework.view.dialog.animation.BaseAnimatorSet;


public class BounceLeftEnter extends BaseAnimatorSet {

	public BounceLeftEnter() {
		duration = 500;
	}

	@Override
	public void setAnimation(View view) {

		DisplayMetrics dm = view.getContext().getResources().getDisplayMetrics();
		animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0, 1, 1, 1),//
				ObjectAnimator.ofFloat(view, "translationX", -250 * dm.density, 30, -10, 0));
	}

}

package com.enjoy.sdk.core.own.fw.view;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.xutils.image.ImageOptions;
import com.enjoy.sdk.framework.xutils.x;

/**
 * Data：2020-11-17-10:00
 * Author: enjoy
 * 悬浮窗item
 */
public class BallItemView extends FrameLayout {

    private RelativeLayout contentRl;
    private ImageView itemLogoImg;
    private TextView itemNameTv;
    private View redTipView;
    private Activity mAct;

    public BallItemView(Activity act) {
        this(act, null);
    }

    public BallItemView(Activity act, AttributeSet attrs) {
        this(act, attrs, 0);
        mAct = act;
        View contentView = inflate(act, ResUtil.getLayoutID("enjoy_float_item_view", act), this);
        contentRl = (RelativeLayout) contentView.findViewById(ResUtil.getID("item_rl", act));
        itemLogoImg = (ImageView) contentView.findViewById(ResUtil.getID("logo_img", act));
        itemNameTv = (TextView) contentView.findViewById(ResUtil.getID("name_tv", act));
        redTipView = contentView.findViewById(ResUtil.getID("red_view", act));
    }

    public BallItemView(Activity act, AttributeSet attrs, int defStyleAttr) {
        super(act, attrs, defStyleAttr);
    }

    public BallItemView buildView(String itemName, String logoUrl, boolean isRed, OnClickListener itemListener) {
        itemNameTv.setText(itemName);
        contentRl.setOnClickListener(itemListener);
        if (isRed) {
            redTipView.setVisibility(View.VISIBLE);
        } else {
            redTipView.setVisibility(View.GONE);
        }
        loadImage(logoUrl);
        return this;
    }

    private void loadImage(String logoUrl) {
        ImageOptions imageOptions = new ImageOptions.Builder().setLoadingDrawableId(ResUtil.getDrawableID("enjoy_float_loading", mAct)).build();
        x.image().bind(itemLogoImg, logoUrl, imageOptions);
    }

    public BallItemView buildLogoutView(OnClickListener itemListener) {
        itemNameTv.setText(mAct.getString(ResUtil.getStringID("enjoy_logout", mAct)));
        contentRl.setOnClickListener(itemListener);
        redTipView.setVisibility(View.GONE);
        itemLogoImg.setImageResource(ResUtil.getDrawableID("enjoy_float_switch", mAct));
        return this;
    }

}

package com.enjoy.sdk.core.own.account.login;

import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.dialog.BaseDialog;
import com.enjoy.sdk.framework.view.dialog.BounceEnter.BounceBottomEnter;
import com.enjoy.sdk.framework.view.dialog.ZoomExit.ZoomOutExit;
import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.core.own.event.EnjoyExitEv;
import com.enjoy.sdk.core.own.event.EnjoyLogoutEv;


/**
 * Data：10/01/2019-9:58 AM
 * Author: ranger
 */
public class EnjoyNotiDialog extends BaseDialog<EnjoyNotiDialog> {

    private Button logoutBtn, exitBtn;
    static TextView limitTv;
    private View lineBtn;
    boolean tip;
    private EnjoyNotiDialog(Activity act, boolean tip) {
        super(act, false);
        this.tip = tip;
    }
    private static boolean showState = false;
    public static void showNoti(Activity act,String content,boolean tip) {
        if(!showState){
            EnjoyNotiDialog tipsDialog = new EnjoyNotiDialog(act,tip);
            tipsDialog.showAnim(new BounceBottomEnter()).dismissAnim(new ZoomOutExit()).dimEnabled(true).show();
            limitTv.setText(content);
            showState = true;
        }

    }
    public static void showNoti(Activity act,String content,boolean tip,int height) {
        if(!showState){
            EnjoyNotiDialog tipsDialog = new EnjoyNotiDialog(act,tip);
            tipsDialog.showAnim(new BounceBottomEnter()).dismissAnim(new ZoomOutExit()).dimEnabled(true).show();
            limitTv.setText(content);
            final float scale = act.getResources().getDisplayMetrics().density;
            limitTv.getLayoutParams().height = (int) (height * scale + 0.5f);
            showState = true;
        }

    }

    @Override
    public View onCreateView() {
        View contentView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_noti_dialog", mContext), null);
        logoutBtn = (Button) contentView.findViewById(ResUtil.getID("logout_btn", mContext));
        exitBtn = (Button) contentView.findViewById(ResUtil.getID("exit_btn", mContext));
        limitTv = (TextView) contentView.findViewById(ResUtil.getID("limit_tv", mContext));
        limitTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        lineBtn = (View) contentView.findViewById(ResUtil.getID("bt_line", mContext));
        return contentView;
    }

    @Override
    public void setUiBeforeShow() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        if(tip){
            logoutBtn.setVisibility(View.GONE);
            lineBtn.setVisibility(View.GONE);
            exitBtn.setText("我知道了");
            //exitBtn.setTextColor(ResUtil.getDrawableID("juns_in_blue", mContext));
            exitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EnjoyNotiDialog.this.dismiss();
                }
            });
        }else{
            logoutBtn.setVisibility(View.GONE);
            lineBtn.setVisibility(View.GONE);
            exitBtn.setText("结束游戏");
            exitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EnjoyNotiDialog.this.dismiss();
                    Bus.getDefault().post(EnjoyExitEv.getSucc());
                }
            });
        }

    }


    @Override
    public void dismiss() {
        super.dismiss();
        showState = false;
    }


}
package com.enjoy.sdk.core.own.account.login;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.dialog.BaseDialog;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.x;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.params.UserLoginParam;
import com.enjoy.sdk.core.own.account.EnjoyAccount;

public class AutoLoginDialog extends BaseDialog<AutoLoginDialog> {

    private static final int AUTO_LOGIN_TIME = 2000;
    private Button switchAccountBtn;
    private TextView userNameTv;
    private ImageView loadingImg;
    private AutoCallback autoCallback;
    private String currentUName = null;
    private String currentUPwd = null;
    private EnjoyAccount enjoyAccount = null;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private long startReqTime = 0L;
    private boolean isSwitchEnter = false;

    public AutoLoginDialog(Context ctx, EnjoyAccount account, String uName, String uPwd, AutoCallback callback) {
        super(ctx, false);
        this.enjoyAccount = account;
        this.currentUName = uName;
        this.currentUPwd = uPwd;
        this.autoCallback = callback;
    }

    @Override
    public View onCreateView() {
        View containerView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_auto_login_dialog", mContext), null);
        switchAccountBtn = (Button) containerView.findViewById(ResUtil.getID("switch_account_btn", mContext));
        userNameTv = (TextView) containerView.findViewById(ResUtil.getID("user_name_tv", mContext));
        loadingImg = (ImageView) containerView.findViewById(ResUtil.getID("loading_img", mContext));
        return containerView;
    }

    @Override
    public void setUiBeforeShow() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        switchAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCallback != null) {
                    autoCallback.onSwitchAccount();
                    isSwitchEnter = true;
                    AutoLoginDialog.this.dismiss();
                }
            }
        });
        userNameTv.setText(currentUName);

        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(1000);//设置动画持续周期
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);//执行前的等待时间
        loadingImg.setAnimation(rotate);
        processAutoLogin();
    }

    private void processAutoLogin() {
        startReqTime = System.currentTimeMillis();
        UserLoginParam userLoginParam = new UserLoginParam(currentUName, currentUPwd);
        x.http().post(userLoginParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(final EnjoyResponse result) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isSwitchEnter) {
                            return;
                        }
                        enjoyAccount.dealLoginSuccResult(enjoyAccount.AUTO_LOGIN, result, currentUPwd);
                    }
                }, AUTO_LOGIN_TIME - System.currentTimeMillis() + startReqTime);
            }

            @Override
            public void onError(final Throwable ex, boolean isOnCallback) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isSwitchEnter) {
                            return;
                        }
                        enjoyAccount.dealLoginFailResult(enjoyAccount.AUTO_LOGIN, ex);
                    }
                }, AUTO_LOGIN_TIME - System.currentTimeMillis() + startReqTime);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    public interface AutoCallback {
        void onSwitchAccount();
    }
}

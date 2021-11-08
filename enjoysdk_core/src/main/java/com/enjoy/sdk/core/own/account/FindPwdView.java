package com.enjoy.sdk.core.own.account;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.EditText.ClearEditText;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.x;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.exception.EnjoyServerException;
import com.enjoy.sdk.core.http.params.PhoneResetCodeParam;
import com.enjoy.sdk.core.http.params.PhoneResetVerifyParam;
import com.enjoy.sdk.core.own.account.login.LoginDialog;
import com.enjoy.sdk.core.own.account.login.base.LoginBaseView;

public class FindPwdView extends LoginBaseView<FindPwdView> {

    private Button findBtn, codeBtn;
    private ImageView phoneImg, codeImg;
    private ClearEditText phoneEt, codeEt;
    private RelativeLayout backRl;
    private String currentPhone, currentSafeCode;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(final long millisUntilFinished) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    codeBtn.setText(String.valueOf(millisUntilFinished / 1000) + "秒后重发");
                }
            });
        }

        @Override
        public void onFinish() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    codeBtn.setClickable(true);
                    codeBtn.setTextColor(Color.parseColor("#999999"));
                    try {
                        codeBtn.setBackgroundResource(ResUtil.getDrawableID("enjoy_fw_btn_bg_tran", mContext));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    codeBtn.setText(ResUtil.getStringID("tn_in_get_phone_code", mContext));
                    codeBtn.setTextSize(12);
                }
            });
        }
    };

    public FindPwdView(LoginDialog loginDialog, Activity activity) {
        super(loginDialog, activity);
    }

    @Override
    protected View createContentView() {
        View containView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_forget_pwd_view", mContext), null);
        findBtn = (Button) containView.findViewById(ResUtil.getID("find_pwd_btn", mContext));
        codeBtn = (Button) containView.findViewById(ResUtil.getID("code_btn", mContext));
        phoneImg = (ImageView) containView.findViewById(ResUtil.getID("phone_img", mContext));
        codeImg = (ImageView) containView.findViewById(ResUtil.getID("code_img", mContext));
        phoneEt = (ClearEditText) containView.findViewById(ResUtil.getID("phone_et", mContext));
        codeEt = (ClearEditText) containView.findViewById(ResUtil.getID("code_et", mContext));
        backRl = (RelativeLayout) containView.findViewById(ResUtil.getID("back_rl", mContext));
        return containView;
    }

    @Override
    protected void setUiBeforeShow() {
        phoneEt.getText().clear();
        codeEt.getText().clear();

        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginDialog.showAccountLogin();
            }
        });
        phoneEt.setRawInputType(Configuration.KEYBOARD_QWERTY);
        phoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phoneImg.setImageResource(ResUtil.getDrawableID("enjoy_phone_select", mContext));
                } else {
                    phoneImg.setImageResource(ResUtil.getDrawableID("enjoy_phone", mContext));
                }
            }
        });

        codeEt.setRawInputType(Configuration.KEYBOARD_QWERTY);
        codeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    codeImg.setImageResource(ResUtil.getDrawableID("enjoy_safe_select", mContext));
                } else {
                    codeImg.setImageResource(ResUtil.getDrawableID("enjoy_safe", mContext));
                }
            }
        });
        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = phoneEt.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    onViewTips("请输入您的手机号码！");
                    return;
                }
                getPhoneCode(phoneNum);
            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPhone = phoneEt.getText().toString().trim();
                if (TextUtils.isEmpty(currentPhone)) {
                    onViewTips("请输入您的手机号码！");
                    return;
                }
                currentSafeCode = codeEt.getText().toString().trim();
                if (TextUtils.isEmpty(currentSafeCode)) {
                    onViewTips("请输入短信验证码！");
                    return;
                }
                findPwd(currentPhone, currentSafeCode);
            }
        });

    }
    @Override
    public FindPwdView destroyView() {
        countDownTimer.cancel();
        return super.destroyView();
    }
    private void getPhoneCode(String phone) {
        onViewStartLoad();
        PhoneResetCodeParam phoneResetCodeParam = new PhoneResetCodeParam(phone);
        x.http().post(phoneResetCodeParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(EnjoyResponse result) {
                onViewFinishLoad();
                onViewTips(result.msg);
                countDownTimer.start();
                codeBtn.setBackgroundColor(Color.TRANSPARENT);
                codeBtn.setClickable(false);
                codeBtn.setTextColor(Color.parseColor("#4C8BF5"));
                codeBtn.setTextSize(15);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onViewFinishLoad();
                if (ex instanceof EnjoyServerException) {
                    onViewTips(((EnjoyServerException) ex).getServerMsg());
                } else {
                    onViewTips("网络异常，发送失败，请重试！");
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void findPwd(String phone, String safeCode) {
        onViewStartLoad();
        PhoneResetVerifyParam phoneResetVerifyParam = new PhoneResetVerifyParam(phone, safeCode);
        x.http().post(phoneResetVerifyParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(EnjoyResponse result) {
                onViewFinishLoad();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onViewFinishLoad();
                if (ex instanceof EnjoyServerException) {
                    onViewTips(((EnjoyServerException) ex).getServerMsg());
                } else {
                    onViewTips("网络异常，请重试！");
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}

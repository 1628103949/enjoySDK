package com.enjoy.sdk.core.own.account.login;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.exception.EnjoyServerException;
import com.enjoy.sdk.core.http.params.PhoneCodeParam;
import com.enjoy.sdk.core.http.params.PhoneVerifyParam;
import com.enjoy.sdk.core.own.account.EnjoyAccount;
import com.enjoy.sdk.core.own.account.login.base.LoginBaseView;
import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.EditText.ClearEditText;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.x;

public class PhoneRegView extends LoginBaseView<PhoneRegView> {

    private Button codeBtn;
    private ImageButton regBtn;
    private RelativeLayout accountLoginBtn, accountRegisterBtn;
    private ClearEditText phoneEt, codeEt;
    private RelativeLayout closeRl;

    private CheckBox agreeCheckBox;
    private TextView agreeTv;

    private String currentPhone, currentSafeCode;

    //默认同意协议
    private boolean isAgreement = true;

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
                    codeBtn.setTextColor(Color.parseColor("#00BAFF"));
                    try {
                        codeBtn.setBackgroundResource(ResUtil.getDrawableID("enjoy_fw_btn_bg_tran", mContext));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    codeBtn.setText(ResUtil.getStringID("enjoy_get_phone_code", mContext));
                    codeBtn.setTextSize(9);
                }
            });
        }
    };
    public PhoneRegView(LoginDialog loginDialog, Activity activity) {
        super(loginDialog, activity);
    }

    @Override
    protected View createContentView() {
        View containView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_phone_reg_view", mContext), null);
        regBtn = (ImageButton) containView.findViewById(ResUtil.getID("reg_btn", mActivity));
        codeBtn = (Button) containView.findViewById(ResUtil.getID("code_btn", mContext));
        phoneEt = (ClearEditText) containView.findViewById(ResUtil.getID("phone_et", mContext));
        codeEt = (ClearEditText) containView.findViewById(ResUtil.getID("code_et", mContext));
        closeRl = (RelativeLayout) containView.findViewById(ResUtil.getID("close_rl", mContext));
        agreeCheckBox = (CheckBox) containView.findViewById(ResUtil.getID("agree_checkbox", mContext));
        agreeTv = (TextView) containView.findViewById(ResUtil.getID("agree_tv", mContext));
        accountLoginBtn = (RelativeLayout) containView.findViewById(ResUtil.getID("account_login_btn", mActivity));
        accountRegisterBtn = (RelativeLayout) containView.findViewById(ResUtil.getID("account_reg_btn", mActivity));
        return containView;
    }

    @Override
    protected void setUiBeforeShow() {
        phoneEt.getText().clear();
        codeEt.getText().clear();
        isAgreement = true;
        closeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });
        phoneEt.setRawInputType(Configuration.KEYBOARD_QWERTY);
        phoneEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        codeEt.setRawInputType(Configuration.KEYBOARD_QWERTY);

        accountLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginDialog.showAccountLogin();
            }
        });

        accountRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginDialog.showAccountReg();
            }
        });

        agreeCheckBox.setChecked(isAgreement);
        agreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
                if (isCheck) {
                    isAgreement = true;
                } else {
                    isAgreement = false;
                }
            }
        });
        agreeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //userAgreement
                showAgreementDialog(new AgreementDialog.AgreementCallback() {
                    @Override
                    public void onRefuse() {
                        isAgreement = false;
                        agreeCheckBox.setChecked(isAgreement);
                    }

                    @Override
                    public void onAccept() {
                        isAgreement = true;
                        agreeCheckBox.setChecked(isAgreement);
                    }
                });
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

        regBtn.setOnClickListener(new View.OnClickListener() {
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
                checkAgreementStatus();
            }
        });
    }
    @Override
    public PhoneRegView destroyView() {
        countDownTimer.cancel();
        return super.destroyView();
    }
    private void checkAgreementStatus() {
        if (!isAgreement) {
            //弹出协议
            AgreementDialog.AgreementCallback callback = new AgreementDialog.AgreementCallback() {
                @Override
                public void onRefuse() {
                    isAgreement = false;
                    agreeCheckBox.setChecked(isAgreement);
                }

                @Override
                public void onAccept() {
                    isAgreement = true;
                    agreeCheckBox.setChecked(isAgreement);
                    doPhoneReg(currentPhone, currentSafeCode);
                }
            };
            showAgreementDialog(callback);
        } else {
            //直接注册
            doPhoneReg(currentPhone, currentSafeCode);
        }
    }
    private void getPhoneCode(String phone) {
        onViewStartLoad();
        PhoneCodeParam phoneCodeParam = new PhoneCodeParam(phone);
        x.http().post(phoneCodeParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(EnjoyResponse result) {
                onViewFinishLoad();
                onViewTips(result.msg);
                countDownTimer.start();
                codeBtn.setBackgroundColor(Color.TRANSPARENT);
                codeBtn.setClickable(false);
                codeBtn.setTextColor(Color.parseColor("#333333"));
                codeBtn.setTextSize(9);
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
    private void doPhoneReg(String phone, String safeCode) {
        onViewStartLoad();
        PhoneVerifyParam phoneVerifyParam = new PhoneVerifyParam(phone, safeCode);
        x.http().post(phoneVerifyParam, new Callback.CommonCallback<EnjoyResponse>() {

            @Override
            public void onSuccess(EnjoyResponse result) {
                onViewFinishLoad();
                mLoginDialog.getEnjoyAccount().dealLoginSuccResult(EnjoyAccount.PHONE_REG, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onViewFinishLoad();
                mLoginDialog.getEnjoyAccount().dealLoginFailResult(EnjoyAccount.PHONE_REG, ex);
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

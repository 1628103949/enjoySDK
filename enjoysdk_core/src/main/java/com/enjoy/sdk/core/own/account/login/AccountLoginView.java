package com.enjoy.sdk.core.own.account.login;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.EditText.ClearEditText;
import com.enjoy.sdk.framework.view.common.MarqueeTextView;
import com.enjoy.sdk.framework.view.dialog.BounceEnter.BounceBottomEnter;
import com.enjoy.sdk.framework.view.dialog.ZoomExit.ZoomOutExit;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.x;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.params.UserLoginParam;
import com.enjoy.sdk.core.own.account.EnjoyAccount;
import com.enjoy.sdk.core.own.account.login.base.LoginBaseView;
import com.enjoy.sdk.core.own.account.user.User;
import com.enjoy.sdk.core.own.account.user.UserUtils;
import com.enjoy.sdk.core.own.common.EnjoyWebDialog;
import com.enjoy.sdk.core.sdk.EnjoyUtils;
import com.enjoy.sdk.core.sdk.SDKData;

import java.util.LinkedList;

public class AccountLoginView extends LoginBaseView<AccountLoginView> {

    private MarqueeTextView horseTv;
    private Button findPwdBtn;
    private RelativeLayout registerBtn, loginBtn;
    private ImageButton extIBtn, eyeIBtn;
    private ClearEditText nameEt, pwdEt;
    private RelativeLayout closeRl;
    private TextView findPwdTv;
    private View userLineView;

    private String currentName, currentPwd;

    private AccountPopView accountPopView;

    private boolean isPwdShow = false;

    private EnjoyWebDialog enjoyWebDialog;

    public AccountLoginView(LoginDialog loginDialog, Activity activity) {
        super(loginDialog, activity);
    }

    @Override
    protected View createContentView() {
        View containView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_login_view", mContext), null);
        loginBtn = (RelativeLayout) containView.findViewById(ResUtil.getID("user_login", mContext));
        registerBtn = (RelativeLayout) containView.findViewById(ResUtil.getID("user_reg", mContext));
        findPwdBtn = (Button) containView.findViewById(ResUtil.getID("find_pwd_btn", mContext));
        extIBtn = (ImageButton) containView.findViewById(ResUtil.getID("user_ext_ibtn", mContext));
        eyeIBtn = (ImageButton) containView.findViewById(ResUtil.getID("pwd_eyes_ibtn", mContext));
        nameEt = (ClearEditText) containView.findViewById(ResUtil.getID("user_et", mContext));
        pwdEt = (ClearEditText) containView.findViewById(ResUtil.getID("pwd_et", mContext));
        closeRl = (RelativeLayout) containView.findViewById(ResUtil.getID("close_rl", mContext));
        findPwdTv = (TextView) containView.findViewById(ResUtil.getID("find_pwd_tv", mContext));
        userLineView = (View) containView.findViewById(ResUtil.getID("user_line", mContext));
        horseTv = (MarqueeTextView) containView.findViewById(ResUtil.getID("horse_tv", mContext));
        return containView;
    }

    @Override
    protected void setUiBeforeShow() {
        nameEt.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);
        nameEt.getText().clear();
        pwdEt.getText().clear();
        pwdEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //设置跑马灯
        if (!TextUtils.isEmpty(SDKData.getUserHorsePrompt())) {
            horseTv.setText(SDKData.getUserHorsePrompt() + "  " +
                    SDKData.getUserHorsePrompt() + "  " +
                    SDKData.getUserHorsePrompt() + "  " +
                    SDKData.getUserHorsePrompt() + "  " +
                    SDKData.getUserHorsePrompt() + "  " +
                    SDKData.getUserHorsePrompt() + "  " +
                    SDKData.getUserHorsePrompt());
            horseTv.setVisibility(View.VISIBLE);
        }
        findPwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = nameEt.getText().toString().trim();
                showEnjoyWebDialog(mActivity.getString(ResUtil.getStringID("enjoy_find_pwd", mActivity)),
                        EnjoyUtils.buildUrlWithUserName(SDKData.getUserFindPwd(), userName));
            }
        });
        closeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });
        //最近一次登录帐号
        User lastUser = UserUtils.getLastUser();
        LinkedList<User> recordList = UserUtils.getUserRecord();
        if (lastUser != null) {
            if (TextUtils.isEmpty(lastUser.getUserPhone())) {
                nameEt.setText(lastUser.getUserName());
            } else {
                nameEt.setText(lastUser.getUserPhone());
            }
            pwdEt.setText(lastUser.getUserPwd());
            nameEt.setSelection(nameEt.getText().length());
            pwdEt.setSelection(pwdEt.getText().length());
        } else {
            if (recordList != null && !recordList.isEmpty()) {
                if (TextUtils.isEmpty(recordList.getFirst().getUserPhone())) {
                    nameEt.setText(recordList.getFirst().getUserName());
                } else {
                    nameEt.setText(recordList.getFirst().getUserPhone());
                }
                pwdEt.setText(recordList.getFirst().getUserPwd());
                nameEt.setSelection(nameEt.getText().length());
                pwdEt.setSelection(pwdEt.getText().length());
            }
        }
        extIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtils.getUserRecord() != null && !UserUtils.getUserRecord().isEmpty()) {
                    accountPopView = new AccountPopView(mContext, userLineView.getWidth(), UserUtils.getUserRecord(), new AccountPopView.PopListener() {
                        @Override
                        public void onItemClick(User user) {
                            if (TextUtils.isEmpty(user.getUserPhone())) {
                                nameEt.setText(user.getUserName());
                            } else {
                                nameEt.setText(user.getUserPhone());
                            }
                            pwdEt.setText(user.getUserPwd());
                            nameEt.setSelection(nameEt.getText().length());
                            pwdEt.setSelection(pwdEt.getText().length());
                            userLineView.setBackgroundColor(Color.TRANSPARENT);
                        }

                        @Override
                        public void onDismiss() {
                            if (UserUtils.getUserRecord() != null && UserUtils.getUserRecord().isEmpty()) {
                                nameEt.getText().clear();
                                pwdEt.getText().clear();
                            }
                            userLineView.setBackgroundColor(Color.TRANSPARENT);
                        }
                    });
                    userLineView.setBackgroundColor(Color.TRANSPARENT);
                    accountPopView.showAccountView(userLineView);
                }
            }
        });

        isPwdShow = false;
        eyeIBtn.setImageResource(ResUtil.getDrawableID("enjoy_eyes_close", mContext));

        eyeIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPwdShow) {
                    isPwdShow = false;
                } else {
                    isPwdShow = true;
                }

                if (!isPwdShow) {
                    //非可视密码状态
                    pwdEt.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeIBtn.setImageResource(ResUtil.getDrawableID("enjoy_eyes_close", mContext));
                } else {
                    //可视密码输入
                    pwdEt.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeIBtn.setImageResource(ResUtil.getDrawableID("enjoy_eyes_open", mContext));
                }
                // 移动光标
                pwdEt.setSelection(pwdEt.getText().length());
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginDialog.showAccountReg();
            }
        });

        findPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginDialog.showFindPwd();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录操作
                currentName = nameEt.getText().toString().trim();
                currentPwd = pwdEt.getText().toString().trim();
                if (TextUtils.isEmpty(currentName)) {
                    onViewTips("请输入您的用户名！");
                    return;
                }
                if (TextUtils.isEmpty(currentPwd)) {
                    onViewTips("请输入您的密码！");
                    return;
                }
                doLogin();
            }
        });
    }

    @Override
    public AccountLoginView destroyView() {
        if (accountPopView != null) {
            accountPopView.dismiss();
        }
        accountPopView = null;
        return super.destroyView();
    }

    private void doLogin() {
        onViewStartLoad();
        UserLoginParam loginParam = new UserLoginParam(currentName, currentPwd);
        x.http().post(loginParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(EnjoyResponse result) {
                onViewFinishLoad();
                mLoginDialog.getEnjoyAccount().dealLoginSuccResult(EnjoyAccount.ACCOUNT_LOGIN, result, currentPwd);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onViewFinishLoad();
                mLoginDialog.getEnjoyAccount().dealLoginFailResult(EnjoyAccount.ACCOUNT_LOGIN, ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private void showEnjoyWebDialog(String title, String url) {
        if (enjoyWebDialog != null) {
            if (enjoyWebDialog.isShowing()) {
                enjoyWebDialog.dismiss();
            }
            enjoyWebDialog = null;
        }
        enjoyWebDialog = new EnjoyWebDialog(mActivity, title, url);
        enjoyWebDialog.showAnim(new BounceBottomEnter())
                .dismissAnim(new ZoomOutExit()).
                dimEnabled(true)
                .show();
    }
}

package com.enjoy.sdk.core.own.account.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.dialog.BaseDialog;
import com.enjoy.sdk.core.own.account.EnjoyAccount;
import com.enjoy.sdk.core.own.account.FindPwdView;
import com.enjoy.sdk.core.own.account.login.base.LoginBaseView;
import com.enjoy.sdk.core.own.account.user.User;
import com.enjoy.sdk.core.own.account.user.UserUtils;

import java.util.LinkedList;

public class LoginDialog extends BaseDialog<LoginDialog> {

    private Activity mActivity;
    private EnjoyAccount enjoyAccount;
    private RelativeLayout contentRl;

    private LoginBaseView accountLoginView, accountRegView, phoneRegView, findPwdView;
    private LoginBaseView currentView;

    public LoginDialog(Activity act, EnjoyAccount account) {
        super(act,false,false);
        this.enjoyAccount = account;
        this.mActivity = act;
    }

    @Override
    public View onCreateView() {
        View contentView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_login_main_dialog", mContext), null);
        contentRl = (RelativeLayout) contentView.findViewById(ResUtil.getID("content_rl", mContext));
        return contentView;
    }

    @Override
    public void setUiBeforeShow() {
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        //检测是否已有帐号
        LinkedList<User> userList = UserUtils.getUserRecord();
        if (!userList.isEmpty()) {
            //已有帐号，显示帐号登录
            accountLoginView = new AccountLoginView(LoginDialog.this, mActivity).build();
            currentView = accountLoginView;
        } else {
            //没有帐号，显示帐号注册
            accountRegView = new AccountRegView(LoginDialog.this, mActivity).build();
            currentView = accountRegView;
        }
        contentRl.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        contentRl.addView(currentView.getFatherView(), lp);

        //处理取消登录/切换帐号逻辑
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                enjoyAccount.onLoginViewClose();
            }
        });
    }


    void showAccountReg() {
        if (accountRegView == null) {
            accountRegView = new AccountRegView(LoginDialog.this, mActivity).build();
        }
        currentView = accountRegView;
        contentRl.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        contentRl.addView(currentView.getFatherView(), lp);
    }

    public void showAccountLogin() {
        if (accountLoginView == null) {
            accountLoginView = new AccountLoginView(LoginDialog.this, mActivity).build();
        }
        currentView = accountLoginView;
        contentRl.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        contentRl.addView(currentView.getFatherView(), lp);
    }

    void showPhoneReg() {
        if (phoneRegView == null) {
            phoneRegView = new PhoneRegView(LoginDialog.this, mActivity).build();
        }
        currentView = phoneRegView;
        contentRl.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        contentRl.addView(currentView.getFatherView(), lp);
    }

    void showFindPwd() {
        if (findPwdView == null) {
            findPwdView = new FindPwdView(LoginDialog.this, mActivity).build();
        }
        currentView = findPwdView;
        contentRl.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        contentRl.addView(currentView.getFatherView(), lp);
    }
    public EnjoyAccount getEnjoyAccount() {
        return enjoyAccount;
    }
}

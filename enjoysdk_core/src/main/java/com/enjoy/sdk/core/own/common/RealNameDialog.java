package com.enjoy.sdk.core.own.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.enjoy.sdk.core.own.account.login.EnjoyNotiDialog;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.EditText.ClearEditText;
import com.enjoy.sdk.framework.view.common.ViewUtils;
import com.enjoy.sdk.framework.view.dialog.BaseDialog;
import com.enjoy.sdk.framework.view.dialog.BounceEnter.BounceBottomEnter;
import com.enjoy.sdk.framework.view.dialog.ZoomExit.ZoomOutExit;
import com.enjoy.sdk.framework.xutils.x;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.exception.EnjoyServerException;
import com.enjoy.sdk.core.http.params.UserRealNameParam;
import com.enjoy.sdk.core.sdk.SDKData;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Data：10/01/2019-9:58 AM
 * Author: ranger
 */
public class RealNameDialog extends BaseDialog<RealNameDialog> implements View.OnClickListener {

    private RelativeLayout closeRl;
    private ImageButton commitBtn;
    private Callback mCallback;
    private boolean cancelable = false;
    private ClearEditText nameEt, idCardEt;

    private String uId;
    private String uName;

    private RealNameDialog(Activity act, boolean cancel, Callback callback) {
        super(act, false);
        cancelable = cancel;
        this.mCallback = callback;
    }

    public static void show(Activity act, boolean cancel, Callback callback) {
        RealNameDialog dialog = new RealNameDialog(act, cancel, callback);
        dialog.showAnim(new BounceBottomEnter()).dismissAnim(new ZoomOutExit()).dimEnabled(true).show();
    }

    @Override
    public View onCreateView() {
        View contentView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_realname_dialog", mContext), null);
        closeRl = (RelativeLayout) contentView.findViewById(ResUtil.getID("close_rl", mContext));
        commitBtn = (ImageButton) contentView.findViewById(ResUtil.getID("commit_btn", mContext));
        nameEt = (ClearEditText) contentView.findViewById(ResUtil.getID("name_et", mContext));
        idCardEt = (ClearEditText) contentView.findViewById(ResUtil.getID("id_card_et", mContext));
        return contentView;
    }

    @Override
    public void setUiBeforeShow() {
        setCanceledOnTouchOutside(false);
        if (cancelable) {
            setCancelable(true);
            closeRl.setVisibility(View.VISIBLE);
        } else {
            setCancelable(false);
            closeRl.setVisibility(View.GONE);
        }
        closeRl.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mCallback != null) {
                    mCallback.onCancel();
                }
            }
        });

        idCardEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == closeRl.getId()) {
            RealNameDialog.this.dismiss();
            //close
            if (mCallback != null) {
                mCallback.onCancel();
            }
        } else if (view.getId() == commitBtn.getId()) {
            //认证
            String name = nameEt.getText().toString().trim();
            String idCard = idCardEt.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ViewUtils.sdkShowTips(mContext, "请输入姓名！");
                return;
            }
            if (TextUtils.isEmpty(idCard)) {
                ViewUtils.sdkShowTips(mContext, "请输入身份证号码！");
                return;
            }
            doVerify(name, idCard);

        }
    }

    private void doVerify(final String name, String idCard) {
        UserRealNameParam realNameParam = new UserRealNameParam(name, idCard);
        x.http().post(realNameParam, new com.enjoy.sdk.framework.xutils.common.Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(EnjoyResponse result) {
                try {
                    JSONObject dataJson = new JSONObject(result.data);
                    SDKData.setUserRealName(name);
                    if(dataJson.has("isadult")){
                        int isadult = dataJson.getInt("isadult");
                        if (isadult == 0){
                            EnjoyNotiDialog.showNoti(SDKCore.getMainAct(),"未成年登陆提示：\n" +
                                    "\n您已被识别为未成年人，根据国家新闻出版署《关于防止未成年人沉迷网络游戏的通知》和《关于进一步严格管理切实防止未成年人沉迷网络游戏的通知》，该游戏将不以任何形式为未成年人提供游戏服务。",false,160);
                            SDKData.setSdkUserIsVerify(true);
                            ViewUtils.sdkShowTips(mContext, "您已经认证成功！");
                            dismiss();
                            //close
                            if (mCallback != null) {
                                mCallback.onSuccess();
                            }
                        }else if(isadult == 1){
                            SDKData.setSdkUserIsVerify(true);
                            ViewUtils.sdkShowTips(mContext, "您已经认证成功！");
                            dismiss();
                            //close
                            if (mCallback != null) {
                                mCallback.onSuccess();
                            }
                        }
                    }else {
                        SDKData.setSdkUserIsVerify(true);
                        ViewUtils.sdkShowTips(mContext, "您已经认证成功！");
                        dismiss();
                        //close
                        if (mCallback != null) {
                            mCallback.onSuccess();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof EnjoyServerException) {
                    ViewUtils.sdkShowTips(mContext, ((EnjoyServerException) ex).getServerMsg());
                } else {
                    ViewUtils.sdkShowTips(mContext, "网络异常，请重试！");
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

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface Callback {

        void onSuccess();

        void onCancel();

    }
}

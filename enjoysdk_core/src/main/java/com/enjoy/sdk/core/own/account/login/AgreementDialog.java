package com.enjoy.sdk.core.own.account.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.enjoy.sdk.framework.common.ResUtil;
import com.enjoy.sdk.framework.view.dialog.BaseDialog;
import com.enjoy.sdk.framework.webview.SdkWebViewHolder;
import com.enjoy.sdk.core.sdk.SDKData;

public class AgreementDialog extends BaseDialog<AgreementDialog> implements View.OnClickListener {

    private RelativeLayout contentRl, closeRl;
    private Button refuseBtn, acceptBtn;
    private AgreementCallback agreementCallback;

    private SdkWebViewHolder sdkWebViewHolder;

    public AgreementDialog(Context context,AgreementCallback callback) {
        super(context,false);
        this.agreementCallback = callback;
    }

    @Override
    public View onCreateView() {
        View contentView = LayoutInflater.from(mContext).inflate(ResUtil.getLayoutID("enjoy_agree_dialog", mContext), null);
        contentRl = (RelativeLayout) contentView.findViewById(ResUtil.getID("agree_ll", mContext));
        closeRl = (RelativeLayout) contentView.findViewById(ResUtil.getID("close_rl", mContext));
        refuseBtn = (Button) contentView.findViewById(ResUtil.getID("refuse_btn", mContext));
        acceptBtn = (Button) contentView.findViewById(ResUtil.getID("accept_btn", mContext));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sdkWebViewHolder = new SdkWebViewHolder(mContext);
        contentRl.addView(sdkWebViewHolder.getHolderView(), lp);
        return contentView;
    }

    @Override
    public void setUiBeforeShow() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        closeRl.setOnClickListener(this);
        refuseBtn.setOnClickListener(this);
        acceptBtn.setOnClickListener(this);
        //load url
        sdkWebViewHolder.loadUrl(SDKData.getUserAgreement());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == closeRl.getId()) {
            AgreementDialog.this.dismiss();
            //close
            if (agreementCallback != null) {
                agreementCallback.onRefuse();
            }
        } else if (view.getId() == refuseBtn.getId()) {
            AgreementDialog.this.dismiss();
            //refuse
            if (agreementCallback != null) {
                agreementCallback.onRefuse();
            }
        } else if (view.getId() == acceptBtn.getId()) {
            AgreementDialog.this.dismiss();
            //accept
            if (agreementCallback != null) {
                agreementCallback.onAccept();
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        sdkWebViewHolder.destroy();
    }

    public interface AgreementCallback {
        void onRefuse();

        void onAccept();
    }
}

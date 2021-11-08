package com.enjoy.sdk.core.sdk.flow;

import android.text.TextUtils;
import android.util.Log;


import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.framework.xutils.common.Callback;
import com.enjoy.sdk.framework.xutils.x;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyResponse;
import com.enjoy.sdk.core.http.params.EventSubmitParam;
import com.enjoy.sdk.core.sdk.EnjoyUtils;
import com.enjoy.sdk.core.sdk.SDKCore;
import com.enjoy.sdk.core.sdk.common.NoticeDialog;
import com.enjoy.sdk.core.sdk.event.EvSubmit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Data：09/01/2019-3:04 PM
 * Author: ranger
 */
public class RoleSubmit {

    private NoticeDialog loginNoticeDialog;
    public void doSubmit(HashMap<String, String> eventInfo) {
        EventSubmitParam eventSubmitParam = new EventSubmitParam(eventInfo);
        x.http().post(eventSubmitParam, new Callback.CommonCallback<EnjoyResponse>() {
            @Override
            public void onSuccess(final EnjoyResponse result) {
                Log.e("TAG", "onSuccess: "+result.state);
                String data = result.data;
                try {
                    JSONObject dataJson = new JSONObject(data);
                    if (dataJson.has("nurl")) {
                        String noticeUrl = EnjoyUtils.buildCommonWebUrl(dataJson.getString("nurl"), true);
                        if (!TextUtils.isEmpty(noticeUrl)) {
                            if (loginNoticeDialog != null && loginNoticeDialog.isShowing()) {
                                loginNoticeDialog.dismiss();
                            }
                            loginNoticeDialog = null;
                            loginNoticeDialog = new NoticeDialog(SDKCore.getMainAct(), noticeUrl, new NoticeDialog.NoticeCallback() {
                                @Override
                                public void onFinish() {

                                }
                            });
                            loginNoticeDialog.show();
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (result.state == 0) {
                    //服务端返回数据为空
                    Bus.getDefault().post(EvSubmit.getFail(EnjoyConstants.Status.SERVER_ERR, "result data is null."));
                    return;
                }
                Bus.getDefault().post(EvSubmit.getSucc());
            }

            @Override
            public void onError(final Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: "+ex.getLocalizedMessage()+isOnCallback);
                Bus.getDefault().post(EvSubmit.getFail(2,"submit fail"));
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

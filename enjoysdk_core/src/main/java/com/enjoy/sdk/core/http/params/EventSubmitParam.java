package com.enjoy.sdk.core.http.params;



import com.enjoy.sdk.framework.xutils.http.annotation.HttpRequest;
import com.enjoy.sdk.core.api.EnjoyConstants;
import com.enjoy.sdk.core.http.EnjoyUrl;
import com.enjoy.sdk.core.http.builder.CommonParamBuilder;
import com.enjoy.sdk.core.sdk.SDKData;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Data：21/12/2018-11:03 AM
 * Author: ranger
 */
@HttpRequest(
        url = EnjoyUrl.EVENT_SUBMIT,
        builder = CommonParamBuilder.class
)
public class EventSubmitParam extends CommonParam {

    public EventSubmitParam(HashMap<String, String> eventInfo) {
        super();
        buildJunSInfo(eventInfo);
    }

    private void buildJunSInfo(HashMap<String, String> eventInfo) {
        try {
            //Log.e("guotest","111"+eventInfo.get(JunSConstants.SUBMIT_TYPE));
            String event = "";
            switch (eventInfo.get(EnjoyConstants.SUBMIT_TYPE)){
                case EnjoyConstants.SUBMIT_TYPE_CREATE:
                    event = "create";
                    break;
                case EnjoyConstants.SUBMIT_TYPE_ENTER:
                    event = "enter";
                    break;
                case EnjoyConstants.SUBMIT_TYPE_UPGRADE:
                    event = "levelup";
                    break;
                case EnjoyConstants.SUBMIT_TYPE_UPDATE:
                    event = "levelup";
                    break;
            }
            if(!event.equals("")){
                enjoyJson.put("uname", SDKData.getSdkUserName());
                enjoyJson.put("uid", SDKData.getSdkUserId());
                enjoyJson.put("dsid", eventInfo.get(EnjoyConstants.SUBMIT_SERVER_ID));
                enjoyJson.put("event", event);
                enjoyJson.put("dsname", eventInfo.get(EnjoyConstants.SUBMIT_SERVER_NAME));
                enjoyJson.put("drid", eventInfo.get(EnjoyConstants.SUBMIT_ROLE_ID));
                enjoyJson.put("drname", eventInfo.get(EnjoyConstants.SUBMIT_ROLE_NAME));
                enjoyJson.put("drlevel", eventInfo.get(EnjoyConstants.SUBMIT_ROLE_LEVEL));
                enjoyJson.put("ext", eventInfo.get(EnjoyConstants.SUBMIT_EXT));
                // junSJson.put("polltime", polltime);
                enjoyJson.put("sign", buildSign());
            }else{
                if(SDKData.getSdkUserName() == null){
                    enjoyJson.put("uname", "");
                    enjoyJson.put("uid", "");
                }else{
                    enjoyJson.put("uname", SDKData.getSdkUserName());
                    enjoyJson.put("uid", SDKData.getSdkUserId());
                }
                enjoyJson.put("event", eventInfo.get(EnjoyConstants.SUBMIT_TYPE));
                enjoyJson.put("ext", eventInfo.get(EnjoyConstants.SUBMIT_EXT));
                // junSJson.put("polltime", polltime);
                enjoyJson.put("sign", buildSign());
            }

            encryptGInfo(EnjoyUrl.EVENT_SUBMIT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

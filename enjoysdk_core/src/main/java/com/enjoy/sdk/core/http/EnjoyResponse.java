package com.enjoy.sdk.core.http;

import com.enjoy.sdk.framework.xutils.http.annotation.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data：21/12/2018-10:45 AM
 * Author: ranger
 */
@HttpResponse(parser = EnjoyResponseParser.class)
public class EnjoyResponse {

    public int state = 0;
    public String data = "";
    public String msg = "fail";

    @Override
    public String toString() {
        try {
            JSONObject dataJson = new JSONObject();
            dataJson.put("state", state);
            dataJson.put("data", data);
            dataJson.put("msg", msg);
            return dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}

package com.enjoy.sdk.core.http.exception;


import com.enjoy.sdk.core.http.EnjoyResponse;

/**
 * Data：24/12/2018-11:07 AM
 * Author: ranger
 */
public class EnjoyServerException extends Exception {

    private String serverMsg;
    private String serverData;

    public EnjoyServerException(EnjoyResponse tnResponse) {
        super(tnResponse.toString());
        serverMsg = tnResponse.msg;
        serverData = tnResponse.data;
    }

    public String getServerMsg() {
        return serverMsg;
    }

    public String getServerData() {
        return serverData;
    }
}

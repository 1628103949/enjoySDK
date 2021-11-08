package com.enjoy.sdk.core.own.fw;

/**
 * Data：07/03/2019-2:43 PM
 * Author: ranger
 */
public class EnjoyBallEv {

    public static final int TO_SHOW = 2;
    public static final int TO_HIDE = 3;

    private int action;

    private EnjoyBallEv(int act) {
        this.action = act;
    }

    public static EnjoyBallEv getShow() {
        return new EnjoyBallEv(TO_SHOW);
    }

    public static EnjoyBallEv getHide() {
        return new EnjoyBallEv(TO_HIDE);
    }

    public int getAction() {
        return action;
    }
}

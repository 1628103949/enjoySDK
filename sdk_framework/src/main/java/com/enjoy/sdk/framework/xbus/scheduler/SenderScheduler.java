package com.enjoy.sdk.framework.xbus.scheduler;

import com.enjoy.sdk.framework.xbus.Bus;

class SenderScheduler implements Scheduler {
    private Bus mBus;

    public SenderScheduler(final Bus bus) {
        mBus = bus;
    }

    @Override
    public void post(final Runnable runnable) {
        runnable.run();
    }
}

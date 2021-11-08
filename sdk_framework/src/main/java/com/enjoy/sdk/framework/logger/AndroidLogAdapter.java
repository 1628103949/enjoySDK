package com.enjoy.sdk.framework.logger;

public class AndroidLogAdapter implements com.enjoy.sdk.framework.logger.LogAdapter {

    private final com.enjoy.sdk.framework.logger.FormatStrategy formatStrategy;

    public AndroidLogAdapter() {
        this.formatStrategy = com.enjoy.sdk.framework.logger.PrettyFormatStrategy.newBuilder().build();
    }

    public AndroidLogAdapter(com.enjoy.sdk.framework.logger.FormatStrategy formatStrategy) {
        this.formatStrategy = formatStrategy;
    }

    @Override
    public boolean isLoggable(int priority, String tag) {
        return true;
    }

    @Override
    public void log(int priority, String tag, String message) {
        formatStrategy.log(priority, tag, message);
    }

}
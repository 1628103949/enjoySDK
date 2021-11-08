package com.enjoy.sdk.framework.xbus.method;

import com.enjoy.sdk.framework.xbus.MethodInfo;

import java.lang.reflect.Method;

interface MethodConverter {

    MethodInfo convert(final Method method);
}

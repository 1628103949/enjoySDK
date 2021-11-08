package com.enjoy.sdk.framework.xbus.method;

import com.enjoy.sdk.framework.xbus.Bus;
import com.enjoy.sdk.framework.xbus.MethodInfo;

import java.util.Set;

public interface MethodFinder {

    Set<MethodInfo> find(final Bus bus, final Class<?> targetClass);
}

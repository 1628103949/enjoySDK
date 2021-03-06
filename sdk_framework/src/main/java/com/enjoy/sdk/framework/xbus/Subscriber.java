package com.enjoy.sdk.framework.xbus;

import java.lang.reflect.InvocationTargetException;

class Subscriber {
    public final MethodInfo method;
    public final Object target;
    public final Class<?> targetType;
    public final Class<?> eventType;
    public final Bus.EventMode mode;
    public final String name;

    public Subscriber(final MethodInfo method, final Object target) {
        this.method = method;
        this.target = target;
        this.eventType = method.eventType;
        this.targetType = method.targetType;
        this.mode = method.mode;
        this.name = method.name;
    }

    public Object invoke(Object event)
            throws InvocationTargetException, IllegalAccessException {
        return this.method.method.invoke(this.target, event);
    }

    public boolean match(final Class<?> eventClass) {
        return this.eventType.isAssignableFrom(eventClass);
    }

    @Override
    public String toString() {
        return targetType.getSimpleName() + "."
                + method.method.getName()
                + "(" + eventType.getSimpleName() + ")"
                + ":" + method.mode.name();
    }


}

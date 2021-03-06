package com.enjoy.sdk.framework.xbus;

import android.util.Log;

public class EventEmitter implements Runnable {
    private static final String TAG = Bus.TAG;

    public final Bus bus;
    public final Object event;
    public final Subscriber subscriber;
    public final Bus.EventMode mode;
    public final boolean debug;

    public EventEmitter(final Bus bus, final Object event,
                        final Subscriber subscriber, final boolean debug) {
        this.bus = bus;
        this.event = event;
        this.subscriber = subscriber;
        this.mode = subscriber.mode;
        this.debug = debug;

    }

    @Override
    public void run() {
        try {
            if (debug) {
                Log.v(TAG, "sending event:[" + event
                        + "] to subscriber:[" + subscriber
                        + "] at thread:" + Thread.currentThread().getName());
            }
            subscriber.invoke(event);
        } catch (Exception e) {
            if (debug) {
                Log.e(TAG, "sending event:[" + event + "] to subscriber:["
                        + subscriber + "] failed, reason: " + e, e);
            }
//            bus.post(new BusException("sending event:[" + event
//                    + "] to subscriber:[" + subscriber + "] failed", e));
        }
    }

    @Override
    public String toString() {
        return "{" +
                "event:[" + event +
                "] to subscriber:[" + subscriber +
                "]}";
    }
}

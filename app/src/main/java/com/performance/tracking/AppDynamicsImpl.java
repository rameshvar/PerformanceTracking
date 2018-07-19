/*
 * Created by rampande on 03/07/18.
 */
package com.performance.tracking;

import android.content.Context;

import java.util.Map;

public class AppDynamicsImpl implements IAnalytics {
    @Override
    public void init(Context context, String token) {
//        Instrumentation.start(token, context);
        AnalyticsLog.debug("AppDynamicsImpl.init :  ");
    }


    @Override
    public void startTimer(String name) {
//        Instrumentation.startTimer(name);
        AnalyticsLog.debug("AppDynamicsImpl.startTimer :  ");
    }

    @Override
    public void stopTimer(String name) {
//        Instrumentation.stopTimer(name);
        AnalyticsLog.debug("AppDynamicsImpl.stopTimer :  ");
    }

    @Override
    public void setUserData(String name, Object value) {
//        if (value instanceof Date) {
//            Instrumentation.setUserDataDate(name, (Date) value);
//        } else if (value instanceof Boolean) {
//            Instrumentation.setUserDataBoolean(name, (Boolean) value);
//        } else if (value instanceof String) {
//            Instrumentation.setUserData(name, (String) value);
//        } else if (value instanceof Long) {
//            Instrumentation.setUserDataLong(name, (Long) value);
//        } else if (value instanceof Double) {
//            Instrumentation.setUserDataDouble(name, (Double) value);
//        }

        AnalyticsLog.debug("AppDynamicsImpl.setUserData : name : " + name + ", value : " + value);
    }

    @Override
    public void addMetric(String name, String category) {

    }

    @Override
    public void addCustomEvent(String eventType, Map<String, Object> eventAttributes) {

    }


    @Override
    public void recordBreadcrumb(String name, Map<String, Object> attributes) {

    }

    @Override
    public void setInteractionName(String interactionName) {

    }
}

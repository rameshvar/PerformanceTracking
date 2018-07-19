/*
 * Created by rampande on 29/06/18.
 */
package com.performance.tracking;

import android.content.Context;

import java.util.Map;

public class NewRelicImpl implements IAnalytics {

    @Override
    public void init(Context context, String token) {
        if (token != null) {
            //            NewRelic.withApplicationToken(token).withLogLevel(AgentLog.AUDIT).start(context);
            AnalyticsLog.debug("NewRelicImpl.init : token : " + token);
        }
    }

    @Override
    public void addMetric(String name, String category) {
        //        NewRelic.recordMetric(name, category);
    }

    @Override
    public void addCustomEvent(String eventName, Map<String, Object> eventAttributes) {
        //        NewRelic.recordCustomEvent(eventName, eventAttributes);
        AnalyticsLog
                .debug("NewRelicImpl.addCustomEvent" + " : name : " + eventName + ", attributes : " + eventAttributes);
    }


    @Override
    public void recordBreadcrumb(String name, Map<String, Object> attributes) {
        //        NewRelic.recordBreadcrumb(name, attributes);
        AnalyticsLog
                .debug("NewRelicImpl.recordBreadcrumb : name : " + name + ", attributes : " + attributes);
    }


    @Override
    public void setInteractionName(String interactionName) {
        //        NewRelic.setInteractionName(interactionName);
        AnalyticsLog
                .debug("NewRelicImpl.setInteractionName : interactionName : " + interactionName);
    }


    @Override
    public void startTimer(String name) {

    }

    @Override
    public void stopTimer(String name) {

    }

    @Override
    public void setUserData(String name, Object value) {

    }
}

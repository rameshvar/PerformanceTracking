package com.performance.tracking;

import android.content.Context;

import java.util.Map;

/*
 * Created by rpan19 on 22/01/18.
 */
public interface IAnalytics {

    /**
     * Used to initialise the performance analytics.
     */
    void init(Context context, String token);

    /**
     * Used to add metric
     *
     * @param name     : name value
     * @param category : category value
     */
    void addMetric(String name, String category);

    /**
     * Add custom event
     *
     * @param eventType       : event type value
     * @param eventAttributes : event attribute values
     */
    void addCustomEvent(String eventType, Map<String, Object> eventAttributes);


    /**
     * Records a MobileBreadcrumb event
     *
     * @param name       Breadcrumb name
     * @param attributes Breadcrumb Attributes
     */
    void recordBreadcrumb(String name, Map<String, Object> attributes);

    /**
     * sets specified interaction name for displayed fragment/Activity
     *
     * @param interactionName interaction name
     */
    void setInteractionName(String interactionName);

    void startTimer(String name);

    void stopTimer(String name);
    void setUserData(String name, Object value);
}

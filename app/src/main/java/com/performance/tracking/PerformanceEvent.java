/*
 * Created by rpan19 on 29/06/17.
 */
package com.performance.tracking;

import android.support.v4.util.ArrayMap;

import java.util.Map;


public class PerformanceEvent {
    private Map<String, PerformanceEventAttribute> eventParamsUnFinished = new ArrayMap<>();
    private Map<String, Object> eventParamsFinished = new ArrayMap<>();
    private String name;
    private long startTime;
    private boolean isAnalyticsInitialized = true;

    public PerformanceEvent(String eventName, long currentTime, boolean isAnalyticsInitialized) {
        this.name = eventName;
        this.startTime = currentTime;
        this.isAnalyticsInitialized = isAnalyticsInitialized;
    }

    public boolean isAnalyticsInitialized() {
        return isAnalyticsInitialized;
    }

    public void setAnalyticsInitialized(boolean analyticsInitialized) {
        isAnalyticsInitialized = analyticsInitialized;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    public Map<String, PerformanceEventAttribute> getEventParamsUnFinished() {
        return eventParamsUnFinished;
    }

    public Map<String, Object> getEventParamsFinished() {
        return eventParamsFinished;
    }

}

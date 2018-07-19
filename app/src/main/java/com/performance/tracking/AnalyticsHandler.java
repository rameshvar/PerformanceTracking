
/*
 * Created by rampande on 10/07/18.
 */
package com.performance.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import static com.performance.tracking.Constants.Monitoring.ADD_EVENT_ATTRIBUTE;
import static com.performance.tracking.Constants.Monitoring.ATTRIBUTE_NAME;
import static com.performance.tracking.Constants.Monitoring.CURRENT_TIME;
import static com.performance.tracking.Constants.Monitoring.EVENT_NAME;
import static com.performance.tracking.Constants.Monitoring.EVENT_PARAMS;
import static com.performance.tracking.Constants.Monitoring.IS_ANALYTICS_INITIALIZED;
import static com.performance.tracking.Constants.Monitoring.START_MONITORING;
import static com.performance.tracking.Constants.Monitoring.STOP_EVENT_ATTRIBUTE;
import static com.performance.tracking.Constants.Monitoring.STOP_MONITORING;

public class AnalyticsHandler extends Handler {
    private Context mContext;
    private IAnalytics mPAnalyticsImpl;

    AnalyticsHandler(Context context, Looper looper) {
        super(looper);
        this.mContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle data = msg.getData();
        String eventName = data.getString(EVENT_NAME, "");
        long currentTime = data.getLong(CURRENT_TIME);
        if (msg.what == START_MONITORING) {
            handleStartMonitoring(eventName, data.getStringArrayList(EVENT_PARAMS), currentTime,
                                  data.getBoolean(IS_ANALYTICS_INITIALIZED));
        } else if (msg.what == STOP_EVENT_ATTRIBUTE) {
            handleStopEventAttribute(eventName, data.getString(ATTRIBUTE_NAME, ""), currentTime);
        } else if (msg.what == STOP_MONITORING) {
            handleStopMonitoring(eventName);
        } else if (msg.what == ADD_EVENT_ATTRIBUTE) {
            handleAddEventAttribute(eventName, data.getString(ATTRIBUTE_NAME, ""), currentTime);
        }
    }

    private void handleAddEventAttribute(String eventName, String attributeName, long currentTime) {
        PerformanceEvent event = PerformanceTracker.getInstance().getEvent(eventName);
        if (event != null) {
            createEventAttribute(attributeName, currentTime, event);
        }
        AnalyticsLog
                .debug("handleAddEventAttribute - eventName : " + eventName + ", attributeName : " + attributeName);
    }

    /**
     * Handles start monitoring event
     *
     * @param eventName              : event name value
     * @param eventParams            : event parameter value
     * @param currentTime            : current time in millisecond
     * @param isAnalyticsInitialized : 1 if Analytics vendor initialized else 0
     */
    private void handleStartMonitoring(String eventName, List<String> eventParams, long currentTime, boolean isAnalyticsInitialized) {
        PerformanceEvent event = new PerformanceEvent(eventName, currentTime,
                                                      isAnalyticsInitialized);
        if (eventParams != null && !eventParams.isEmpty()) {
            int count = eventParams.size();
            for (int i = 0; i < count; i++) {
                createEventAttribute(eventParams.get(i), event.getStartTime(), event);
            }
        }
        AnalyticsLog
                .debug("handleStartMonitoring - eventName : " + eventName + ", eventParams : " + eventParams);
        PerformanceTracker.getInstance().addEvent(eventName, event);
    }

    private void createEventAttribute(String attributeName, long startTime, PerformanceEvent event) {
        PerformanceEventAttribute attribute = new PerformanceEventAttribute(attributeName,
                                                                            startTime);
        event.getEventParamsUnFinished().put(attributeName, attribute);
    }

    /**
     * Handles stop monitoring event
     *
     * @param eventName     : event name value
     * @param attributeName : event attribute value
     * @param currentTime   : current time in millisecond
     */
    private void handleStopEventAttribute(String eventName, String attributeName, long currentTime) {
        PerformanceEvent event = PerformanceTracker.getInstance().getEvent(eventName);
        if (event == null) {
            return;
        }
        PerformanceEventAttribute eventAttribute = event.getEventParamsUnFinished()
                .get(attributeName);
        event.getEventParamsFinished()
                .put(attributeName, getDuration(eventAttribute.getStartTime(), currentTime));
        event.getEventParamsUnFinished().remove(attributeName);
        AnalyticsLog
                .debug("handleStopEventAttribute - eventName : " + eventName + ", attributeName : " + attributeName);
        if (event.getEventParamsUnFinished().isEmpty()) {
            handleStopMonitoring(eventName);
        }
    }

    /**
     * Handle stop monitoring event
     *
     * @param eventName : event name to stop
     */
    private void handleStopMonitoring(String eventName) {
        final PerformanceEvent event = PerformanceTracker.getInstance().getEvent(eventName);
        if (event != null) {
            if (null == mPAnalyticsImpl) {
                throw new AnalyticsInitException();
            } else if (event != null && !event.getEventParamsFinished().isEmpty()) {
                String launchType = null;
                if (Constants.AppLaunch.NAME.equals(eventName)) {
                    launchType = updateLaunchType(eventName, event);
                    //removeBGTimeFromAppLaunch(event.getEventParamsFinished());
                }
                if (PerformanceTracker.getInstance().isNewRelic()) {
                    if (launchType != null) {
                        event.getEventParamsFinished().put(EVENT_NAME, launchType);
                    }
                    mPAnalyticsImpl.addCustomEvent(event.getName(), event.getEventParamsFinished());
                    AnalyticsLog.debug("handleStopMonitoring - eventName : " + event
                            .getName() + ", attributes : " + event.getEventParamsFinished());
                } else if (PerformanceTracker.getInstance().isAppDynamics()){
                    //TODO APP D
                    AnalyticsLog
                            .debug("handleStopMonitoring - eventName : " + launchType + ", attributes : " + event
                                    .getEventParamsFinished());
                    for (Map.Entry<String, Object> entry : event.getEventParamsFinished()
                            .entrySet()) {
                        mPAnalyticsImpl.setUserData(launchType + "-" + entry.getKey(),
                                                    String.valueOf(entry.getValue()));
                    }
                } else {
                        mPAnalyticsImpl.addCustomEvent(event.getName(), event.getEventParamsFinished());
                }
                PerformanceTracker.getInstance().removeEvent(eventName);
            }
        }
    }

    @NonNull
    private String updateLaunchType(String eventName, PerformanceEvent event) {
        SharedPreferences sharedPreferences = mContext.getApplicationContext()
                .getSharedPreferences("PerfHelper", Context.MODE_PRIVATE);
        String launchName;
        if (sharedPreferences.getBoolean(eventName, false)) {
            launchName = Constants.AppLaunch.WARM_LAUNCH;
        } else {
            launchName = Constants.AppLaunch.COLD_LAUNCH;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(eventName, true);
            editor.commit();
        }
        return launchName;
    }


    private float getDuration(long startTime, long endTime) {
        return (endTime - startTime) / 1000f;
    }


    void setAnalyticsVendorImpl(IAnalytics analyticsVendorImpl) {
        mPAnalyticsImpl = analyticsVendorImpl;
    }
}

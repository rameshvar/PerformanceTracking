/*
 * Created by abande on 2/19/16.
 */
package com.performance.tracking;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.performance.tracking.Constants.AnalyticsVendor.LAST_INTERACTIONS;
import static com.performance.tracking.Constants.Monitoring.ADD_EVENT_ATTRIBUTE;
import static com.performance.tracking.Constants.Monitoring.ATTRIBUTE_NAME;
import static com.performance.tracking.Constants.Monitoring.CURRENT_TIME;
import static com.performance.tracking.Constants.Monitoring.EVENT_NAME;
import static com.performance.tracking.Constants.Monitoring.EVENT_PARAMS;
import static com.performance.tracking.Constants.Monitoring.IS_ANALYTICS_INITIALIZED;
import static com.performance.tracking.Constants.Monitoring.START_MONITORING;
import static com.performance.tracking.Constants.Monitoring.STOP_EVENT_ATTRIBUTE;
import static com.performance.tracking.Constants.Monitoring.STOP_MONITORING;


/**
 * helper class for interacting analytics classes like New Relic
 */
public class PerformanceTracker {
    private static PerformanceTracker mPerformanceTracker;
    private static AnalyticsHandler mHandler = null;
    private Context mContext;
    private IAnalytics mPAnalyticsImpl;
    private HashMap<String, PerformanceEvent> mTaskMap = new HashMap<>();
    private HandlerThread mHandlerThread = null;
    private InteractionHistory<String> mLastInteractions = new InteractionHistory<>();
    private String mAnalyticsVendor;


    private PerformanceTracker() {
    }

    public static PerformanceTracker getInstance() {
        if (null == mPerformanceTracker) {
            mPerformanceTracker = new PerformanceTracker();
        }
        return mPerformanceTracker;
    }

    /**
     * initialize PerformanceTracker
     *
     * @param context
     */
    public void initialize(Context context) {
        mContext = context;
    }

    /**
     * @param analyticsVendor name of Analytics Vendor from {@link Constants.AnalyticsVendor}
     * @param token           appId or token for the vendor
     */
    public void initializeAnalyticsVendor(String analyticsVendor, String token, IAnalytics analyticsImpl) {
        if (!TextUtils.isEmpty(analyticsVendor) && !TextUtils.isEmpty(token)) {
            mPAnalyticsImpl = analyticsImpl;
            this.mAnalyticsVendor = analyticsVendor;
            if (BuildConfig.BUILD_TYPE.equals("debug")) {
                AnalyticsLog.setLevel(5);
            } else {
                AnalyticsLog.setLevel(1);
            }
            if (mHandler != null) {
                mHandler.setAnalyticsVendorImpl(mPAnalyticsImpl);
            }
            mPAnalyticsImpl.init(mContext, token);
        }
    }

    /**
     * get created PerformanceEvent for given name
     *
     * @param eventName event Name
     * @return returns event if exists else null
     */
    PerformanceEvent getEvent(String eventName) {
        return mTaskMap.get(eventName);
    }

    /**
     * creates a task with id as event Name
     *
     * @param eventName   Name of event to be captured
     * @param eventParams attributes of event
     */
    public void startMonitoring(String eventName, List<String> eventParams) {
        // mPAnalyticsImpl == null, client is not initialized keep it in collection, this might be a case for App Launch specific to APP D
        if (isNewRelic() || mPAnalyticsImpl == null || isDiskWrite()) {
            if (mHandler == null || mHandlerThread == null) {
                startHandlerThread();
            }
            if (getEvent(eventName) != null) {
                mTaskMap.remove(eventName);
            }
            Message message = new Message();
            message.what = START_MONITORING;
            Bundle data = new Bundle();
            data.putCharSequence(EVENT_NAME, eventName);
            data.putBoolean(IS_ANALYTICS_INITIALIZED, mPAnalyticsImpl != null);
            data.putLong(CURRENT_TIME, System.currentTimeMillis());
            data.putStringArrayList(EVENT_PARAMS, (ArrayList<String>) eventParams);
            message.setData(data);
            mHandler.sendMessage(message);
        } else {
            if (eventParams != null && !eventParams.isEmpty()) {
                int count = eventParams.size();
                for (int i = 0; i < count; i++) {
                    mPAnalyticsImpl.startTimer(eventName + "-" + eventParams.get(i));
                }
            } else {
                mPAnalyticsImpl.startTimer(eventName);
            }
        }
    }

    public void startMonitoring(String eventName) {
        if (isNewRelic()) {
            //Nothing to do in this case for New Relic
        } else if (isDiskWrite() && mPAnalyticsImpl != null) {
            mPAnalyticsImpl.startTimer(eventName);
        } else if (mPAnalyticsImpl != null) {
            mPAnalyticsImpl.startTimer(eventName);
        }
    }


    /**
     * Stops an event attribute, if all attributes are done it will stop monitoring
     * and removes task from task map
     *
     * @param eventName     Name of event
     * @param attributeName Name of attribute which is closed
     */
    public void stopEventAttribute(String eventName, String attributeName) {
        if (eventName == null || attributeName == null) {
            return;
        }
        if (isNewRelic() || mPAnalyticsImpl == null || (getEvent(eventName) != null && !getEvent(
                eventName).isAnalyticsInitialized()) || isDiskWrite()) {
            Message message = new Message();
            message.what = STOP_EVENT_ATTRIBUTE;
            Bundle data = new Bundle();
            data.putCharSequence(EVENT_NAME, eventName);
            data.putLong(CURRENT_TIME, System.currentTimeMillis());
            data.putCharSequence(ATTRIBUTE_NAME, attributeName);
            message.setData(data);
            mHandler.sendMessage(message);
        } else if (mPAnalyticsImpl != null) {
            mPAnalyticsImpl.stopTimer(eventName + "-" + attributeName);
        }
    }


    /**
     * Stops monitoring for an event
     *
     * @param eventName Name of event
     */
    public void stopMonitoring(final String eventName) {
        PerformanceEvent event = getEvent(eventName);
        if (mPAnalyticsImpl != null && event != null) {
            if (isNewRelic() || !event.isAnalyticsInitialized() || isDiskWrite()) {
                if (eventName != null && mHandler != null && mHandlerThread != null) {
                    Message message = new Message();
                    message.what = STOP_MONITORING;
                    Bundle data = new Bundle();
                    data.putCharSequence(EVENT_NAME, eventName);
                    message.setData(data);
                    mHandler.sendMessage(message);
                }
            } else {
                mPAnalyticsImpl.stopTimer(eventName);
                removeEvent(eventName);
            }
        } else {
            mTaskMap.remove(eventName);
        }
    }

    public void addEventAttribute(String eventName, String attributeName) {
        if (isNewRelic() || (getEvent(eventName) != null && !getEvent(eventName)
                .isAnalyticsInitialized()) || isDiskWrite()) {
            Message message = new Message();
            message.what = ADD_EVENT_ATTRIBUTE;
            Bundle data = new Bundle();
            data.putLong(CURRENT_TIME, System.currentTimeMillis());
            data.putCharSequence(EVENT_NAME, eventName);
            data.putCharSequence(ATTRIBUTE_NAME, attributeName);
            message.setData(data);
            mHandler.sendMessage(message);
        } else {
            mPAnalyticsImpl.startTimer(eventName + "-" + attributeName);
        }
    }

    /**
     * if performance reporting is disbaled in server config then do clean up.
     */
    public void cleanUp() {
        mTaskMap.clear();
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Starts the handler thread
     */
    private void startHandlerThread() {
        mHandlerThread = new HandlerThread("HandlerThreadPerf");
        mHandlerThread.start();
        mHandler = new AnalyticsHandler(mContext, mHandlerThread.getLooper());
    }


    /**
     * Records a MobileBreadcrumb event
     *
     * @param name               Breadcrumb name
     * @param attributes         Breadcrumb Attributes
     * @param publishUserJourney whether to publish user journey or not
     */
    public void recordBreadcrumb(String name, Map<String, Object> attributes, boolean publishUserJourney) {
        if (null != mPAnalyticsImpl) {
            if (publishUserJourney) {
                String userJourney = getUserJourney();
                if (!TextUtils.isEmpty(userJourney)) {
                    attributes.put(LAST_INTERACTIONS, userJourney);
                }
            }
            mPAnalyticsImpl.recordBreadcrumb(name, attributes);
        }
    }

    /**
     * get user journey
     */
    private String getUserJourney() {
        StringBuilder builder = new StringBuilder();
        int size = mLastInteractions.size();
        for (int index = 0; index < size; index++) {
            if (index == size - 1) {
                builder.append(mLastInteractions.get(index));
            } else {
                builder.append(mLastInteractions.get(index)).append("->");
            }
        }
        return builder.toString();
    }

    /**
     * sets specified interaction name for displayed fragment/Activity and that name to last Interactions
     *
     * @param interactionName interaction name
     */
    public void addInteraction(String interactionName) {
        if (mPAnalyticsImpl != null) {
            mPAnalyticsImpl.setInteractionName(interactionName);
        }
        mLastInteractions.add(interactionName);
    }

    void addEvent(String eventName, PerformanceEvent event) {
        mTaskMap.put(eventName, event);

    }

    void removeEvent(String eventName) {
        mTaskMap.remove(eventName);

    }

    boolean isNewRelic() {
        return mAnalyticsVendor != null && Constants.AnalyticsVendor.NEW_RELIC
                .equals(mAnalyticsVendor);
    }

    boolean isAppDynamics() {
        return mAnalyticsVendor != null && Constants.AnalyticsVendor.APP_DYNAMICS
                .equals(mAnalyticsVendor);
    }

    boolean isDiskWrite() {
        return mAnalyticsVendor != null && Constants.AnalyticsVendor.APP_DYNAMICS
                .equals(mAnalyticsVendor);
    }

}

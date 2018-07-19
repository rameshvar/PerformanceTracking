/*
 * Created by rampande on 04/07/18.
 */
package com.performance.tracking.app;

import android.app.Application;

import com.performance.tracking.AnalyticsLog;
import com.performance.tracking.Constants;
import com.performance.tracking.PerformanceTracker;

import java.util.ArrayList;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ArrayList<String> params = new ArrayList<>();
        params.add("applicationOnCreate");
        params.add("backgroundTime");
        params.add("launchDuration");
        PerformanceTracker.getInstance().initialize(getApplicationContext());
        PerformanceTracker.getInstance().startMonitoring(Constants.AppLaunch.NAME, params);

        AnalyticsLog.debug("MyApplication.onCreate :  1");
        AnalyticsLog.debug("MyApplication.onCreate :  2");
        PerformanceTracker.getInstance()
                .stopEventAttribute(Constants.AppLaunch.NAME, "applicationOnCreate");

    }
}

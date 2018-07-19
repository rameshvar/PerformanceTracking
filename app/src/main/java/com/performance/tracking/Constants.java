/*
 * Created by rampande on 09/07/18.
 */
package com.performance.tracking;

public class Constants {

    private Constants() {

    }

    public static final class AnalyticsVendor {
        public static final String LAST_INTERACTIONS = "lastInteractions";
        public static String NEW_RELIC = "newRelic";
        public static String APP_DYNAMICS = "appDynamics";
        public static String DISK = "disk";

        private AnalyticsVendor() {

        }
    }

    public static final class AppLaunch {
        public static final String LAUNCH_DURATION = "launchDuration";
        public static final String NAME = "AppLaunch";
        public static final String COLD_LAUNCH = "ColdLaunch";
        public static final String WARM_LAUNCH = "WarmLaunch";
        public static final String BACKGROUND_TIME = "backgroundTime";
        public static final String APPLICATION_ON_CREATE = "applicationOnCreate";

        private AppLaunch() {

        }
    }

    public static final class Monitoring {
        public static final int START_MONITORING = 1;
        public static final int STOP_EVENT_ATTRIBUTE = 2;
        public static final int STOP_MONITORING = 3;
        public static final int ADD_EVENT_ATTRIBUTE = 6;
        public static final String CURRENT_TIME = "currentTime";
        public static final String EVENT_PARAMS = "eventParams";
        public static final String EVENT_NAME = "eventName";
        public static final String IS_ANALYTICS_INITIALIZED = "isAnalyticsInitialized";
        public static final String ATTRIBUTE_NAME = "attributeName";

        private Monitoring() {

        }
    }


}

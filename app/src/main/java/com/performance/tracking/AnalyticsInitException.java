package com.performance.tracking;

/*
 * Created by rampande on 04/07/18.
 */public class AnalyticsInitException extends RuntimeException {
    private static final String ERROR_MSG = "IAnalyticsTracker Not Initialized";

    AnalyticsInitException() {
        super(ERROR_MSG);
    }
}
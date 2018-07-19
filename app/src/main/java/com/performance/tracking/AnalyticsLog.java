/*
 * Created by rampande on 09/07/18.
 */
package com.performance.tracking;

import android.util.Log;

public final class AnalyticsLog {
    private static final String TAG = "AnalyticsLog";
    private static int mLevel = 3;

    private AnalyticsLog() {

    }

    public static void debug(String message) {
        if (mLevel == 5) {
            Log.d(TAG, message);
        }
    }

    public static void verbose(String message) {
        if (mLevel >= 4) {
            Log.v(TAG, message);
        }
    }

    public static void info(String message) {
        if (mLevel >= 3) {
            Log.i(TAG, message);
        }
    }

    public static void warning(String message) {
        if (mLevel >= 2) {
            Log.w(TAG, message);
        }
    }

    public static void error(String message) {
        if (mLevel >= 1) {
            Log.e(TAG, message);
        }
    }

    public static void error(String message, Throwable cause) {
        if (mLevel >= 1) {
            Log.e(TAG, message, cause);
        }
    }

    public static int getLevel() {
        return mLevel;
    }

    public static void setLevel(int level) {
        if (level > 5 || level < 1) {
            throw new IllegalArgumentException("Log level is not between ERROR and DEBUG");
        }
        mLevel = level;
    }
}

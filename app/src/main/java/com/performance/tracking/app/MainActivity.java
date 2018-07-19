package com.performance.tracking.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.performance.tracking.AppDynamicsImpl;
import com.performance.tracking.Constants;
import com.performance.tracking.PerformanceTracker;
import com.performance.tracking.R;

public class MainActivity extends AppCompatActivity {
    private static final String APP_DYNAMICS_KEY = "AD-AAB-AAK-HTJ";
    private static final String NEW_RELIC_KEY = "AA03ca93fc314aa73d1a73ce3069ba5bda9bf2c228";

    public static void showPermissionDialog(
            @NonNull Activity activity, @NonNull String permission, final int requestCode) {
        if (!(activity instanceof ActivityCompat.OnRequestPermissionsResultCallback)) {
            throw new UnsupportedOperationException(
                    "Activity should implement " + "ActivityCompat.OnRequestPermissionsResultCallback");
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat
                .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PerformanceTracker.getInstance()
                .stopEventAttribute(Constants.AppLaunch.NAME, "backgroundTime");
        super.onCreate(savedInstanceState);
        if (isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            showPermissionDialog(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1000);
        }
                PerformanceTracker.getInstance()
                        .initializeAnalyticsVendor(Constants.AnalyticsVendor.APP_DYNAMICS, APP_DYNAMICS_KEY,
                                                   new AppDynamicsImpl());

//        PerformanceTracker.getInstance()
//                .initializeAnalyticsVendor(Constants.AnalyticsVendor.NEW_RELIC, NEW_RELIC_KEY,
//                                           new NewRelicImpl());
        //        PerformanceTracker.getInstance()
        //                .initializeAnalyticsVendor(Constants.AnalyticsVendor.DISK,
        //                                           "",
        //                                           new DiskImpl());

        PerformanceTracker.getInstance().startMonitoring("Main Activity Load Time");
        setContentView(R.layout.activity_main);

        Log.d("rameshvar", "MainActivity.onCreate :  ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        PerformanceTracker.getInstance()
                .stopEventAttribute(Constants.AppLaunch.NAME, "launchDuration");
        PerformanceTracker.getInstance().stopMonitoring("Main Activity Load Time");
    }
}

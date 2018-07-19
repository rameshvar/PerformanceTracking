/*
 * Created by rampande on 15/07/18.
 */
package com.performance.tracking;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DiskImpl implements IAnalytics {


    private Context mContext;

    public void log(String... messages) {
        writeText(TextUtils.join(", ", messages));
    }

    private void writeText(final String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS", Locale.ENGLISH);
        final String date = sdf.format(new Date());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(mContext.getApplicationContext().getFilesDir()
                                                 .getAbsolutePath() + "/analytics.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fOut = new FileOutputStream(file, true);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
                    outputStreamWriter.append(System.getProperty("line.separator"));
                    outputStreamWriter.append(date);
                    outputStreamWriter.append(" --> ");
                    outputStreamWriter.append(msg);
                    outputStreamWriter.close();
                    fOut.close();
                } catch (Exception e) {
                    Log.e("DiskImpl", Log.getStackTraceString(e), e);
                }
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void init(Context context, String token) {

        mContext = context;
    }

    @Override
    public void addMetric(String name, String category) {

    }

    @Override
    public void addCustomEvent(String eventType, Map<String, Object> eventAttributes) {
        for (Map.Entry<String, Object> entry : eventAttributes.entrySet()) {
            log(eventType + "-" + entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    @Override
    public void recordBreadcrumb(String name, Map<String, Object> attributes) {

    }

    @Override
    public void setInteractionName(String interactionName) {

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

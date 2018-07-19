/*
 * Created by rpan19 on 30/06/17.
 */
package com.performance.tracking;


public class PerformanceEventAttribute {
    private String name;
    private long startTime;

    public PerformanceEventAttribute(String attributeName, long startTime) {
        this.name = attributeName;
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

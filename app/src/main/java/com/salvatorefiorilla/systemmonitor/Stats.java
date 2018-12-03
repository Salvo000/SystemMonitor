package com.salvatorefiorilla.systemmonitor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Stats implements Serializable {


    private final String packageName;
    //private final long date;
    private long timeForeground;
    private long timeBackgroung;
    private long lastForegroundTimeStamp;
    private long lastBackgroundTimeStamp;


    public Stats(final String pn){
        this.packageName = pn;
        this.lastForegroundTimeStamp = 0;
        this.lastBackgroundTimeStamp = 0;
        this.timeForeground = 0;
        this.timeBackgroung = 0;
    }

    public void setForegroundTimeStamp(long tf){
        this.timeForeground = tf;
    }

    public void setBackgroungTimeStamp(long tb){
        this.timeBackgroung = tb;
    }

    public void setLastForegroundTimeStamp(long lastForegroundTimeStamp){
        this.lastForegroundTimeStamp = lastForegroundTimeStamp;
    }

    public void setLastBackgroundTimeStamp(long lastBackgroundTimeStamp){
        this.lastBackgroundTimeStamp = lastBackgroundTimeStamp;
    }

    public long getTimeForeground(){
        long l = this.timeForeground;
        return l;
    }

    public long getTimeBackgroung(){
        long l = this.timeBackgroung;
        return l;
    }

    public long getLastForegroundTimeStamp(){
        long l = this.lastForegroundTimeStamp;
        return l;

    }

    public long getLastBackgroundTimeStamp(){
        long l = this.lastBackgroundTimeStamp;
        return  l;
    }

    public String getPackageName(){
        String s = packageName;
        return s;
    }

}

package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "ApplicationStats",primaryKeys = {"dateTimeStamp", "nameApp"})
public class ApplicationStats {

    @ColumnInfo(name = "dateTimeStamp")
    @NonNull
    private long dateTimeStamp;

    @ColumnInfo(name = "nameApp")
    @NonNull
    private String nameApp;

    @ColumnInfo(name = "backgroundTime")
    private long backgroundTime;

    @ColumnInfo(name = "foregroundTime")
    private long foregroundTime;

    @ColumnInfo(name = "lastBackgroundTimestamp")
    private long lastBackgroundTimestamp;

    @ColumnInfo(name = "lastForegroundTimestamp")
    private long lastForegroundTimestamp;


    public void setDateTimeStamp(long dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }
    public void setNameApp(String nameApp){
        this.nameApp = nameApp;
    }

    public void setBackgroundTime(long backgroundTime){
        this.backgroundTime = backgroundTime;
    }

    public void setForegroundTime(long foregroundTime )
    {
        this.foregroundTime = foregroundTime;
    }

    public void setLastBackgroundTimestamp(long lastBackgroundTimestamp)
    {
        this.lastBackgroundTimestamp = lastBackgroundTimestamp;
    }

    public void setLastForegroundTimestamp(long lastForegroundTimestamp){
        this.lastForegroundTimestamp = lastForegroundTimestamp;
    }

    public Date getDateOfTimeStamp()
    {
        return new Date(this.dateTimeStamp);
    }

    public long getDateTimeStamp()
    {
        return this.dateTimeStamp;
    }

    public String getNameApp()
    {
        return this.nameApp;
    }

    public long getBackgroundTime()
    {
        return this.backgroundTime;
    }

    public long getForegroundTime()
    {
        return this.foregroundTime;
    }

    public long getLastBackgroundTimestamp()
    {
        return this.lastBackgroundTimestamp;
    }

    public long getLastForegroundTimestamp()
    {
        return this.lastForegroundTimestamp;
    }


    public String toString(){
        return " Name : "+getNameApp()+" " +
                "\tData Last Saved: "+getDateTimeStamp()+
                "\tBackground time : "+getBackgroundTime()+
                "\tForeground time : "+getForegroundTime()+
                "\tLast Background timestamp : "+getLastBackgroundTimestamp()+
                "\tLast Foreground timestamp : "+getLastForegroundTimestamp()+"\n";
    }
}
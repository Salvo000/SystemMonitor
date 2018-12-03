package com.salvatorefiorilla.systemmonitor;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;

import java.io.Serializable;
import java.util.Comparator;

public class Card implements Comparator,Serializable{

    Drawable icon;
    String title;
    String info;
    String packageName;
    int rate;

    public Card(String packageName, String title, String info){
        this.packageName = packageName;
        icon=null;
        this.title = title;
        this.info = info;
        this.rate = -1;
    }

    public Card(String packageName,Drawable icon, String processName, String s) {
        this.packageName = packageName;
        this.icon = icon;
        this.title = processName;
        this.info = s;
        this.rate=-1;
    }

    public Card(String packageName,Drawable icon, String processName, String s, int color) {
        this.packageName = packageName;
        this.icon = icon;
        this.title = processName;
        this.info = s;
        this.rate = color;
    }

    public String getTitle(){   return this.title;  }
    public String getInfo() {   return this.info;  }
    public Drawable getIcon() { return this.icon; }
    public String getPackageName() {   return this.packageName; }
    public int getRate() { return rate; }

    @Override
    public String toString() {
        super.toString();
        String s = "package name: "+packageName+
                "\ttitle: "+title+
                "\tinfo: "+info;
        return s;
    }

    @Override
    public int compare(Object o1, Object o2) {
        return 0;
    }


}

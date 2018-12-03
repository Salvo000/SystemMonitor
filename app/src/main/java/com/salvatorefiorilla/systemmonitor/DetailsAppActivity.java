package com.salvatorefiorilla.systemmonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class DetailsAppActivity extends AppCompatActivity {


    private String packageName, title="", totalUseLabel = "", timeBackgroundLabel="", timeForegroundLabel="", lastUsedLabel="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("ON CREATE DETAILSAPPACTIVITY");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.details);
        TextView titleTextView = findViewById(R.id.textViewTitle);
        TextView stats1TextView = findViewById(R.id.textViewStats1);
        TextView totalUseTextView = findViewById(R.id.total_use);
        TextView timeBackgroundTextView = findViewById(R.id.total_background);
        TextView timeForegroundTextView = findViewById(R.id.total_foreground);
        TextView lastUsedTextView = findViewById(R.id.last_used);
        ImageView icon = findViewById(R.id.imageViewIcon);

        if(savedInstanceState==null){
            Intent i = getIntent();
            String titleOne = i.getStringExtra("title");
            System.out.println("TTTTTITOLOOOOOO one ==> "+titleOne);
            title = titleOne.trim();
            System.out.println("TTTTTITOLOOOOOO ==> "+title);
            packageName = i.getStringExtra("pkgn");
            Stats s = new StatisticsCalculator(this).getAllDetails(packageName);
            Date dateOfEvent = new Date( s.getLastForegroundTimeStamp() );
            java.text.DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
            String dateLastUsed = df.format(dateOfEvent);

            long b = s.getTimeBackgroung();
            long f = s.getTimeForeground();
            long t = b + f;

            System.out.println("t ==> "+t);
            System.out.println("t ==> "+(b+f));
            System.out.println("b  ==> "+b);
            System.out.println("f  ==> "+f);

            if(t == 0){
                totalUseLabel = "We're sorry, at the moment we have no data over this app!\n\n ";

            }else{
                totalUseLabel = "TOTAL USE :\n\t\t"+getTimeString(t);
                timeBackgroundLabel = "TIME IN BACKGROUND :\n\t\t"+getTimeString(b);
                timeForegroundLabel = "TIME IN FOREGROUND :\n\t\t"+getTimeString(f);
                lastUsedLabel ="LAST USED :\n\t\t"+dateLastUsed+"";
            }

        }else{

            packageName = savedInstanceState.getString("packageName");
            title = savedInstanceState.getString("title");
            totalUseLabel = savedInstanceState.getString("totalUseLabel");
            timeBackgroundLabel = savedInstanceState.getString("timeBackgroundLabel");
            timeForegroundLabel = savedInstanceState.getString("timeForegroundLabel");
            lastUsedLabel = savedInstanceState.getString("lastUsedLabel");

        }

        titleTextView.setText(title);
        PackageManager pm = getPackageManager();
        try {
            icon.setImageDrawable( pm.getApplicationIcon(packageName));
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("NameNotFoundException","icon not found for "+packageName+" message:_ "+e.getMessage());
            icon.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_launcher));
        }


        stats1TextView.setText("The package name is: "+packageName);
        totalUseTextView.setText(totalUseLabel);

        timeBackgroundTextView.setText(timeBackgroundLabel);
        timeForegroundTextView.setText(timeForegroundLabel);
        lastUsedTextView.setText(lastUsedLabel);
    }

    public String getTimeString(long time ){

        long millis = time % 1000;
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;
        long hour = (time / (1000 * 60 * 60)) % 24;
        String desc = " ";

        if(hour>0)
            desc += String.format("%02d h ",hour);
        if(minute>0)
            desc += String.format("%02d min ",minute);

        desc += String.format("%02d sec %d ms",second, millis);
        return desc;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("ONSAVEINSTANCESTATE DETAILSAPPACTIVITY");
        super.onSaveInstanceState(outState);
        outState.putString("title", title);
        outState.putString("packageName", packageName);
        outState.putString("totalUseLabel", totalUseLabel);
        outState.putString("timeBackgroundLabel", timeBackgroundLabel);
        outState.putString("timeForegroundLabel", timeForegroundLabel);
        outState.putString("lastUsedLabel", lastUsedLabel);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        System.out.println("ONRESTOREINSTANCESTATE DETAILSAPPACTIVITY");
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if(savedInstanceState!= null){
            packageName = savedInstanceState.getString("packageName");
            title = savedInstanceState.getString("title");
            totalUseLabel = savedInstanceState.getString("totalUseLabel");
            timeBackgroundLabel = savedInstanceState.getString("timeBackgroundLabel");
            timeForegroundLabel = savedInstanceState.getString("timeForegroundLabel");
            lastUsedLabel = savedInstanceState.getString("lastUsedLabel");
        }

    }
}

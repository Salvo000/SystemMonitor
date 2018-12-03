package com.salvatorefiorilla.systemmonitor;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.salvatorefiorilla.systemmonitor.room.DaoPreferencesInterface;
import com.salvatorefiorilla.systemmonitor.room.PreferencesEntity;
import com.salvatorefiorilla.systemmonitor.room.SystemMonitorDatabase;

class ItemRow {
    private String name;
    private final String packageName;
    private Boolean isChecked;
    private Drawable icon;
    private SystemMonitorDatabase db;

    public ItemRow(Context context, String packageName, String name,boolean isChecked){

        db = Room.databaseBuilder(context,SystemMonitorDatabase.class, "systemmonitor").allowMainThreadQueries().build();
        this.packageName = packageName;
        this.name = name;
        this.isChecked = isChecked;
        try {
            icon = context.getPackageManager().getApplicationIcon(this.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            icon = (Drawable) context.getResources().getDrawable(R.drawable.ic_launcher);
        }

    }

    public String getPackageName(){ return this.packageName;}
    public String getName(){ return this.name; }
    public Boolean getIsChecked(){ return this.isChecked; }
    public Drawable getIcon(){ return this.icon; }

    public synchronized boolean changeBool(){

        if(this.isChecked)
            this.isChecked= false;
        else
            this.isChecked = true;

        //this.isChecked = this.isChecked?true:false;

        System.out.println("Cambio checked lancio async task! ora checked è "+this.isChecked);
        new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] objects) {
                DaoPreferencesInterface agent = db.preferencesDao();
                if(!isChecked){
                    agent.insertAllPreferences(new PreferencesEntity(getName()));
                }else{
                    agent.deleteFromTable(new PreferencesEntity(getName()));
                }
                return null;
            }
        }.execute();

        return isChecked;
    }


}

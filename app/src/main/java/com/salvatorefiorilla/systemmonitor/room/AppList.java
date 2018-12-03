package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.room.ColumnInfo;

public class AppList {
    String nameApp;
    long TTU;

    public AppList(String nameApp,Long TTU){
        this.nameApp= nameApp;
        this.TTU = TTU;
    }




    public String getNameApp() {
        return nameApp;
    }
    public long getTtu(){
        return this.TTU;
    }
}

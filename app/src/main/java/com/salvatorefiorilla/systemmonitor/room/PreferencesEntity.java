package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = "PreferencesEntity",primaryKeys = {"nameApp"})
public class PreferencesEntity {

    @ColumnInfo(name = "nameApp")
    @NonNull
    private final String nameApplication;//a quanto ne so, due applicazioni non possono avere lo stesso nome

    public PreferencesEntity(String nameApplication){
        this.nameApplication = nameApplication;
    }

    @NonNull
    public String getNameApplication() {
        return nameApplication;
    }
}

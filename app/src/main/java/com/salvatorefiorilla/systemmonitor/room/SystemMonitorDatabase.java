package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {ApplicationStats.class, PreferencesEntity.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class SystemMonitorDatabase extends RoomDatabase {
    public abstract DaoInterface statsDao();
    public abstract DaoPreferencesInterface preferencesDao();
}

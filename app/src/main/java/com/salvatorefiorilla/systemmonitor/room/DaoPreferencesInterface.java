package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.HashMap;
import java.util.List;

@Dao
public interface DaoPreferencesInterface {
    /**
     * @param preferencesEntities
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllPreferences(PreferencesEntity... preferencesEntities);

    @Query("SELECT * FROM PreferencesEntity")
    List<PreferencesEntity> loadAllAPreferences();

    @Query("SELECT EXISTS(SELECT * FROM PreferencesEntity WHERE nameApp==:nameApplication)")
    boolean getIfIsChecked(String nameApplication);

    @Delete
    void deleteFromTable(PreferencesEntity preferencesEntities);


}

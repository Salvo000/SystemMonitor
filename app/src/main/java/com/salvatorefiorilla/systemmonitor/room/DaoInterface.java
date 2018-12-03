package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import java.util.List;
import java.util.TreeMap;

@Dao
public interface DaoInterface {



    @Query("SELECT * FROM ApplicationStats")
    List<ApplicationStats> loadAllAppllicationStats();

    /*/


    @Query( "SELECT \n"+
            "   (SELECT MAX(LastTimestamp)\n" +
            "      FROM (VALUES (lastBackgroundTimestamp),(lastForegroundTimestamp)) AS UpdateDate(LastUpdateDate)) \n" +
            "   AS LastUpdateDate\n" +
            "FROM ApplicationStats" )
    long getLastBackgroundTimestampTimestamp();


    /*/


    @Query("SELECT * \n" +
            "    FROM    ApplicationStats\n" +
            "    WHERE   lastBackgroundTimestamp = (SELECT MAX(lastBackgroundTimestamp)  FROM ApplicationStats); ")
    ApplicationStats getLastBackgroundTimestamp();

    @Query("SELECT * \n" +
            "    FROM    ApplicationStats\n" +
            "    WHERE   lastForegroundTimestamp = (SELECT MAX(lastForegroundTimestamp)  FROM ApplicationStats); ")
    ApplicationStats getLastForegroundTimestamp();

    @Query("select lastForegroundTimestamp from ApplicationStats")
    long[] getAllForegroundTimestamp();

    @Query("select lastBackgroundTimestamp from ApplicationStats")
    long[] getAllBackgroundTimestamp();

    @Query("SELECT * FROM ApplicationStats WHERE dateTimeStamp BETWEEN :start AND :end ")
    List<ApplicationStats> loadAllApplicationStatsOlderThan(long start, long end);

    @Query("SELECT * FROM ApplicationStats WHERE nameApp == :nameApplication AND dateTimeStamp BETWEEN :start AND :end")
    List<ApplicationStats> loadAllApplicationStatsOlderThan(String nameApplication,long start, long end);


    @Query("DELETE FROM ApplicationStats")
    public void nukeTable();

    @Delete
    public void deleteApplicationStats(ApplicationStats as);

    @Query("SELECT * FROM ApplicationStats WHERE nameApp=:nameApp and dateTimeStamp >=:date")
    public ApplicationStats getApplicationStats(String nameApp, long date);

    @Query("SELECT * FROM ApplicationStats WHERE nameApp=:nameApp ")
    List<ApplicationStats> loadAllInformation(String nameApp);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT *, (backgroundTime+foregroundTime) as 'TTU' " +
            "FROM ApplicationStats where (dateTimeStamp >= :dateTimeS) " +
            "GROUP BY nameApp "+
            "ORDER BY (backgroundTime+foregroundTime) DESC LIMIT 3")
    List<ApplicationStats> bestThreeApp(long dateTimeS);



    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT *, (backgroundTime+foregroundTime) as 'TTU' " +
            "FROM ApplicationStats where (dateTimeStamp >= :dateTimeS) " +
            "GROUP BY nameApp "+
            "ORDER BY (backgroundTime+foregroundTime) LIMIT 1")
    List<ApplicationStats> lessUsed(long dateTimeS);


    @Query("SELECT nameApp, (backgroundTime+foregroundTime) as 'TTU' " +
            "FROM ApplicationStats where (dateTimeStamp >= :dateTimeS) " +
            "GROUP BY nameApp "+
            "ORDER BY (backgroundTime+foregroundTime) DESC ")
    List<AppList> queryLastTimeTwo(long dateTimeS);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ApplicationStats... applications);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT *, (backgroundTime+foregroundTime) as 'TTU' " +
            "FROM ApplicationStats where (dateTimeStamp >= :dateTimeS) " +
            "GROUP BY nameApp "+
            "ORDER BY (backgroundTime+foregroundTime) DESC ")
    List<ApplicationStats> queryLastTime(long dateTimeS);

    @Query("SELECT SUM(backgroundTime+foregroundTime) as ttu from ApplicationStats where (dateTimeStamp >= :timePeriod) ")
    long totalHourUseNumber(long timePeriod);

    @Query("SELECT SUM(backgroundTime) from ApplicationStats where (dateTimeStamp >= :timePeriod) ")
    long totalBackgroundTime(long timePeriod);

    @Query("SELECT SUM(foregroundTime) from ApplicationStats where (dateTimeStamp >= :timePeriod) ")
    long totalForegroundTime(long timePeriod);

    @Query("SELECT count(DISTINCT nameApp) FROM ApplicationStats where (dateTimeStamp >= :timePeriod)")
     int totalAppNumber(long timePeriod);

}



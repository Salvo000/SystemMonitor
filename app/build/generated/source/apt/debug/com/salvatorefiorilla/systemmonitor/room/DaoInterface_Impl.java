package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class DaoInterface_Impl implements DaoInterface {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfApplicationStats;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfApplicationStats;

  private final SharedSQLiteStatement __preparedStmtOfNukeTable;

  public DaoInterface_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfApplicationStats = new EntityInsertionAdapter<ApplicationStats>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `ApplicationStats`(`dateTimeStamp`,`nameApp`,`backgroundTime`,`foregroundTime`,`lastBackgroundTimestamp`,`lastForegroundTimestamp`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ApplicationStats value) {
        stmt.bindLong(1, value.getDateTimeStamp());
        if (value.getNameApp() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getNameApp());
        }
        stmt.bindLong(3, value.getBackgroundTime());
        stmt.bindLong(4, value.getForegroundTime());
        stmt.bindLong(5, value.getLastBackgroundTimestamp());
        stmt.bindLong(6, value.getLastForegroundTimestamp());
      }
    };
    this.__deletionAdapterOfApplicationStats = new EntityDeletionOrUpdateAdapter<ApplicationStats>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `ApplicationStats` WHERE `dateTimeStamp` = ? AND `nameApp` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ApplicationStats value) {
        stmt.bindLong(1, value.getDateTimeStamp());
        if (value.getNameApp() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getNameApp());
        }
      }
    };
    this.__preparedStmtOfNukeTable = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM ApplicationStats";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(ApplicationStats... applications) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfApplicationStats.insert(applications);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteApplicationStats(ApplicationStats as) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfApplicationStats.handle(as);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void nukeTable() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfNukeTable.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfNukeTable.release(_stmt);
    }
  }

  @Override
  public List<ApplicationStats> loadAllAppllicationStats() {
    final String _sql = "SELECT * FROM ApplicationStats";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final List<ApplicationStats> _result = new ArrayList<ApplicationStats>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ApplicationStats _item;
        _item = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _item.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _item.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _item.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _item.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _item.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _item.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ApplicationStats getLastBackgroundTimestamp() {
    final String _sql = "SELECT * \n"
            + "    FROM    ApplicationStats\n"
            + "    WHERE   lastBackgroundTimestamp = (SELECT MAX(lastBackgroundTimestamp)  FROM ApplicationStats); ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final ApplicationStats _result;
      if(_cursor.moveToFirst()) {
        _result = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _result.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _result.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _result.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _result.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _result.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _result.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ApplicationStats getLastForegroundTimestamp() {
    final String _sql = "SELECT * \n"
            + "    FROM    ApplicationStats\n"
            + "    WHERE   lastForegroundTimestamp = (SELECT MAX(lastForegroundTimestamp)  FROM ApplicationStats); ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final ApplicationStats _result;
      if(_cursor.moveToFirst()) {
        _result = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _result.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _result.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _result.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _result.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _result.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _result.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public long[] getAllForegroundTimestamp() {
    final String _sql = "select lastForegroundTimestamp from ApplicationStats";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final long[] _result = new long[_cursor.getCount()];
      int _index = 0;
      while(_cursor.moveToNext()) {
        final long _item;
        _item = _cursor.getLong(0);
        _result[_index] = _item;
        _index ++;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public long[] getAllBackgroundTimestamp() {
    final String _sql = "select lastBackgroundTimestamp from ApplicationStats";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final long[] _result = new long[_cursor.getCount()];
      int _index = 0;
      while(_cursor.moveToNext()) {
        final long _item;
        _item = _cursor.getLong(0);
        _result[_index] = _item;
        _index ++;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ApplicationStats> loadAllApplicationStatsOlderThan(long start, long end) {
    final String _sql = "SELECT * FROM ApplicationStats WHERE dateTimeStamp BETWEEN ? AND ? ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final List<ApplicationStats> _result = new ArrayList<ApplicationStats>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ApplicationStats _item;
        _item = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _item.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _item.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _item.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _item.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _item.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _item.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ApplicationStats> loadAllApplicationStatsOlderThan(String nameApplication, long start,
      long end) {
    final String _sql = "SELECT * FROM ApplicationStats WHERE nameApp == ? AND dateTimeStamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (nameApplication == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, nameApplication);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, start);
    _argIndex = 3;
    _statement.bindLong(_argIndex, end);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final List<ApplicationStats> _result = new ArrayList<ApplicationStats>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ApplicationStats _item;
        _item = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _item.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _item.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _item.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _item.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _item.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _item.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ApplicationStats getApplicationStats(String nameApp, long date) {
    final String _sql = "SELECT * FROM ApplicationStats WHERE nameApp=? and dateTimeStamp >=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (nameApp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, nameApp);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, date);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final ApplicationStats _result;
      if(_cursor.moveToFirst()) {
        _result = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _result.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _result.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _result.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _result.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _result.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _result.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ApplicationStats> loadAllInformation(String nameApp) {
    final String _sql = "SELECT * FROM ApplicationStats WHERE nameApp=? ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (nameApp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, nameApp);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final List<ApplicationStats> _result = new ArrayList<ApplicationStats>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ApplicationStats _item;
        _item = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _item.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _item.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _item.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _item.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _item.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _item.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ApplicationStats> bestThreeApp(long dateTimeS) {
    final String _sql = "SELECT *, (backgroundTime+foregroundTime) as 'TTU' FROM ApplicationStats where (dateTimeStamp >= ?) GROUP BY nameApp ORDER BY (backgroundTime+foregroundTime) DESC LIMIT 3";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dateTimeS);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final List<ApplicationStats> _result = new ArrayList<ApplicationStats>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ApplicationStats _item;
        _item = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _item.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _item.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _item.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _item.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _item.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _item.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ApplicationStats> lessUsed(long dateTimeS) {
    final String _sql = "SELECT *, (backgroundTime+foregroundTime) as 'TTU' FROM ApplicationStats where (dateTimeStamp >= ?) GROUP BY nameApp ORDER BY (backgroundTime+foregroundTime) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dateTimeS);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final List<ApplicationStats> _result = new ArrayList<ApplicationStats>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ApplicationStats _item;
        _item = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _item.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _item.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _item.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _item.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _item.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _item.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<AppList> queryLastTimeTwo(long dateTimeS) {
    final String _sql = "SELECT nameApp, (backgroundTime+foregroundTime) as 'TTU' FROM ApplicationStats where (dateTimeStamp >= ?) GROUP BY nameApp ORDER BY (backgroundTime+foregroundTime) DESC ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dateTimeS);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfTTU = _cursor.getColumnIndexOrThrow("TTU");
      final List<AppList> _result = new ArrayList<AppList>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final AppList _item;
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        final long _tmpTTU;
        _tmpTTU = _cursor.getLong(_cursorIndexOfTTU);
        _item = new AppList(_tmpNameApp,_tmpTTU);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ApplicationStats> queryLastTime(long dateTimeS) {
    final String _sql = "SELECT *, (backgroundTime+foregroundTime) as 'TTU' FROM ApplicationStats where (dateTimeStamp >= ?) GROUP BY nameApp ORDER BY (backgroundTime+foregroundTime) DESC ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dateTimeS);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfDateTimeStamp = _cursor.getColumnIndexOrThrow("dateTimeStamp");
      final int _cursorIndexOfNameApp = _cursor.getColumnIndexOrThrow("nameApp");
      final int _cursorIndexOfBackgroundTime = _cursor.getColumnIndexOrThrow("backgroundTime");
      final int _cursorIndexOfForegroundTime = _cursor.getColumnIndexOrThrow("foregroundTime");
      final int _cursorIndexOfLastBackgroundTimestamp = _cursor.getColumnIndexOrThrow("lastBackgroundTimestamp");
      final int _cursorIndexOfLastForegroundTimestamp = _cursor.getColumnIndexOrThrow("lastForegroundTimestamp");
      final List<ApplicationStats> _result = new ArrayList<ApplicationStats>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ApplicationStats _item;
        _item = new ApplicationStats();
        final long _tmpDateTimeStamp;
        _tmpDateTimeStamp = _cursor.getLong(_cursorIndexOfDateTimeStamp);
        _item.setDateTimeStamp(_tmpDateTimeStamp);
        final String _tmpNameApp;
        _tmpNameApp = _cursor.getString(_cursorIndexOfNameApp);
        _item.setNameApp(_tmpNameApp);
        final long _tmpBackgroundTime;
        _tmpBackgroundTime = _cursor.getLong(_cursorIndexOfBackgroundTime);
        _item.setBackgroundTime(_tmpBackgroundTime);
        final long _tmpForegroundTime;
        _tmpForegroundTime = _cursor.getLong(_cursorIndexOfForegroundTime);
        _item.setForegroundTime(_tmpForegroundTime);
        final long _tmpLastBackgroundTimestamp;
        _tmpLastBackgroundTimestamp = _cursor.getLong(_cursorIndexOfLastBackgroundTimestamp);
        _item.setLastBackgroundTimestamp(_tmpLastBackgroundTimestamp);
        final long _tmpLastForegroundTimestamp;
        _tmpLastForegroundTimestamp = _cursor.getLong(_cursorIndexOfLastForegroundTimestamp);
        _item.setLastForegroundTimestamp(_tmpLastForegroundTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public long totalHourUseNumber(long timePeriod) {
    final String _sql = "SELECT SUM(backgroundTime+foregroundTime) as ttu from ApplicationStats where (dateTimeStamp >= ?) ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timePeriod);
    final Cursor _cursor = __db.query(_statement);
    try {
      final long _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getLong(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public long totalBackgroundTime(long timePeriod) {
    final String _sql = "SELECT SUM(backgroundTime) from ApplicationStats where (dateTimeStamp >= ?) ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timePeriod);
    final Cursor _cursor = __db.query(_statement);
    try {
      final long _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getLong(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public long totalForegroundTime(long timePeriod) {
    final String _sql = "SELECT SUM(foregroundTime) from ApplicationStats where (dateTimeStamp >= ?) ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timePeriod);
    final Cursor _cursor = __db.query(_statement);
    try {
      final long _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getLong(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int totalAppNumber(long timePeriod) {
    final String _sql = "SELECT count(DISTINCT nameApp) FROM ApplicationStats where (dateTimeStamp >= ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timePeriod);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}

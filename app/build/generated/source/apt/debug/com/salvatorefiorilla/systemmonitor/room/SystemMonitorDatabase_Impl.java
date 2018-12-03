package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;

public class SystemMonitorDatabase_Impl extends SystemMonitorDatabase {
  private volatile DaoInterface _daoInterface;

  private volatile DaoPreferencesInterface _daoPreferencesInterface;

  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(3) {
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ApplicationStats` (`dateTimeStamp` INTEGER NOT NULL, `nameApp` TEXT NOT NULL, `backgroundTime` INTEGER NOT NULL, `foregroundTime` INTEGER NOT NULL, `lastBackgroundTimestamp` INTEGER NOT NULL, `lastForegroundTimestamp` INTEGER NOT NULL, PRIMARY KEY(`dateTimeStamp`, `nameApp`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `PreferencesEntity` (`nameApp` TEXT NOT NULL, PRIMARY KEY(`nameApp`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"6942266b330e96cefcccad550da91da3\")");
      }

      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `ApplicationStats`");
        _db.execSQL("DROP TABLE IF EXISTS `PreferencesEntity`");
      }

      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsApplicationStats = new HashMap<String, TableInfo.Column>(6);
        _columnsApplicationStats.put("dateTimeStamp", new TableInfo.Column("dateTimeStamp", "INTEGER", true, 1));
        _columnsApplicationStats.put("nameApp", new TableInfo.Column("nameApp", "TEXT", true, 2));
        _columnsApplicationStats.put("backgroundTime", new TableInfo.Column("backgroundTime", "INTEGER", true, 0));
        _columnsApplicationStats.put("foregroundTime", new TableInfo.Column("foregroundTime", "INTEGER", true, 0));
        _columnsApplicationStats.put("lastBackgroundTimestamp", new TableInfo.Column("lastBackgroundTimestamp", "INTEGER", true, 0));
        _columnsApplicationStats.put("lastForegroundTimestamp", new TableInfo.Column("lastForegroundTimestamp", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysApplicationStats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesApplicationStats = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoApplicationStats = new TableInfo("ApplicationStats", _columnsApplicationStats, _foreignKeysApplicationStats, _indicesApplicationStats);
        final TableInfo _existingApplicationStats = TableInfo.read(_db, "ApplicationStats");
        if (! _infoApplicationStats.equals(_existingApplicationStats)) {
          throw new IllegalStateException("Migration didn't properly handle ApplicationStats(com.salvatorefiorilla.systemmonitor.room.ApplicationStats).\n"
                  + " Expected:\n" + _infoApplicationStats + "\n"
                  + " Found:\n" + _existingApplicationStats);
        }
        final HashMap<String, TableInfo.Column> _columnsPreferencesEntity = new HashMap<String, TableInfo.Column>(1);
        _columnsPreferencesEntity.put("nameApp", new TableInfo.Column("nameApp", "TEXT", true, 1));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPreferencesEntity = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPreferencesEntity = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPreferencesEntity = new TableInfo("PreferencesEntity", _columnsPreferencesEntity, _foreignKeysPreferencesEntity, _indicesPreferencesEntity);
        final TableInfo _existingPreferencesEntity = TableInfo.read(_db, "PreferencesEntity");
        if (! _infoPreferencesEntity.equals(_existingPreferencesEntity)) {
          throw new IllegalStateException("Migration didn't properly handle PreferencesEntity(com.salvatorefiorilla.systemmonitor.room.PreferencesEntity).\n"
                  + " Expected:\n" + _infoPreferencesEntity + "\n"
                  + " Found:\n" + _existingPreferencesEntity);
        }
      }
    }, "6942266b330e96cefcccad550da91da3");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "ApplicationStats","PreferencesEntity");
  }

  @Override
  public DaoInterface statsDao() {
    if (_daoInterface != null) {
      return _daoInterface;
    } else {
      synchronized(this) {
        if(_daoInterface == null) {
          _daoInterface = new DaoInterface_Impl(this);
        }
        return _daoInterface;
      }
    }
  }

  @Override
  public DaoPreferencesInterface preferencesDao() {
    if (_daoPreferencesInterface != null) {
      return _daoPreferencesInterface;
    } else {
      synchronized(this) {
        if(_daoPreferencesInterface == null) {
          _daoPreferencesInterface = new DaoPreferencesInterface_Impl(this);
        }
        return _daoPreferencesInterface;
      }
    }
  }
}

package com.salvatorefiorilla.systemmonitor.room;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.database.Cursor;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class DaoPreferencesInterface_Impl implements DaoPreferencesInterface {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfPreferencesEntity;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfPreferencesEntity;

  public DaoPreferencesInterface_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPreferencesEntity = new EntityInsertionAdapter<PreferencesEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `PreferencesEntity`(`nameApp`) VALUES (?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PreferencesEntity value) {
        if (value.getNameApplication() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getNameApplication());
        }
      }
    };
    this.__deletionAdapterOfPreferencesEntity = new EntityDeletionOrUpdateAdapter<PreferencesEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `PreferencesEntity` WHERE `nameApp` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PreferencesEntity value) {
        if (value.getNameApplication() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getNameApplication());
        }
      }
    };
  }

  @Override
  public void insertAllPreferences(PreferencesEntity... preferencesEntities) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfPreferencesEntity.insert(preferencesEntities);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteFromTable(PreferencesEntity preferencesEntities) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfPreferencesEntity.handle(preferencesEntities);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<PreferencesEntity> loadAllAPreferences() {
    final String _sql = "SELECT * FROM PreferencesEntity";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfNameApplication = _cursor.getColumnIndexOrThrow("nameApp");
      final List<PreferencesEntity> _result = new ArrayList<PreferencesEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final PreferencesEntity _item;
        final String _tmpNameApplication;
        _tmpNameApplication = _cursor.getString(_cursorIndexOfNameApplication);
        _item = new PreferencesEntity(_tmpNameApplication);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public boolean getIfIsChecked(String nameApplication) {
    final String _sql = "SELECT EXISTS(SELECT * FROM PreferencesEntity WHERE nameApp==?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (nameApplication == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, nameApplication);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final boolean _result;
      if(_cursor.moveToFirst()) {
        final int _tmp;
        _tmp = _cursor.getInt(0);
        _result = _tmp != 0;
      } else {
        _result = false;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}

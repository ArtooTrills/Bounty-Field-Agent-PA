package com.example.taskreminder.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class ReminderContentProvider extends ContentProvider {

  private static final int LOCREM = 1;
  private static final int LOCREM_ID = 2;

  private static final int DATETIMEREM = 3;
  private static final int DATETIMEREM_ID = 4;

  private static final int ALL = 5;
  private static final int ALL_ID = 6;


  private static final UriMatcher URI_MATCHER;
  private ReminderDataHelper mOpenHelper;

  static {
    URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    URI_MATCHER.addURI(ReminderContract.AUTHORITY, ReminderContract.PATH_LOC, LOCREM);
    URI_MATCHER.addURI(ReminderContract.AUTHORITY, ReminderContract.PATH_LOC_ID, LOCREM_ID);
    URI_MATCHER.addURI(ReminderContract.AUTHORITY, ReminderContract.PATH_DATETIME, DATETIMEREM);
    URI_MATCHER.addURI(ReminderContract.AUTHORITY, ReminderContract.PATH_DATETIME_ID, DATETIMEREM_ID);
    URI_MATCHER.addURI(ReminderContract.AUTHORITY, ReminderContract.PATH_ALL, ALL);
    URI_MATCHER.addURI(ReminderContract.AUTHORITY, ReminderContract.PATH_ALL_ID, ALL_ID);
  }

  @Override
  public boolean onCreate() {
    mOpenHelper = new ReminderDataHelper(getContext());
    return false;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                      @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

    switch (URI_MATCHER.match(uri)) {
      case LOCREM:
        builder.setTables(ReminderContract.LocationAlert.TABLE_NAME);
        builder.appendWhere(ReminderContract.LocationAlert.TYPE + " = '" +
                ReminderContract.PATH_LOC + "'");
        break;
      case LOCREM_ID:
        builder.setTables(ReminderContract.LocationAlert.TABLE_NAME);
        builder.appendWhere(ReminderContract.LocationAlert._ID + " = " +
                uri.getLastPathSegment());
        break;
      case DATETIMEREM:
        builder.setTables(ReminderContract.DateTimeAlert.TABLE_NAME);
        builder.appendWhere(ReminderContract.DateTimeAlert.TYPE + " = '" +
                ReminderContract.PATH_DATETIME + "'");
        break;
      case DATETIMEREM_ID:
        builder.setTables(ReminderContract.DateTimeAlert.TABLE_NAME);
        builder.appendWhere(ReminderContract.DateTimeAlert._ID + " = " +
                uri.getLastPathSegment());
        break;
      case ALL:
        builder.setTables(ReminderContract.DateTimeAlert.TABLE_NAME);
        break;
      case ALL_ID:
        builder.setTables(ReminderContract.All.TABLE_NAME);
        builder.appendWhere(ReminderContract.All._ID + " = " +
                uri.getLastPathSegment());
        break;
      default:
        throw new IllegalArgumentException(
                "Unsupported URI: " + uri);
    }
    Cursor cursor =
            builder.query(
                    db,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
    cursor.setNotificationUri(getContext().getContentResolver(), ReminderContract.BASE_CONTENT_URI);
    return cursor;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    switch (URI_MATCHER.match(uri)) {
      case LOCREM:
        return ReminderContract.LocationAlert.CONTENT_TYPE;
      case LOCREM_ID:
        return ReminderContract.LocationAlert.CONTENT_ITEM_TYPE;
      case DATETIMEREM:
        return ReminderContract.DateTimeAlert.CONTENT_TYPE;
      case DATETIMEREM_ID:
        return ReminderContract.DateTimeAlert.CONTENT_ITEM_TYPE;
      case ALL:
        return ReminderContract.All.CONTENT_TYPE;
      case ALL_ID:
        return ReminderContract.All.CONTENT_ITEM_TYPE;
      default:
        return null;
    }
  }


  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
    if (URI_MATCHER.match(uri) != LOCREM && URI_MATCHER.match(uri) != DATETIMEREM) {
      throw new IllegalArgumentException(
              "Unsupported URI for insertion: " + uri);
    }

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    if (URI_MATCHER.match(uri) == LOCREM) {
      long id = db.insert(ReminderContract.LocationAlert.TABLE_NAME, null, contentValues);
      getContext().getContentResolver().notifyChange(uri, null);
      return ContentUris.withAppendedId(uri, id);
    } else {
      long id = db.insert(ReminderContract.DateTimeAlert.TABLE_NAME, null, contentValues);
      getContext().getContentResolver().notifyChange(uri, null);
      return ContentUris.withAppendedId(uri, id);
    }
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    int delCount = 0;
    String id, where;
    switch (URI_MATCHER.match(uri)) {
      case LOCREM:
        delCount = db.delete(
                ReminderContract.LocationAlert.TABLE_NAME,
                selection,
                selectionArgs);
        break;
      case LOCREM_ID:
        id = uri.getLastPathSegment();
        where = ReminderContract.LocationAlert._ID + " = " + id;
        if (!TextUtils.isEmpty(selection)) {
          where += " AND " + selection;
        }
        delCount = db.delete(
                ReminderContract.LocationAlert.TABLE_NAME,
                where,
                selectionArgs);
        break;
      case DATETIMEREM:
        delCount = db.delete(
                ReminderContract.DateTimeAlert.TABLE_NAME,
                selection,
                selectionArgs);
        break;
      case DATETIMEREM_ID:
        id = uri.getLastPathSegment();
        where = ReminderContract.DateTimeAlert._ID + " = " + id;
        if (!TextUtils.isEmpty(selection)) {
          where += " AND " + selection;
        }
        delCount = db.delete(
                ReminderContract.DateTimeAlert.TABLE_NAME,
                where,
                selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
    // notify all listeners of changes:
    getContext().getContentResolver().notifyChange(uri, null);
    return delCount;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values,
                    @Nullable String selection, @Nullable String[] selectionArgs) {
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    int updateCount = 0;
    String id, where;
    switch (URI_MATCHER.match(uri)) {
      case LOCREM:
        updateCount = db.update(
                ReminderContract.LocationAlert.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        break;
      case LOCREM_ID:
        id = uri.getLastPathSegment();
        where = ReminderContract.LocationAlert._ID + " = " + id;
        if (!TextUtils.isEmpty(selection)) {
          where += " AND " + selection;
        }
        updateCount = db.update(
                ReminderContract.LocationAlert.TABLE_NAME,
                values,
                where,
                selectionArgs);
        break;
      case DATETIMEREM:
        updateCount = db.update(
                ReminderContract.LocationAlert.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        break;
      case DATETIMEREM_ID:
        id = uri.getLastPathSegment();
        where = ReminderContract.DateTimeAlert._ID + " = " + id;
        if (!TextUtils.isEmpty(selection)) {
          where += " AND " + selection;
        }
        updateCount = db.update(
                ReminderContract.DateTimeAlert.TABLE_NAME,
                values,
                where,
                selectionArgs);
        break;
      default:
        // no support for updating photos or entities
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
    // notify all listeners of changes:
    if (updateCount > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return updateCount;
  }
}

package com.example.taskreminder.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReminderDataHelper extends SQLiteOpenHelper {

  public static final String DATABASE_NAME = "reminderData.db";
  private static final int DATABASE_VERSION = 1;
  public static final String DB_TABLE_NAME = "reminders";
  public static final String DB_COLUMN_ID = "_id";
  public static final String DB_COLUMN_TYPE = "type";
  public static final String DB_COLUMN_TITLE = "title";
  public static final String DB_COLUMN_CONTENT = "content";
  public static final String DB_COLUMN_TIME = "time";
  public static final String DB_COLUMN_FREQUENCY = "frequency";
  public static final String DB_COLUMN_LOCATION_X= "locationX";
  public static final String DB_COLUMN_LOCATION_Y= "locationY";
  public static final String DB_COLUMN_LOCATION_NAME= "locationName";

  public ReminderDataHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + DB_TABLE_NAME + "(" +
            DB_COLUMN_ID + " INTEGER PRIMARY KEY, " +
            DB_COLUMN_TYPE + " TEXT, " +
            DB_COLUMN_TITLE + " TEXT, " +
            DB_COLUMN_CONTENT + " TEXT, " +
            DB_COLUMN_FREQUENCY + " TEXT, " +
            DB_COLUMN_TIME + " LONG, " +
            DB_COLUMN_LOCATION_X +" TEXT, "+
            DB_COLUMN_LOCATION_Y +" TEXT, "+
            DB_COLUMN_LOCATION_NAME +" TEXT)"
    );
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
    onCreate(db);
  }

  public Cursor getAllItems() {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery("SELECT * FROM " + DB_TABLE_NAME + " ORDER BY " + DB_COLUMN_ID + " DESC", null);
  }

  public Cursor getAllAlerts() {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery("SELECT * FROM " + DB_TABLE_NAME + " WHERE " +
            DB_COLUMN_TYPE + " = ? ", new String[]{ReminderContract.PATH_DATETIME});
  }

  public Cursor getAllNotes() {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.rawQuery("SELECT * FROM " + DB_TABLE_NAME + " WHERE " +
            DB_COLUMN_TYPE + " = ? ", new String[]{ReminderContract.PATH_LOC});
  }

  public boolean isEmpty(String type) {

    if (type.equals(ReminderContract.PATH_ALL)) return getAllItems().getCount() == 0;
    else if (type.equals(ReminderContract.PATH_DATETIME)) return getAllAlerts().getCount() == 0;
    else return getAllNotes().getCount() == 0;
  }

}

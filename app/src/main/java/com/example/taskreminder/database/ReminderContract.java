package com.example.taskreminder.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class ReminderContract {

  public static final String AUTHORITY =
          "com.example.taskreminder";

  public static final Uri BASE_CONTENT_URI =
          Uri.parse("content://" + AUTHORITY);

  public static final String PATH_ALL = "all";
  public static final String PATH_ALL_ID = "all/#";
  public static final String PATH_LOC = "locrem";
  public static final String PATH_LOC_ID = "locrem/#";
  public static final String PATH_DATETIME = "datetimerem";
  public static final String PATH_DATETIME_ID = "datetimerem/#";

  public static final String _ID = "_id";
  public static final String TYPE = "type";
  public static final String TITLE = "title";
  public static final String CONTENT = "content";
  public static final String TIME = "time";
  public static final String FREQUENCY = "frequency";

  public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT, TIME, FREQUENCY};

  public static final class LocationAlert implements BaseColumns {
    public static final String TABLE_NAME = "reminders";
    public static final String _ID = "_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    public static final String TIME = "time";
    public static final String FREQUENCY = "frequency";
    public static final String LOCATIONX= "locationX";
    public static final String LOCATIONY= "locationY";
    public static final String LOCATIONNAME= "locationName";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOC).build();

    // Custom MIME types
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_LOC;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_LOC;

    public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT,TIME,LOCATIONX,LOCATIONY,LOCATIONNAME};
  }

  public static final class DateTimeAlert implements BaseColumns {
    public static final String TABLE_NAME = "reminders";
    public static final String _ID = "_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String FREQUENCY = "frequency";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATETIME).build();

    // Custom MIME types
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_DATETIME;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_DATETIME;

    public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT, TIME, FREQUENCY};

  }

  public static final class All implements BaseColumns {
    public static final String TABLE_NAME = "reminders";
    public static final String _ID = "_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String FREQUENCY = "frequency";
    public static final String LOCATIONX= "locationX";
    public static final String LOCATIONY= "locationY";
    public static final String LOCATIONNAME= "locationName";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALL).build();

    // Custom MIME types
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_ALL;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_ALL;

    public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT, TIME, FREQUENCY,LOCATIONX,LOCATIONY,LOCATIONNAME};

  }
}
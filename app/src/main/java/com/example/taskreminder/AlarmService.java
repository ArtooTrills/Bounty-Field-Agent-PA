package com.example.taskreminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

import com.example.taskreminder.database.ReminderContract;
import com.example.taskreminder.database.ReminderDataHelper;
import com.example.taskreminder.views.activity.DateTimeReminderActivity;

public class AlarmService extends IntentService {

    public static final String CREATE = "CREATE";
    private IntentFilter matcher;

    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String MESSAGE_KEY = "msg";

    public AlarmService() {
        super("AlarmService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        int id = intent.getIntExtra(ID_KEY, 0);
        boolean value = intent.getBooleanExtra("isLocation",false);

        if (matcher.matchAction(action)) {
            execute(action, id, value);
        }
    }

    private void execute(String action, int id, boolean isLocation) {

        long timeInMilliseconds = 0;
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Uri uri = null;
        if(!isLocation) {
            uri = ContentUris.withAppendedId(ReminderContract.DateTimeAlert.CONTENT_URI,
                    id);
        }
        else
        {
            uri = ContentUris.withAppendedId(ReminderContract.LocationAlert.CONTENT_URI,
                    id);
        }

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();

        Intent intent = new Intent(this, AlarmReceiver.class);
        if(!isLocation) {


            intent.putExtra(ID_KEY, cursor.getInt(cursor.getColumnIndex(ReminderContract.DateTimeAlert._ID)));
            intent.putExtra(TITLE_KEY, cursor.getString(
                    cursor.getColumnIndex(ReminderContract.DateTimeAlert.TITLE)));
            intent.putExtra(MESSAGE_KEY, cursor.getString(
                    cursor.getColumnIndex(ReminderContract.DateTimeAlert.CONTENT)));

        }
        else
        {
            intent.putExtra(ID_KEY, cursor.getInt(cursor.getColumnIndex(ReminderContract.LocationAlert._ID)));
            intent.putExtra(TITLE_KEY, cursor.getString(
                    cursor.getColumnIndex(ReminderContract.LocationAlert.TITLE)));
            intent.putExtra(MESSAGE_KEY, cursor.getString(
                    cursor.getColumnIndex(ReminderContract.LocationAlert.CONTENT)));
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        timeInMilliseconds = cursor.getLong(cursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_TIME));

        if (CREATE.equals(action)) {
            alarm.setExact(AlarmManager.RTC_WAKEUP, timeInMilliseconds, pendingIntent);

        }
        cursor.close();
    }

}

package com.example.taskreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.taskreminder.database.ReminderContract;
import com.example.taskreminder.database.ReminderDataHelper;
import com.example.taskreminder.views.activity.DateTimeReminderActivity;

import java.util.Calendar;

public class AlarmReceiver extends WakefulBroadcastReceiver {

  private static final int HOURLY = 1, DAILY = 2;

  @Override
  public void onReceive(Context context, Intent intent) {

    int id = intent.getIntExtra(AlarmService.ID_KEY, 0);
    String title = intent.getStringExtra(AlarmService.TITLE_KEY);
    String msg = intent.getStringExtra(AlarmService.MESSAGE_KEY);

    Uri uri = ContentUris.withAppendedId(ReminderContract.All.CONTENT_URI, id);
    Cursor cursor = context.getContentResolver().query(uri,
            null, null, null, null);
    cursor.moveToFirst();

    int frequency = cursor.getInt(cursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_FREQUENCY));
    Calendar time = Calendar.getInstance();
    time.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ReminderDataHelper.DB_COLUMN_TIME)));

    if (frequency > 0) {
      if (frequency == HOURLY) {
        time.add(Calendar.HOUR, 1);

      } else if (frequency == DAILY) {
        time.add(Calendar.DATE, 1);
      }

      ContentValues values = new ContentValues();
      values.put(ReminderContract.DateTimeAlert.TIME, time.getTimeInMillis());
      uri = ContentUris.withAppendedId(ReminderContract.DateTimeAlert.CONTENT_URI, id);
      context.getContentResolver().update(uri, values, null, null);

      Intent setAlarm = new Intent(context, AlarmService.class);
      setAlarm.putExtra(AlarmService.ID_KEY, id);
      setAlarm.setAction(AlarmService.CREATE);
      context.startService(setAlarm);
    }

    NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
    bigStyle.setBigContentTitle(title);
    bigStyle.bigText(msg);
    Notification n = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(Notification.PRIORITY_MAX)
            .setWhen(0)
            .setStyle(bigStyle)
//            .setContentIntent(clicked)
            .setAutoCancel(true)
            .build();



    n.defaults |= Notification.DEFAULT_VIBRATE;
    n.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    n.defaults |= Notification.DEFAULT_SOUND;

    NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(id, n);


  }

}

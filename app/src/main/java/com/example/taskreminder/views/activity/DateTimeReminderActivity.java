package com.example.taskreminder.views.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import com.example.taskreminder.AlarmService;
import com.example.taskreminder.R;
import com.example.taskreminder.database.ReminderContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateTimeReminderActivity extends AppCompatActivity {

    private SimpleAdapter mAdapter;
    private EditText mContent, mTitle;
    private String mTime, mDate;
    private int mRepeatMode;
    private Map<String, String> mAlarmTime, mAlarmDate, mAlarmRepeat;
    private Calendar mAlertTime;
    private ContentResolver mContentResolver;

    private static final String NONE = "None";
    private static final String HOURLY = "Hourly";
    private static final String DAILY = "Daily";

    private static final String[] REPEAT_MODES =
            new String[]{NONE, HOURLY, DAILY};

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm aa");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");

    public static final String ID_KEY = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_datetime_reminder);

        mContentResolver = getContentResolver();

        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        mAlarmTime = new HashMap<String, String>();
        mAlarmDate = new HashMap<String, String>();
        mAlarmRepeat = new HashMap<String, String>();

        mContent = (EditText) findViewById(R.id.alertContent);
        mTitle = (EditText) findViewById(R.id.alertTitle);

        mRepeatMode = 0;

        mAlertTime = Calendar.getInstance();

        Calendar current = Calendar.getInstance();
        mTime = TIME_FORMAT.format(current.getTime());
        mDate = DATE_FORMAT.format(current.getTime());
        mAlertTime.setTimeInMillis(current.getTimeInMillis());
        getSupportActionBar().setTitle("Create Alert");

        mAlarmTime.put("mTitle", "Time");
        mAlarmTime.put("subtext", mTime);
        mAlarmDate.put("mTitle", "Date");
        mAlarmDate.put("subtext", mDate);
        mAlarmRepeat.put("mTitle", "Repeat");
        mAlarmRepeat.put("subtext", REPEAT_MODES[mRepeatMode]);

        mapList.add(mAlarmTime);
        mapList.add(mAlarmDate);
        mapList.add(mAlarmRepeat);
        mAdapter = new SimpleAdapter(this, mapList, android.R.layout.simple_list_item_2,
                new String[]{"mTitle", "subtext"}, new int[]{android.R.id.text1, android.R.id.text2});
        ListView listView = (ListView) findViewById(R.id.alertSettings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if first item in list (the mTime), then show timePickerDialog
                if (i == 0) {
                    TimePickerDialog timePicker = timePicker();
                    timePicker.show();
                    //if second item in list (the mDate), then show datePicker dialog
                } else if (i == 1) {
                    DatePickerDialog datePicker = datePicker();
                    datePicker.show();
                } else {
                    repeatDialog().show();
                }

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        saveAlert();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveAlert();
                break;

            default:
                break;

        }

        return true;
    }

    private TimePickerDialog timePicker() {
        return new TimePickerDialog(DateTimeReminderActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mAlertTime.set(Calendar.HOUR_OF_DAY, hour);
                        mAlertTime.set(Calendar.MINUTE, minute);
                        mAlertTime.set(Calendar.SECOND, 0);
                        mTime = TIME_FORMAT.format(mAlertTime.getTime());
                        mAlarmTime.put("subtext", mTime);
                        mAdapter.notifyDataSetChanged();
                    }
                }, mAlertTime.get(Calendar.HOUR_OF_DAY), mAlertTime.get(Calendar.MINUTE), false);
    }

    private DatePickerDialog datePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(DateTimeReminderActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mAlertTime.set(Calendar.YEAR, year);
                        mAlertTime.set(Calendar.MONTH, month);
                        mAlertTime.set(Calendar.DAY_OF_MONTH, day);
                        mDate = DATE_FORMAT.format(mAlertTime.getTime());
                        mAlarmDate.put("subtext", mDate);
                        mAdapter.notifyDataSetChanged();
                    }
                }, mAlertTime.get(Calendar.YEAR), mAlertTime.get(Calendar.MONTH),
                mAlertTime.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePicker;
    }

    private AlertDialog saveDialog(String title, String content, long time) {
        final long saveTime = time;
        final String saveMessage = content;
        final String saveTitle = title;

        return new AlertDialog.Builder(this)

                .setTitle("Confirm")
                .setMessage("Do you want to save?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int i) {
                        ContentValues values = new ContentValues();
                        values.put(ReminderContract.DateTimeAlert.TYPE, ReminderContract.PATH_DATETIME);
                        values.put(ReminderContract.DateTimeAlert.TITLE, saveTitle);
                        values.put(ReminderContract.DateTimeAlert.CONTENT, saveMessage);
                        values.put(ReminderContract.DateTimeAlert.TIME, saveTime);
                        values.put(ReminderContract.DateTimeAlert.FREQUENCY, mRepeatMode);
                        Uri uri = mContentResolver.insert(ReminderContract.LocationAlert.CONTENT_URI,
                                values);
                        createAlarm(Integer.parseInt(uri.getLastPathSegment()));
                        terminateActivity();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        terminateActivity();
                        dialog.dismiss();

                    }
                })
                .create();
    }

    private AlertDialog repeatDialog() {
        final int prevRepeat = mRepeatMode;
        return new AlertDialog.Builder(this)
                .setTitle("Repeat")
                .setSingleChoiceItems(REPEAT_MODES, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mRepeatMode = i;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mAlarmRepeat.put("subtext", REPEAT_MODES[mRepeatMode]);
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mRepeatMode = prevRepeat;
                    }
                })
                .create();
    }

    private void createAlarm(int id) {
        Intent alarm = new Intent(this, AlarmService.class);
        alarm.putExtra(ID_KEY, id);
        alarm.setAction(AlarmService.CREATE);
        startService(alarm);
    }

    private void terminateActivity() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void saveAlert() {
        String contentString = mContent.getText().toString();
        String titleString = mTitle.getText().toString();
        if (!(mAlertTime.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())) {
            saveDialog(titleString, contentString, mAlertTime.getTimeInMillis()).show();

        } else {
            saveDialog(titleString, contentString, Calendar.getInstance().getTimeInMillis()).show();
        }
    }

}

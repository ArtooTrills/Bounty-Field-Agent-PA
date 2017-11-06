package com.example.taskreminder.views.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.taskreminder.AlarmService;
import com.example.taskreminder.R;
import com.example.taskreminder.database.ReminderContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationReminderActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selectedLocation;
    private EditText titleText, taskDescription;
    private Button doneBtn, cancelBtn;
    private String latitude,longitude,locationName;

    private Cursor mCursor;
    private ContentResolver mContentResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_reminder);
        getSupportActionBar().setTitle("Create Alert");

        Intent intent = getIntent();
        latitude = Double.toString(intent.getDoubleExtra("Lat",0.00));
        longitude = Double.toString(intent.getDoubleExtra("Lon", 0.00));
        locationName = intent.getStringExtra("Loc");
        titleText =(EditText) findViewById(R.id.title);
        taskDescription = (EditText) findViewById(R.id.task_description);
        selectedLocation = (TextView) findViewById(R.id.location);
        selectedLocation.setText("Selected Location: "+locationName);

        doneBtn = (Button) findViewById(R.id.done);
        doneBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(this);
        mContentResolver = getContentResolver();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.done:
                ContentValues values = new ContentValues();
                values.put(ReminderContract.LocationAlert.TYPE, ReminderContract.PATH_LOC);
                values.put(ReminderContract.LocationAlert.TITLE, titleText.getText().toString());
                values.put(ReminderContract.LocationAlert.CONTENT, taskDescription.getText().toString());
                values.put(ReminderContract.LocationAlert.LOCATIONX, latitude);
                values.put(ReminderContract.LocationAlert.LOCATIONY, longitude);
                values.put(ReminderContract.LocationAlert.LOCATIONNAME, locationName);

                Uri uri = mContentResolver.insert(ReminderContract.LocationAlert.CONTENT_URI,
                        values);
                Intent mainIntent = new Intent(getApplicationContext(), RemindersListActivity.class);
                startActivity(mainIntent);
                finish();
                 break;

            case R.id.cancel:
                Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(mapIntent);
                finish();
                 break;
            default:
                 break;
        }
    }

}
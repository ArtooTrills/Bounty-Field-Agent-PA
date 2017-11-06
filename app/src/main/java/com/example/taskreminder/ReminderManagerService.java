package com.example.taskreminder;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.taskreminder.database.ReminderContract;

import java.util.ArrayList;

public class ReminderManagerService extends Service {

    private static final float MIN_RADIUS = 150;
    private static final int POLLING_INTERVAL = 1000;
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;
    private ArrayList<Reminder> activeReminders = new ArrayList<Reminder>();

    private ContentResolver mContentResolver;
    public static final String ID_KEY = "id";

    public ReminderManagerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContentResolver = getContentResolver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO
            return 0;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLLING_INTERVAL, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, POLLING_INTERVAL, 0, myLocationListener);


        Uri uri = ReminderContract.LocationAlert.CONTENT_URI;
        Cursor mCursor = mContentResolver.query(uri, null, null, null, null);
        Reminder reminder;
        mCursor.moveToFirst();
        if(mCursor.getCount()!=0) {
            if (mCursor.moveToFirst()) {
                do {
                    String id = mCursor.getString(mCursor.getColumnIndex(ReminderContract.LocationAlert._ID));
                    String titleString = mCursor.getString(mCursor.getColumnIndex(ReminderContract.LocationAlert.TITLE));
                    String contentString = mCursor.getString(mCursor.getColumnIndex(ReminderContract.LocationAlert.CONTENT));
                    String latitude = mCursor.getString(mCursor.getColumnIndex(ReminderContract.LocationAlert.LOCATIONX));
                    String longitude = mCursor.getString(mCursor.getColumnIndex(ReminderContract.LocationAlert.LOCATIONY));
                    String locName = mCursor.getString(mCursor.getColumnIndex(ReminderContract.LocationAlert.LOCATIONNAME));
                    reminder = new Reminder(Double.parseDouble(latitude), Double.parseDouble(longitude), locName, titleString, contentString,id);
                    activeReminders.add(reminder);
                } while (mCursor.moveToNext());
            }
            mCursor.close();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            for(int i=0;i < activeReminders.size(); i++){
                float[] results = new float[1];
                Location.distanceBetween(latitude, longitude, activeReminders.get(i).latitude, activeReminders.get(i).longitude, results);
                if(results[0] < MIN_RADIUS){
                    showNotification(activeReminders.get(i));
                    activeReminders.remove(i);
                }
            }
        }

        private void showNotification(Reminder reminder) {
            createAlarm(Integer.valueOf(reminder.getId()));
        }

        private void createAlarm(int id) {
            Intent alarm = new Intent(ReminderManagerService.this, AlarmService.class);
            alarm.putExtra(ID_KEY, id);
            alarm.putExtra("isLocation",true);
            alarm.setAction(AlarmService.CREATE);
            startService(alarm);
        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
    }


}

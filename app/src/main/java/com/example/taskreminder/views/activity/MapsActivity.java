package com.example.taskreminder.views.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.taskreminder.GeoResultCheck;
import com.example.taskreminder.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    @Nullable
    private GoogleMap googleMap;
    private String txt;
    private Geocoder geoCoder;
    private ArrayAdapter<String> adapter;
    private String[] languages;
    private List<Address> addresses;
    private String locationName = "";
    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        getSupportActionBar().setTitle("Select Location");

        ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        if (gMap == null) {
            Toast.makeText(getApplicationContext(),
                    " Unable to create maps!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            String provider_info = "";
            if (isGPSEnabled) {
                System.out.println("Gps is Enable");
                provider_info = LocationManager.GPS_PROVIDER;

            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                System.out.println("Network Is Enable");
                provider_info = LocationManager.NETWORK_PROVIDER;
            }

            // Application can use GPS or Network Provider
            if (!provider_info.isEmpty()) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    //if permission is not granted, get permission
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                } else {
                    //if permission is granted set location at the current location as soon as app opens
                    locationManager.requestLocationUpdates(provider_info,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this);

                    Location location;
                    if (locationManager != null) {
                        System.out.println("Provider Infos " + provider_info);
                        location = locationManager.getLastKnownLocation(provider_info);
                        System.out.println("Location " + location);
                        if (location != null) {
                            onLocationChanged(location);
                        } else {
                            provider_info = LocationManager.NETWORK_PROVIDER;
                            location = locationManager.getLastKnownLocation(provider_info);
                            System.out.println("Location " + location);
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    }
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMapLongClickListener(this);
                googleMap.setOnMarkerClickListener(this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void loadLatlong(Address address) {
        double latitude = address.getLatitude();
        double longitude = address.getLongitude();
        System.out.println("Test :: Latit " + latitude);
        LatLng latLng = new LatLng(latitude, longitude);
        createMarker(latLng);
    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        System.out.println("Test :: Latit " + latitude);
        LatLng latLng = new LatLng(latitude, longitude);
        String address = getThelocation(latitude, longitude);
        if (address != null)
            locationName = address;
        createMarker(latLng);
    }

    public void createMarker(LatLng latLng) {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    latLng).zoom(15).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;

        System.out.println("Test :: Latitude " + lat + " long" + lon + "LocationName " + locationName);
        Intent in = new Intent(MapsActivity.this, LocationReminderActivity.class);
        in.putExtra("Lat", lat);
        in.putExtra("Lon", lon);
        in.putExtra("Loc", locationName);
        startActivity(in);
        finish();
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        System.out.println("Test ::  Long click lat " + latLng.latitude + "longi " + latLng.longitude);
        ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        pd.setMessage("Checking Location");
        pd.show();
        String address = getThelocation(latLng.latitude, latLng.longitude);
        pd.dismiss();
        System.out.println("Test Address " + address);
        if (address != null)
            locationName = address;
        createMarker(latLng);
    }

    private String getThelocation(double latitude, double longitude) {
        Geocoder gcoder = new Geocoder(MapsActivity.this, Locale.ENGLISH);
        try {
            List<Address> address = gcoder.getFromLocation(latitude, longitude, 1);
            GeoResultCheck geoResultCheck = new GeoResultCheck(address.get(0));
            return geoResultCheck.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}

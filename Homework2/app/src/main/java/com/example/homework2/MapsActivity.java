package com.example.homework2;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class  MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        SensorEventListener
{

    private static final int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 101;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    Marker gpsMarker = null;
    List<Marker> markerList;
    private TextView accelerometer_text;
    private FloatingActionButton StartSensor;
    private FloatingActionButton StopSensor;
    SensorManager SM;
    Sensor mySensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        markerList = new ArrayList<>();

        SM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        accelerometer_text = (TextView)findViewById(R.id.accelerometer_text);
        StartSensor = (FloatingActionButton)findViewById(R.id.StartSensor);
        StopSensor = (FloatingActionButton)findViewById(R.id.StopSensor);

        StartSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animateDown = ObjectAnimator.ofFloat(StartSensorUse(), "translationY", 200f);
                animateDown.setDuration(0);
                ObjectAnimator animationUp = ObjectAnimator.ofFloat(StartSensorUse(), "translationY", 0f);
                animationUp.setDuration(1000);
            }
        });
        StopSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animateDown = ObjectAnimator.ofFloat(StopSensorUse(), "translationY", 200f);
                animateDown.setDuration(0);
                ObjectAnimator animationUp = ObjectAnimator.ofFloat(StopSensorUse(), "translationY", 0f);
                animationUp.setDuration(1000);
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }
    @Override
    public void onMapLoaded() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }



        createLocationRequest();
        createLocationCallback();
        startLocationUpdates();
    }
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }






    @Override
    public boolean onMarkerClick(Marker marker) {
        ObjectAnimator animateDown = ObjectAnimator.ofFloat(showButtons() , "translationY", 200f);
        animateDown.setDuration(0);
        ObjectAnimator animationUp = ObjectAnimator.ofFloat(showButtons(), "translationY", 0f);
        animationUp.setDuration(1000);

        return false;
    }
    private float showButtons(){
        StartSensor.setVisibility(View.VISIBLE);
        StopSensor.setVisibility(View.VISIBLE);
        return 0;}
        public float StartSensorUse(){
            accelerometer_text.setVisibility(View.INVISIBLE);
            StartSensor.setVisibility(View.INVISIBLE);
            StopSensor.setVisibility(View.INVISIBLE);
            return 0;
        }
        public float StopSensorUse(){
            accelerometer_text.setVisibility(View.VISIBLE);
            return 0;
        }
    @Override
    public void onMapClick(LatLng latLng) {
        ObjectAnimator animateDown = ObjectAnimator.ofFloat(hideButtons() , "translationY", 200f);
        animateDown.setDuration(0);
        ObjectAnimator animationUp = ObjectAnimator.ofFloat(hideButtons(), "translationY", 0f);
        animationUp.setDuration(1000);
        hideButtons();
    }

    private float hideButtons(){
        StartSensor.setVisibility(View.INVISIBLE);
        StopSensor.setVisibility(View.INVISIBLE);
        return 0;
    }
    @SuppressLint("DefaultLocale")
    private void SetInformation(SensorEvent sensorEvent) {
        String information;
        information = String.format("Acceleration:\n" +
                "X(%.4f), Y(%.4f)", sensorEvent.values[0], sensorEvent.values[1]);

        accelerometer_text.setText(information);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            SetInformation(sensorEvent);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void clearMemoryClick(View view) {
        for(Marker marker : markerList)
            marker.remove();
        accelerometer_text.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onMapLongClick(LatLng latLng) {


        @SuppressLint("DefaultLocale") Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2))
                .alpha(0.8f)
                .title(String.format("Position: (%.2f,%.2f)", latLng.latitude, latLng.longitude)));
        markerList.add(marker);
    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback,null);

    }
    private void stopLocationUpdates() {
        if (locationCallback != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }

        private void createLocationCallback() {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if(locationResult!=null)
                        gpsMarker.remove();
                    Location location = locationResult.getLastLocation();
                    gpsMarker=mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(),location.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                            .alpha(0.8f)
                            .title("Current Location"));
                }
            };
        }
        public void zoomInClick(View v){
            mMap.moveCamera(CameraUpdateFactory.zoomIn());
        }
        public void zoomOutClick(View v){
            mMap.moveCamera(CameraUpdateFactory.zoomOut());
        }


}

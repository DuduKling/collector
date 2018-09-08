package com.dudukling.collector.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.R;
import com.dudukling.collector.formActivity;

import java.io.IOException;


public class gpsController {
    public static final int GPS_REQUEST_CODE = 111;

    private Activity activity;
    private final Button gpsButton;
    private boolean activeGPS;
    private mapsController mapsControl;

    private final TextInputLayout editTextGPSLatitude;
    private final TextInputLayout editTextGPSLongitude;

    private final EditText fieldEditTextGPSLatitude;
    private final EditText fieldEditTextGPSLongitude;
    private final EditText fieldEditTextGPSAltitude;

    private static LocationManager locationManager;
    private static LocationListener locationListener;



    public gpsController(formActivity activity, Button gpsButton, TextInputLayout editTextGPSLatitude, TextInputLayout editTextGPSLongitude, EditText fieldEditTextGPSAltitude) {
        this.activity = activity;
        this.gpsButton = gpsButton;
        this.editTextGPSLatitude = editTextGPSLatitude;
        this.editTextGPSLongitude = editTextGPSLongitude;
        this.fieldEditTextGPSLatitude = editTextGPSLatitude.getEditText();
        this.fieldEditTextGPSLongitude = editTextGPSLongitude.getEditText();
        this.fieldEditTextGPSAltitude = fieldEditTextGPSAltitude;
    }

    public void setGPSValues(formActivity formActivity) {
        mapsControl = new mapsController(formActivity);

        fieldEditTextGPSLatitude.setFocusableInTouchMode(false);
        fieldEditTextGPSLatitude.setFocusable(false);
        fieldEditTextGPSLongitude.setFocusableInTouchMode(false);
        fieldEditTextGPSLongitude.setFocusable(false);

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        final ProgressBar progressBar = activity.findViewById(R.id.progressBarGPS);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_REQUEST_CODE);
            gpsButton.setBackgroundResource(R.drawable.ic_play);
        }else{
            final double[] longitude = {0};
            final double[] latitude = {0};
            final double[] altitude = {0};

            activeGPS = true;
            gpsButton.setBackgroundResource(R.drawable.ic_pause);
            progressBar.setVisibility(View.VISIBLE);

            locationListener = new LocationListener() {
                @SuppressLint("SetTextI18n")
                public void onLocationChanged(Location location) {
                    if(location != null) {
                        progressBar.setVisibility(View.INVISIBLE);

                        longitude[0] = location.getLongitude();
                        latitude[0] = location.getLatitude();
                        altitude[0] = location.getAltitude();

                        fieldEditTextGPSLatitude.setText("" + latitude[0], TextView.BufferType.EDITABLE);
                        fieldEditTextGPSLongitude.setText("" + longitude[0], TextView.BufferType.EDITABLE);
                        fieldEditTextGPSAltitude.setText("" + altitude[0], TextView.BufferType.EDITABLE);

                        try {
                            mapsControl.getCurrentPlace(latitude[0], longitude[0]);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Couldn't find this location address..", Toast.LENGTH_SHORT).show();
                        }

                        mapsControl.startMaps();
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
                    if(provider.equals(LocationManager.GPS_PROVIDER)){
                        Toast.makeText(activity, "Please, enable GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(startGPSIntent);
                    }
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            mapsControl.startMaps();
        }
    }

    public void toggleGPS(final formActivity activity) {
        final ProgressBar progressBar = activity.findViewById(R.id.progressBarGPS);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activeGPS){
                    disableGPSUpdates();
                    progressBar.setVisibility(View.INVISIBLE);

                    fieldEditTextGPSLatitude.setFocusableInTouchMode(true);
                    fieldEditTextGPSLatitude.setFocusable(true);
                    fieldEditTextGPSLatitude.setTextColor(Color.parseColor("#000000"));
                    fieldEditTextGPSLatitude.setCursorVisible(true);

                    fieldEditTextGPSLongitude.setFocusableInTouchMode(true);
                    fieldEditTextGPSLongitude.setFocusable(true);
                    fieldEditTextGPSLongitude.setTextColor(Color.parseColor("#000000"));
                    fieldEditTextGPSLongitude.setCursorVisible(true);
                }else{
                    setGPSValues(activity);
                    gpsButton.setBackgroundResource(R.drawable.ic_pause);
                    fieldEditTextGPSLatitude.setFocusableInTouchMode(false);
                    fieldEditTextGPSLatitude.setFocusable(false);
                    fieldEditTextGPSLatitude.setTextColor(Color.parseColor("#616161"));
                    fieldEditTextGPSLatitude.setCursorVisible(false);

                    fieldEditTextGPSLongitude.setFocusableInTouchMode(false);
                    fieldEditTextGPSLongitude.setFocusable(false);
                    fieldEditTextGPSLongitude.setTextColor(Color.parseColor("#616161"));
                    fieldEditTextGPSLongitude.setCursorVisible(false);
                }
            }
        });
    }

    private void disableGPSUpdates() {
        activeGPS = false;
        locationManager.removeUpdates(locationListener);
        gpsButton.setBackgroundResource(R.drawable.ic_play);
    }

    public void disableGPS() {
        if(activeGPS){
            activeGPS = false;
            locationManager.removeUpdates(locationListener);
        }
    }

    public void onChangeLatLong() {
        fieldEditTextGPSLatitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String text = fieldEditTextGPSLatitude.getText().toString();
                    if(!text.isEmpty()){
                        if(text.matches("^-?([1-8]?[1-9]|[1-9]0)\\.\\d{1,8}")){
                            disableGPSUpdates();
                            mapsControl.startMaps();

                        }else{editTextGPSLatitude.setError("Invalid format!");}
                    }else{editTextGPSLatitude.setError("Required Field!");}
                }else{
                    editTextGPSLatitude.setError(null);
                    editTextGPSLatitude.setErrorEnabled(false);
                }
            }
        });

        fieldEditTextGPSLongitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String text = fieldEditTextGPSLongitude.getText().toString();
                    if(!text.isEmpty()){
                        if(text.matches("^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\\.\\d{1,8}")){
                            disableGPSUpdates();
                            mapsControl.startMaps();

                        }else{editTextGPSLongitude.setError("Invalid format!");}
                    }else{editTextGPSLongitude.setError("Required Field!");}
                }else{
                    editTextGPSLongitude.setError(null);
                    editTextGPSLongitude.setErrorEnabled(false);
                }
            }
        });

//        fieldEditTextGPSLatitude.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s){}
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(fieldEditTextGPSLatitude.hasFocus()) {
//                    disableGPSUpdates();
//                    mapsControl.startMaps();
//                }
//            }
//        });
//
//        fieldEditTextGPSLongitude.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s){}
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(fieldEditTextGPSLongitude.hasFocus()) {
//                    disableGPSUpdates();
//                    mapsControl.startMaps();
//                }
//            }
//        });
    }

    public void setActiveGPS(boolean activeGPS) {
        this.activeGPS = activeGPS;
    }

    public void setGPSButtonBgPlay(){
        gpsButton.setBackgroundResource(R.drawable.ic_play);
    }

    public void setGPSButtonGone(){
        gpsButton.setVisibility(View.GONE);
    }
}

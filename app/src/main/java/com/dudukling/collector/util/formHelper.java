package com.dudukling.collector.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.MapFragment;
import com.dudukling.collector.R;
import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.formActivity;
import com.dudukling.collector.model.Sample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class formHelper {
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static boolean activeGPS;
    private final ImageView imageViewSample;
    private static formActivity activity;
    private int sampleID;
    private static Button gpsButton;

    private TextView fieldIDForm;
    private TextView fieldDateForm;

    private EditText fieldSpecies;
    private EditText fieldCollectorName;
    private EditText fieldSpeciesFamily;
    private EditText fieldAuthor;
    private EditText fieldSampleDescription;
    private EditText fieldAmbientDescription;
    private EditText fieldNotes;

    private static EditText fieldEditTextGPSLatitude;
    private static EditText fieldEditTextGPSLongitude;
    private static EditText fieldEditTextGPSAltitude;


    public formHelper(final formActivity activity, String formType, Sample sample) {
        this.activity = activity;

        fieldIDForm = activity.findViewById(R.id.TextViewIDForm);
        fieldDateForm = activity.findViewById(R.id.TextViewDateForm);
        Button cameraButton = activity.findViewById(R.id.buttonCamera);
        Button albumButton = activity.findViewById(R.id.buttonAlbum);
        gpsButton = activity.findViewById(R.id.buttonGPS);

        imageViewSample = activity.findViewById(R.id.imageViewSampleForm);


        TextInputLayout textInputCollectorName = activity.findViewById(R.id.editTextCollectorName);
        fieldCollectorName = textInputCollectorName.getEditText();

        TextInputLayout textInputSpecies = activity.findViewById(R.id.editTextSpecies);
        fieldSpecies = textInputSpecies.getEditText();

        TextInputLayout textInputSpeciesFamily = activity.findViewById(R.id.editTextSpeciesFamily);
        fieldSpeciesFamily = textInputSpeciesFamily.getEditText();

        TextInputLayout textInputAuthor = activity.findViewById(R.id.editTextAuthor);
        fieldAuthor = textInputAuthor.getEditText();

        TextInputLayout textInputSampleDescription = activity.findViewById(R.id.editTextSampleDescription);
        fieldSampleDescription = textInputSampleDescription.getEditText();

        TextInputLayout textInputAmbientDescription = activity.findViewById(R.id.editTextAmbientDescription);
        fieldAmbientDescription = textInputAmbientDescription.getEditText();

        TextInputLayout textInputNotes = activity.findViewById(R.id.editTextNotes);
        fieldNotes = textInputNotes.getEditText();

        TextInputLayout editTextGPSLatitude = activity.findViewById(R.id.editTextGPSLatitude);
        fieldEditTextGPSLatitude = editTextGPSLatitude.getEditText();

        TextInputLayout editTextGPSLongitude = activity.findViewById(R.id.editTextGPSLongitude);
        fieldEditTextGPSLongitude = editTextGPSLongitude.getEditText();

        TextInputLayout editTextGPSAltitude = activity.findViewById(R.id.editTextGPSAltitude);
        fieldEditTextGPSAltitude = editTextGPSAltitude.getEditText();


        cameraButton.setVisibility(View.GONE);
        albumButton.setVisibility(View.GONE);

        if (formType.equals("readOnly")) {
            disableEditText(fieldCollectorName);
            disableEditText(fieldSpecies);
            disableEditText(fieldSpeciesFamily);
            disableEditText(fieldAuthor);
            disableEditText(fieldSampleDescription);
            disableEditText(fieldAmbientDescription);
            disableEditText(fieldNotes);
            disableEditText(fieldEditTextGPSLatitude);
            disableEditText(fieldEditTextGPSLongitude);
            disableEditText(fieldEditTextGPSAltitude);

            sampleID = sample.getId();

            List<String> images = sample.getImagesList();
            if(images.size() > 1) {
                albumButton.setVisibility(View.VISIBLE);
            }

            gpsButton.setVisibility(View.GONE);
            fillForm(sample);

            startMaps();
        }

        if (formType.equals("new")) {
            sampleID = 0;

            fieldDateForm.setText("Date: " + todayDate());
            fieldIDForm.setText("ID: #" + (getLastID(activity) + 1));

            cameraButton.setVisibility(View.VISIBLE);

            setGPSValues(activity);
            toggleGPS(activity);
            onChangeLatLong();
        }

        if (formType.equals("edit")) {
            sampleID = sample.getId();
            fillForm(sample);

            activeGPS = false;
            gpsButton.setBackgroundResource(R.drawable.ic_play);
            toggleGPS(activity);
            onChangeLatLong();

            cameraButton.setVisibility(View.VISIBLE);
            List<String> images = sample.getImagesList();
            if(images.size() > 1) {
                albumButton.setVisibility(View.VISIBLE);
            }

            startMaps();

        }
    }

    private void onChangeLatLong() {
        fieldEditTextGPSLatitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s){}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(fieldEditTextGPSLatitude.hasFocus()) {
                    disableGPSUpdates();
                    startMaps();
                }
            }
        });

        fieldEditTextGPSLongitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s){}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(fieldEditTextGPSLongitude.hasFocus()) {
                    disableGPSUpdates();
                    startMaps();
                }
            }
        });
    }

    private int getLastID(formActivity activity) {
        sampleDAO dao = new sampleDAO(activity);
        int lastID = dao.lastID();
        dao.close();

        return lastID;
    }

    private String todayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(todayDate);
    }

    public Sample getSample(List<String> imagesList) {
        Sample sample = new Sample();

        sample.setDate(todayDate());
        sample.setId(sampleID);

        sample.setSpecies(fieldSpecies.getText().toString());
        sample.setCollectorName(fieldCollectorName.getText().toString());
        sample.setSpeciesFamily(fieldSpeciesFamily.getText().toString());
        sample.setAuthor(fieldAuthor.getText().toString());
        sample.setSampleDescription(fieldSampleDescription.getText().toString());
        sample.setAmbientDescription(fieldAmbientDescription.getText().toString());
        sample.setNotes(fieldNotes.getText().toString());

        sample.setGPSLatitude(fieldEditTextGPSLatitude.getText().toString());
        sample.setGPSLongitude(fieldEditTextGPSLongitude.getText().toString());
        sample.setGPSAltitude(fieldEditTextGPSAltitude.getText().toString());


        sampleDAO dao = new sampleDAO(activity);
        List<String> imagesListDB = dao.getImagesDB(sample.getId());

        List<String> newestImageList = new ArrayList<>();
        newestImageList.addAll(imagesListDB);
        newestImageList.addAll(imagesList);

        sample.setImagesList(newestImageList);

        //Toast.makeText(activity,""+formActivity.imagesList,Toast.LENGTH_LONG).show();

        return sample;
    }

    public void fillForm(Sample sample) {
        fieldIDForm.setText("ID: #" + sample.getId());
        fieldDateForm.setText("Date: " + sample.getDate());

        fieldSpecies.setText(sample.getSpecies());
        fieldCollectorName.setText(sample.getCollectorName());
        fieldSpeciesFamily.setText(sample.getSpeciesFamily());
        fieldAuthor.setText(sample.getAuthor());
        fieldSampleDescription.setText(sample.getSampleDescription());
        fieldAmbientDescription.setText(sample.getAmbientDescription());
        fieldNotes.setText(sample.getNotes());

        fieldEditTextGPSLatitude.setText(sample.getGPSLatitude());
        fieldEditTextGPSLongitude.setText(sample.getGPSLongitude());
        fieldEditTextGPSAltitude.setText(sample.getGPSAltitude());

        List<String> images = sample.getImagesList();
        if(images.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));
            Bitmap smallerBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            imageViewSample.setImageBitmap(smallerBitmap);
            imageViewSample.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        //editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setTextColor(Color.parseColor("#616161"));
    }

    public static void setGPSValues(final formActivity activity) {
        gpsButton.setBackgroundResource(R.drawable.ic_pause);
        fieldEditTextGPSLatitude.setFocusableInTouchMode(false);
        fieldEditTextGPSLatitude.setFocusable(false);
        fieldEditTextGPSLongitude.setFocusableInTouchMode(false);
        fieldEditTextGPSLongitude.setFocusable(false);

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},111);
            return;
        }else{
            final double[] longitude = {0};
            final double[] latitude = {0};
            final double[] altitude = {0};

            activeGPS = true;

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(location != null) {
                        longitude[0] = location.getLongitude();
                        latitude[0] = location.getLatitude();
                        altitude[0] = location.getAltitude();

                        fieldEditTextGPSLatitude.setText("" + latitude[0], TextView.BufferType.EDITABLE);
                        fieldEditTextGPSLongitude.setText("" + longitude[0], TextView.BufferType.EDITABLE);
                        fieldEditTextGPSAltitude.setText("" + altitude[0], TextView.BufferType.EDITABLE);

                        startMaps();
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
                        Toast.makeText(activity, "Favor habilitar o GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(startGPSIntent);
                    }
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            startMaps();
        }
    }

    public static void toggleGPS(final formActivity activity) {
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activeGPS){
                    disableGPSUpdates();
                    fieldEditTextGPSLatitude.setFocusableInTouchMode(true);
                    fieldEditTextGPSLatitude.setFocusable(true);
                    fieldEditTextGPSLongitude.setFocusableInTouchMode(true);
                    fieldEditTextGPSLongitude.setFocusable(true);
                }else{
                    setGPSValues(activity);
                    gpsButton.setBackgroundResource(R.drawable.ic_pause);
                    fieldEditTextGPSLatitude.setFocusableInTouchMode(false);
                    fieldEditTextGPSLatitude.setFocusable(false);
                    fieldEditTextGPSLongitude.setFocusableInTouchMode(false);
                    fieldEditTextGPSLongitude.setFocusable(false);


                }
            }
        });
    }

    private static void disableGPSUpdates() {
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

    public static void deleteImagesFromPhoneMemory(Sample sample) {
        List<String> imagesListToDelete = sample.getImagesList();
        //Toast.makeText(formActivity.this, "Deletando "+imagesListToDelete.size()+"!", Toast.LENGTH_LONG).show();
        for(int i = 0; i < imagesListToDelete.size(); i++){
            File file = new File(imagesListToDelete.get(i));
            file.delete();
        }
    }

    private static void startMaps() {
        fixScrollForMaps();

        FragmentManager fragManager = activity.getSupportFragmentManager();
        FragmentTransaction tx = fragManager.beginTransaction();
        tx.replace(R.id.mapFrame, new MapFragment(activity));
        tx.commit();
    }

    private static void fixScrollForMaps() {
        final ScrollView mainScrollView = activity.findViewById(R.id.main_scrollview);
        ImageView transparentImageView = activity.findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }
}

package com.dudukling.collector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class formActivity extends AppCompatActivity {

    public static final String REGISTER_TITLE = "Register";
    public static final String REGISTRY_TITLE = "Registry #";
    public static final String EDIT_TITLE = "Edit #";
    private formHelper helperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setNewOrEditOrReadForm();
    }

    private void getGPSValues() {
        TextInputLayout editTextGPSLatitude = findViewById(R.id.editTextGPSLatitude);
        final EditText fieldEditTextGPSLatitude = editTextGPSLatitude.getEditText();

        TextInputLayout editTextGPSLongitude = findViewById(R.id.editTextGPSLongitude);
        final EditText fieldEditTextGPSLongitude = editTextGPSLongitude.getEditText();

        TextInputLayout editTextGPSAltitude = findViewById(R.id.editTextGPSAltitude);
        final EditText fieldEditTextGPSAltitude = editTextGPSAltitude.getEditText();

        // GPS:
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final double[] longitude = {location.getLongitude()};
        final double[] latitude = {location.getLatitude()};
        final double[] altitude = {location.getAltitude()};

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude[0] = location.getLongitude();
                latitude[0] = location.getLatitude();
                altitude[0] = location.getAltitude();

                fieldEditTextGPSLatitude.setText("" + latitude[0], TextView.BufferType.EDITABLE);
                fieldEditTextGPSLongitude.setText("" + longitude[0], TextView.BufferType.EDITABLE);
                fieldEditTextGPSAltitude.setText("" + altitude[0], TextView.BufferType.EDITABLE);
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
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    private void setNewOrEditOrReadForm() {
        Intent intent = getIntent();
        Sample sample = (Sample) intent.getSerializableExtra("sample");
        Boolean readOnly = (Boolean) intent.getSerializableExtra("readOnly");

        if (sample != null) {
            if(readOnly != null && readOnly) {
                setFormReadOnly(sample);
            }else{
                setFormEdit(sample);
            }
        }else{
            setFormNew();
        }
    }

    private void setFormNew() {
        setTitle(REGISTER_TITLE);
        helperForm = new formHelper(this, todayDate(), 0, false);

        getGPSValues();

        TextView tv1 = findViewById(R.id.TextViewDateForm);
        tv1.setText("Date: " + todayDate());

        sampleDAO dao = new sampleDAO(this);
        int lastID = dao.nextID();
        dao.close();
        TextView tv2 = findViewById(R.id.TextViewIDForm);
        tv2.setText("ID: #" + (lastID + 1));
    }

    private void setFormEdit(Sample sample) {
        setTitle(EDIT_TITLE +sample.getId());
        helperForm = new formHelper(this, todayDate(), sample.getId(), false);
        helperForm.fillForm(sample);
    }

    private void setFormReadOnly(Sample sample) {
        setTitle(REGISTRY_TITLE +sample.getId());
        helperForm = new formHelper(this, todayDate(), sample.getId(), true);
        helperForm.fillForm(sample);

        Button cameraButton = findViewById(R.id.buttonCamera);
        cameraButton.setVisibility(View.GONE);
    }

    private String todayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(todayDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_form, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_save_button:
                Sample sample = helperForm.getSample();

                sampleDAO dao = new sampleDAO(formActivity.this);
//                Toast.makeText(formActivity.this, "edit/insert: " + String.valueOf(sample.getId()), Toast.LENGTH_SHORT).show();
                if(sample.getId() != 0){
                    dao.edit(sample);
                } else {
                    dao.insert(sample);
                }
                dao.close();

//                Toast.makeText(formActivity.this, "Salvando: " + sample.getSpecies() + " !", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

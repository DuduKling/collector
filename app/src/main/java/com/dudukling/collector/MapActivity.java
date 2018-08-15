package com.dudukling.collector;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction tx = fragManager.beginTransaction();
        tx.replace(R.id.mapFrame, new SupportMapFragment());
        tx.commit();
    }
}

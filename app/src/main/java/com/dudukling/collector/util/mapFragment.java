package com.dudukling.collector.util;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.R;
import com.dudukling.collector.formActivity;
import com.dudukling.collector.model.Sample;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;


public class mapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static formActivity activity;
    private static double[] latitude = {0};
    private static double[] longitude = {0};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity = (formActivity) getActivity();
        getMapAsync(this);
    }

    private static void getGPSFromInput(){
        TextInputLayout editTextGPSLatitude = activity.findViewById(R.id.editTextGPSLatitude);
        String StringGPSLatitude = editTextGPSLatitude.getEditText().getText().toString();

        TextInputLayout editTextGPSLongitude = activity.findViewById(R.id.editTextGPSLongitude);
        String StringGPSLongitude = editTextGPSLongitude.getEditText().getText().toString();

        if(!StringGPSLatitude.equals("") && !StringGPSLongitude.equals("")){
            try{
                latitude[0] = Double.parseDouble(StringGPSLatitude);
                longitude[0] = Double.parseDouble(StringGPSLongitude);
            }catch(NumberFormatException e){}
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getGPSFromInput();

        //Toast.makeText(activity, "POSICAO: "+latitude[0]+","+longitude[0], Toast.LENGTH_LONG).show();

        if(latitude[0] !=0 && longitude[0] !=0){
            LatLng posicao = new LatLng(latitude[0], longitude[0]);
            if(posicao != null){
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicao, 18);
                mMap.moveCamera(update);

                MarkerOptions marker = new MarkerOptions();
                marker.position(posicao);
                marker.title("You are here!");
                //marker.snippet("oi");
                mMap.addMarker(marker);
            }
        }
    }
}

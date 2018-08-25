package com.dudukling.collector;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;
import com.dudukling.collector.util.CSVWriter;
import com.dudukling.collector.util.formHelper;
import com.dudukling.collector.util.recyclerAdapter;

import java.io.File;
import java.io.FileWriter;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class collectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        recyclerView = findViewById(R.id.collection_list);

        startNewRegisterButton();
        registerForContextMenu(recyclerView);
    }

    private void startNewRegisterButton() {
        FloatingActionButton buttonNewSample = findViewById(R.id.buttonNewSample);
        buttonNewSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Toast.makeText(collectionActivity.this, "Clicado!", Toast.LENGTH_SHORT).show();

            Intent goToFormActivity = new Intent(collectionActivity.this, formActivity.class);
            goToFormActivity.putExtra("type", "new");
            startActivity(goToFormActivity);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_export_csv:
                Toast.makeText(this, "Exportando..", Toast.LENGTH_SHORT).show();
                exportDB();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadRecycler() {
        sampleDAO dao = new sampleDAO(this);
        List<Sample> samples = dao.getSamples();
        dao.close();

        recyclerView.setAdapter(new recyclerAdapter(samples, this));

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void exportDB() {
        sampleDAO dao = new sampleDAO(this);

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()){
            exportDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(new Date());
        File file = new File(exportDir, "CollectionExport_"+timeStamp+".csv");

        try{
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            SQLiteDatabase db = dao.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM Collection",null);

            String[] LatLong = {"LatDeg", "LatMin", "LatSec", "LatHem", "LongDeg", "LongMin", "LongSec", "LongHem"};
            String[] fromDB = curCSV.getColumnNames();

            String[] str = new String[fromDB.length + LatLong.length];
            for(int i=0; i<=(fromDB.length+LatLong.length-1); i++){
                if(i<=(fromDB.length-1)){
                    str[i] = fromDB[i];
                }else{
                    str[i] = LatLong[i-fromDB.length];
                }
            }
            
            csvWrite.writeNext(str);

            while(curCSV.moveToNext()){
                //Which column you want to export
                String s0 = curCSV.getString(0);
                String s1 = curCSV.getString(1);
                String s2 = curCSV.getString(2);
                String s3 = curCSV.getString(3);
                String s4 = curCSV.getString(4);
                String s5 = curCSV.getString(5);
                String s6 = curCSV.getString(6);
                String s7 = curCSV.getString(7);
                String s8 = curCSV.getString(8);
                String latitude = curCSV.getString(9);
                String longitude = curCSV.getString(10);
                String s11 = curCSV.getString(11);

                s0 = stripAccents(s0);
                s1 = stripAccents(s1);
                s2 = stripAccents(s2);
                s3 = stripAccents(s3);
                s4 = stripAccents(s4);
                s5 = stripAccents(s5);
                s6 = stripAccents(s6);
                s7 = stripAccents(s7);
                s8 = stripAccents(s8);
                latitude = stripAccents(latitude);
                longitude = stripAccents(longitude);
                s11 = stripAccents(s11);


                String LatHemisphere;
                String LongHemisphere;

                if (Double.parseDouble(latitude) < 0) {
                    LatHemisphere = "S";
                } else {
                    LatHemisphere = "N";
                }

                if (Double.parseDouble(longitude) < 0) {
                    LongHemisphere = "W";
                } else {
                    LongHemisphere = "E";
                }

                String latitudeDegrees = Location.convert(Math.abs(Double.parseDouble(latitude)), Location.FORMAT_SECONDS);
                String[] latitudeSplit = latitudeDegrees.split(":");
                String longitudeDegrees = Location.convert(Math.abs(Double.parseDouble(longitude)), Location.FORMAT_SECONDS);
                String[] longitudeSplit = longitudeDegrees.split(":");

                String arrStr[] ={s0, s1, s2, s3, s4, s5, s6, s7, s8, latitude, longitude, s11, latitudeSplit[0], latitudeSplit[1], latitudeSplit[2], LatHemisphere, longitudeSplit[0], longitudeSplit[1], longitudeSplit[2], LongHemisphere};

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

        }catch(Exception sqlEx){
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
}

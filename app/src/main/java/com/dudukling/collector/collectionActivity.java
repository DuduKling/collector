package com.dudukling.collector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.collector.dao.sampleDAO;
import com.dudukling.collector.model.Sample;
import com.dudukling.collector.util.CSVWriter;
import com.dudukling.collector.util.recyclerAdapter;

import java.io.File;
import java.io.FileWriter;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class collectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private recyclerAdapter RecyclerAdapter;

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
        checkRecords();
    }

    public void checkRecords() {
        sampleDAO dao = new sampleDAO(this);
        TextView textViewNoRecord = this.findViewById(R.id.textViewNoRecord);
        if (dao.lastID() == 0) {textViewNoRecord.setVisibility(View.VISIBLE);
        }else{
            textViewNoRecord.setVisibility(View.GONE);
            loadRecycler();
        }
        dao.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection, menu);

        //final MenuItem searchItem = menu.findItem(R.id.action_search);
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search...");
//        searchView.setOnQueryTextListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_export_csv:
                exportDB();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadRecycler() {
        sampleDAO dao = new sampleDAO(this);
        List<Sample> samples = dao.getSamples();
        dao.close();

        RecyclerAdapter = new recyclerAdapter(samples, this);
        recyclerView.setAdapter(RecyclerAdapter);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void exportDB() {
        sampleDAO dao = new sampleDAO(this);

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()){
            boolean dirCreated = exportDir.mkdirs();
            Log.d("TAG1", "exportDB() called: "+dirCreated);
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        File file = new File(exportDir, "CollectionExport_"+timeStamp+".csv");

        try{
            boolean fileCreated = file.createNewFile();
            Log.d("TAG2", "createNewFile() called: "+fileCreated);
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            SQLiteDatabase db = dao.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT id, date, number, speciesFamily, genus, species, collector, locnotes, notes, latitude, longitude, altitude, hasFlower, hasFruit, country, state, city, neighborhood FROM Collection",null);

            String[] str = {"ID", "colldd", "collmm", "collyy", "number", "family", "genus", "sp", "collector", "notes", "lat_grau", "lat_min", "lat_sec", "ns", "long_grau", "long_min", "long_sec", "ew", "Altitude", "Flower", "Fruit", "country", "majorarea", "minorarea", "gazetteer", "locnotes"};

            csvWrite.writeNext(str);

            while(curCSV.moveToNext()){
                // Column you want get from DB:
                String id = curCSV.getString(0);
                String date = curCSV.getString(1);
                String number = stripAccents(curCSV.getString(2));
                String speciesFamily = stripAccents(curCSV.getString(3));
                String genus = stripAccents(curCSV.getString(4));
                String species = stripAccents(curCSV.getString(5));
                String collector = stripAccents(curCSV.getString(6));
                String locnotes = stripAccents(curCSV.getString(7));
                String notes = stripAccents(curCSV.getString(8));
                String latitude = curCSV.getString(9);
                String longitude = curCSV.getString(10);
                String altitude = curCSV.getString(11);
                String flower = curCSV.getString(12);
                String fruit = curCSV.getString(13);
                String country = stripAccents(curCSV.getString(14));
                String state = stripAccents(curCSV.getString(15));
                String city = stripAccents(curCSV.getString(16));
                String neighborhood = stripAccents(curCSV.getString(17));

                // Fix GPS:
                String LatHemisphere;
                String LongHemisphere;
                if (Double.parseDouble(latitude) < 0){LatHemisphere = "S";
                }else{LatHemisphere = "N";}
                if (Double.parseDouble(longitude) < 0){LongHemisphere = "W";
                }else{LongHemisphere = "E";}
                String latitudeDegrees = Location.convert(Math.abs(Double.parseDouble(latitude)), Location.FORMAT_SECONDS);
                String[] latitudeSplit = latitudeDegrees.split(":");
                String longitudeDegrees = Location.convert(Math.abs(Double.parseDouble(longitude)), Location.FORMAT_SECONDS);
                String[] longitudeSplit = longitudeDegrees.split(":");

                // Fix Date:
                String[] dateSplit = date.split("/");


                String arrStr[] = {id, dateSplit[0], dateSplit[1], dateSplit[2], number, speciesFamily, genus, species, collector, notes, latitudeSplit[0], latitudeSplit[1], latitudeSplit[2], LatHemisphere, longitudeSplit[0], longitudeSplit[1], longitudeSplit[2], LongHemisphere, altitude, flower, fruit, country, state, city, neighborhood, locnotes};

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

        }catch(Exception sqlEx){
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }

        dao.close();
        Toast.makeText(this, "Exported!", Toast.LENGTH_SHORT).show();
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

}

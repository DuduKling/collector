package com.dudukling.collector.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudukling.collector.model.Sample;

import java.util.ArrayList;
import java.util.List;

public class sampleDAO extends SQLiteOpenHelper {
    public sampleDAO(Context context) {
        super(context, "Collection", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Collection (" +
                " id INTEGER PRIMARY KEY," +
                " date TEXT NOT NULL," +
                " collectorName TEXT NOT NULL," +
                " species TEXT NOT NULL," +
                " speciesFamily TEXT NOT NULL," +
                " author TEXT NOT NULL," +
                " sampleDescription TEXT NOT NULL," +
                " ambientDescription TEXT NOT NULL," +
                " notes TEXT NOT NULL" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Collection";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(Sample sample) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();

        queryData.put("date", sample.getDate());
        queryData.put("collectorName", sample.getCollectorName());

        queryData.put("species", sample.getSpecies());
        queryData.put("speciesFamily", sample.getSpeciesFamily());
        queryData.put("author", sample.getAuthor());
        queryData.put("sampleDescription", sample.getSampleDescription());
        queryData.put("ambientDescription", sample.getAmbientDescription());
        queryData.put("notes", sample.getNotes());

        db.insert("Collection", null, queryData);
//        String sql = "INSERT INTO Collection (idNum, species, date) VALUES (?,?,?)";
    }

    public List<Sample> getSamples() {
        String sql = "SELECT * FROM Collection";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        List<Sample> samples =  new ArrayList<Sample>();
        while(c.moveToNext()){
            Sample sample = new Sample();

            sample.setId(c.getInt(c.getColumnIndex("id")));
            sample.setDate(c.getString(c.getColumnIndex("date")));

            sample.setCollectorName(c.getString(c.getColumnIndex("collectorName")));
            sample.setSpecies(c.getString(c.getColumnIndex("species")));
            sample.setSpeciesFamily(c.getString(c.getColumnIndex("speciesFamily")));
            sample.setAuthor(c.getString(c.getColumnIndex("author")));
            sample.setSampleDescription(c.getString(c.getColumnIndex("sampleDescription")));
            sample.setAmbientDescription(c.getString(c.getColumnIndex("ambientDescription")));
            sample.setNotes(c.getString(c.getColumnIndex("notes")));


            samples.add(sample);
        }
        c.close();

        return samples;
    }

    public void delete(Sample sample) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(sample.getId())};
        db.delete("Collection","id = ?", params);
    }

    public int nextID(){
        String sql = "SELECT MAX(id) AS LAST FROM Collection";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int ID = c.getInt(0);
        c.close();

        return ID;
    }
}

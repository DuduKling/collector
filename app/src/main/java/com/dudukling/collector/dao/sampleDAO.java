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
        String sql = "CREATE TABLE Collection (id INTEGER PRIMARY KEY, idNum INTEGER, species TEXT NOT NULL, date TEXT NOT NULL);";
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
        queryData.put("idNum", sample.getIdNum());
        queryData.put("species", sample.getSpecies());
        queryData.put("date", sample.getDate());

        db.insert("Collection", null, queryData);
        String sql = "INSERT INTO Collection (idNum, species, date) VALUES (?,?,?)";
    }

    public List<Sample> getSamples() {
        String sql = "SELECT * FROM Collection";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        List<Sample> samples =  new ArrayList<Sample>();
        while(c.moveToNext()){
            Sample sample = new Sample();

            sample.setIdNum(c.getString(c.getColumnIndex("idNum")));
            sample.setSpecies(c.getString(c.getColumnIndex("species")));
            sample.setDate(c.getString(c.getColumnIndex("date")));

            samples.add(sample);
        }
        c.close();

        return samples;
    }
}

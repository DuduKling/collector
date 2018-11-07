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
                " number TEXT NOT NULL," +
                " speciesFamily TEXT NOT NULL," +
                " genus TEXT NOT NULL," +
                " species TEXT NOT NULL," +
                " collector TEXT NOT NULL," +
                " notes TEXT NOT NULL," +
                " locnotes TEXT NOT NULL," +
                " latitude TEXT NOT NULL," +
                " longitude TEXT NOT NULL," +
                " altitude TEXT NOT NULL," +
                " hasFlower TEXT NOT NULL," +
                " hasFruit TEXT NOT NULL," +
                " country TEXT NOT NULL," +
                " state TEXT NOT NULL," +
                " city TEXT NOT NULL," +
                " neighborhood TEXT NOT NULL" +
                ");";
        db.execSQL(sql);

        String sql2 = "CREATE TABLE Images ( " +
                "id INTEGER PRIMARY KEY, " +
                "path TEXT NOT NULL, " +
                "collectionID INTEGER NOT NULL, " +
                "FOREIGN KEY (collectionID) REFERENCES Collection(id)" +
                ");";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Collection";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(Sample sample) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = getContentValues(sample);
        db.insert("Collection", null, queryData);


        // Add pictures:
        ContentValues queryData2 = new ContentValues();
        List<String> images = sample.getImagesList();
        int lastId = lastID();

        for(int i=0; i <= (images.size() - 1); i++){
            queryData2.clear();
            queryData2.put("path", images.get(i));
            queryData2.put("collectionID", lastId);
            db.insert("Images", null, queryData2);
        }

        //Toast.makeText(formActivity,""+images,Toast.LENGTH_LONG).show();
    }

    private ContentValues getContentValues(Sample sample) {
        ContentValues queryData = new ContentValues();

        queryData.put("date", sample.getDate());

        queryData.put("number", sample.getNumber());
        queryData.put("species", sample.getSpecies());
        queryData.put("speciesFamily", sample.getSpeciesFamily());
        queryData.put("genus", sample.getGenus());
        queryData.put("collector", sample.getCollector());
        queryData.put("notes", sample.getNotes());
        queryData.put("locnotes", sample.getLocnotes());

        queryData.put("latitude", sample.getGPSLatitude());
        queryData.put("longitude", sample.getGPSLongitude());
        queryData.put("altitude", sample.getGPSAltitude());

        queryData.put("hasFlower", sample.getHasFlower());
        queryData.put("hasFruit", sample.getHasFruit());

        queryData.put("country", sample.getGeoCountry());
        queryData.put("state", sample.getGeoState());
        queryData.put("city", sample.getGeoCity());
        queryData.put("neighborhood", sample.getGeoNeighborhood());

        return queryData;
    }

    public List<Sample> getSamples() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM Collection";

        Cursor c = db.rawQuery(sql, null);
        List<Sample> samples = new ArrayList<>();

        while (c.moveToNext()) {
            Sample sample = new Sample();

            int dbSampleID = c.getInt(c.getColumnIndex("id"));
            sample.setId(dbSampleID);
            sample.setDate(c.getString(c.getColumnIndex("date")));

            sample.setNumber(c.getString(c.getColumnIndex("number")));
            sample.setSpeciesFamily(c.getString(c.getColumnIndex("speciesFamily")));
            sample.setGenus(c.getString(c.getColumnIndex("genus")));
            sample.setSpecies(c.getString(c.getColumnIndex("species")));
            sample.setCollector(c.getString(c.getColumnIndex("collector")));
            sample.setNotes(c.getString(c.getColumnIndex("notes")));
            sample.setLocnotes(c.getString(c.getColumnIndex("locnotes")));

            sample.setGPSLatitude(c.getString(c.getColumnIndex("latitude")));
            sample.setGPSLongitude(c.getString(c.getColumnIndex("longitude")));
            sample.setGPSAltitude(c.getString(c.getColumnIndex("altitude")));

            sample.setHasFlower(c.getString(c.getColumnIndex("hasFlower")));
            sample.setHasFruit(c.getString(c.getColumnIndex("hasFruit")));

            sample.setGeoCountry(c.getString(c.getColumnIndex("country")));
            sample.setGeoState(c.getString(c.getColumnIndex("state")));
            sample.setGeoCity(c.getString(c.getColumnIndex("city")));
            sample.setGeoNeighborhood(c.getString(c.getColumnIndex("neighborhood")));

            // Pegando imagens
            List<String> imagesList = getImagesDB(dbSampleID);
            sample.setImagesList(imagesList);

            samples.add(sample);
        }

        c.close();

        return samples;
    }

    public List<String> getImagesDB(int dbSampleID) {
        SQLiteDatabase db = getReadableDatabase();

        List<String> imagesList = new ArrayList<>();
        String sql2 = "SELECT * FROM Images WHERE collectionID = ?";
        Cursor c2 = db.rawQuery(sql2, new String[]{String.valueOf(dbSampleID)});
        while (c2.moveToNext()) {
            imagesList.add(c2.getString(c2.getColumnIndex("path")));
        }
        c2.close();

        return imagesList;
    }

    public void delete(Sample sample) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(sample.getId())};
        db.delete("Collection","id = ?", params);
        db.delete("Images","collectionID = ?", params);
    }

    public int lastID(){
        String sql = "SELECT MAX(id) AS LAST FROM Collection";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int ID = c.getInt(0);
        c.close();

        return ID;
    }

    public void edit(Sample sample) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(sample);
        String[] params = {String.valueOf(sample.getId())};
        db.update("Collection", queryData, "id=?", params);

        int sampleId = sample.getId();
        deleteImagesFromDB(sampleId);

        // Add all pictures:
        ContentValues queryData2 = new ContentValues();
        List<String> images = sample.getImagesList();

        for(int i=0; i <= (images.size() - 1); i++){
            queryData2.clear();
            queryData2.put("path", images.get(i));
            queryData2.put("collectionID", sampleId);
            db.insert("Images", null, queryData2);
        }
    }

    private void deleteImagesFromDB(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(id)};
        db.delete("Images","collectionID = ?", params);
    }

    public void deleteImageFromDB(int collectionID, int imageID) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(collectionID), String.valueOf(imageID)};
        db.delete("Images","collectionID = ? AND id = ?", params);
    }

    public void truncateDBs() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Collection", null,null);
        db.delete("Images", null,null);
    }

    public int getImageIdDB(String imagePath) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT id FROM Images WHERE path = ?";
        Cursor c = db.rawQuery(sql, new String[]{imagePath});
        c.moveToFirst();
        int imageID = c.getInt(0);
        c.close();

        return imageID;
    }

}

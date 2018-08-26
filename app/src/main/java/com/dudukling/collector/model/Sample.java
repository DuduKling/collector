package com.dudukling.collector.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sample implements Serializable {
    private int id;
    private String date;

    private String species;
    private String collectorName;
    private String speciesFamily;
    private String author;
    private String sampleDescription;
    private String ambientDescription;
    private String notes;

    private String GPSLatitude;
    private String GPSLongitude;
    private String GPSAltitude;

    private String hasFlower;
    private String hasFruit;

    private List<String> imagesList  = new ArrayList<String>();



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    public String getSpeciesFamily() {
        return speciesFamily;
    }

    public void setSpeciesFamily(String speciesFamily) {
        this.speciesFamily = speciesFamily;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSampleDescription() {
        return sampleDescription;
    }

    public void setSampleDescription(String sampleDescription) {
        this.sampleDescription = sampleDescription;
    }

    public String getAmbientDescription() {
        return ambientDescription;
    }

    public void setAmbientDescription(String ambientDescription) {
        this.ambientDescription = ambientDescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getGPSLatitude() {
        return GPSLatitude;
    }

    public void setGPSLatitude(String GPSLatitude) {
        this.GPSLatitude = GPSLatitude;
    }

    public String getGPSLongitude() {
        return GPSLongitude;
    }

    public void setGPSLongitude(String GPSLongitude) {
        this.GPSLongitude = GPSLongitude;
    }

    public String getGPSAltitude() {
        return GPSAltitude;
    }

    public void setGPSAltitude(String GPSAltitude) {
        this.GPSAltitude = GPSAltitude;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getHasFlower() {
        return hasFlower;
    }

    public void setHasFlower(String hasFlower) {
        this.hasFlower = hasFlower;
    }

    public String getHasFruit() {
        return hasFruit;
    }

    public void setHasFruit(String hasFruit) {
        this.hasFruit = hasFruit;
    }
}

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

    private List<String> imagesList  = new ArrayList<String>();
    private String GPSLatitudeHem;
    private String GPSLatitudeDeg;
    private String GPSLatitudeMin;
    private String GPSLatitudeSec;
    private String GPSLongitudeHem;
    private String GPSLongitudeDeg;
    private String GPSLongitudeMin;
    private String GPSLongitudeSec;


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

    public void setGPSLatitudeHem(String GPSLatitudeHem) {
        this.GPSLatitudeHem = GPSLatitudeHem;
    }

    public void setGPSLatitudeDeg(String GPSLatitudeDeg) {
        this.GPSLatitudeDeg = GPSLatitudeDeg;
    }

    public void setGPSLatitudeMin(String GPSLatitudeMin) {
        this.GPSLatitudeMin = GPSLatitudeMin;
    }

    public void setGPSLatitudeSec(String GPSLatitudeSec) {
        this.GPSLatitudeSec = GPSLatitudeSec;
    }

    public void setGPSLongitudeHem(String GPSLongitudeHem) {
        this.GPSLongitudeHem = GPSLongitudeHem;
    }

    public void setGPSLongitudeDeg(String GPSLongitudeDeg) {
        this.GPSLongitudeDeg = GPSLongitudeDeg;
    }

    public void setGPSLongitudeMin(String GPSLongitudeMin) {
        this.GPSLongitudeMin = GPSLongitudeMin;
    }

    public void setGPSLongitudeSec(String GPSLongitudeSec) {
        this.GPSLongitudeSec = GPSLongitudeSec;
    }

    public String getGPSLatitudeHem() {
        return GPSLatitudeHem;
    }

    public String getGPSLatitudeDeg() {
        return GPSLatitudeDeg;
    }

    public String getGPSLatitudeMin() {
        return GPSLatitudeMin;
    }

    public String getGPSLatitudeSec() {
        return GPSLatitudeSec;
    }

    public String getGPSLongitudeHem() {
        return GPSLongitudeHem;
    }

    public String getGPSLongitudeDeg() {
        return GPSLongitudeDeg;
    }

    public String getGPSLongitudeMin() {
        return GPSLongitudeMin;
    }

    public String getGPSLongitudeSec() {
        return GPSLongitudeSec;
    }
}

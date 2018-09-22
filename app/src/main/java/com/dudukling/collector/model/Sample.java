package com.dudukling.collector.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sample implements Serializable {
    private int id;
    private String date;

    private String number;
    private String speciesFamily;
    private String genus;
    private String species;
    private String collector;
    private String notes;
    private String locnotes;

    private String GPSLatitude;
    private String GPSLongitude;
    private String GPSAltitude;

    private String GeoCountry;
    private String GeoState;
    private String GeoCity;
    private String GeoNeighborhood;
    //private String GeoStreet;
    //private String GeoNumber;

    private String hasFlower;
    private String hasFruit;

    private List<String> imagesList  = new ArrayList<>();



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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSpeciesFamily() {
        return speciesFamily;
    }

    public void setSpeciesFamily(String speciesFamily) {
        this.speciesFamily = speciesFamily;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getLocnotes() {
        return locnotes;
    }

    public void setLocnotes(String locnotes) {
        this.locnotes = locnotes;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
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

    public String getGeoCountry() {
        return GeoCountry;
    }

    public void setGeoCountry(String geoCountry) {
        GeoCountry = geoCountry;
    }

    public String getGeoState() {
        return GeoState;
    }

    public void setGeoState(String geoState) {
        GeoState = geoState;
    }

    public String getGeoCity() {
        return GeoCity;
    }

    public void setGeoCity(String geoCity) {
        GeoCity = geoCity;
    }

    public String getGeoNeighborhood() {
        return GeoNeighborhood;
    }

    public void setGeoNeighborhood(String geoNeighborhood) {
        GeoNeighborhood = geoNeighborhood;
    }

}

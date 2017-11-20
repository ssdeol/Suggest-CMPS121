package com.example.ssdeol.suggest.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ssdeol on 11/19/17.
 */

// This class will store data for the places. It is necessary to do it here because
// then we can use it anywhere and it will be stored into out memory.
// Basically this class holds a bunch of getter and setter methods for each variable.

public class PlaceInfo {

    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private String attributions;
    private float rating;
    private LatLng latLng;
    private Uri websiteUri;

    public PlaceInfo(String name,
                     String address,
                     String phoneNumber,
                     String id,
                     String attributions,
                     float rating,
                     LatLng latLng,
                     Uri websiteUri) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.attributions = attributions;
        this.rating = rating;
        this.latLng = latLng;
        this.websiteUri = websiteUri;
    }

    public PlaceInfo(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", attributions='" + attributions + '\'' +
                ", rating=" + rating +
                ", latLng=" + latLng +
                ", websiteUri=" + websiteUri +
                '}';
    }
}

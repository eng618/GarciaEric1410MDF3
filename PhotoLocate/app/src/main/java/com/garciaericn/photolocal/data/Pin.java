package com.garciaericn.photolocal.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Full Sail University
 * Mobile Development BS
 * Created by ENG618-Mac on 10/22/14.
 */
public class Pin implements Serializable{
    public static final long serialVersionUID = 5743257589655875125L;

    public static final String LAT_LNG = "com.garciaericn.photolocal.data.Pin.LAT_LNG";
    public static final String PIN = "com.garciaericn.photolocal.data.Pin.PIN";

    // Object fields
    private double latitude;
    private double longitude;
    private String title;
    private String description;
    private String imageUriString;

    public Pin() {

    }

    public Pin(LatLng latLng, String title, String description) {
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
        setTitle(title);
        setDescription(description);
    }

    /**
     * Getter and Setter Methods
     * */

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUriString() {
        return imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }
}

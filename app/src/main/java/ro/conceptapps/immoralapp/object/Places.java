package ro.conceptapps.immoralapp.object;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;

public class Places implements Serializable {

    private long place_id;
    private String name;
    private String type;
    private String class_type;
    private double importance;
    private double lat;
    private double lng;
    private double latSV;
    private double latNE;
    private double lngSV;
    private double lngNE;

    public Places() {
    }


    public long getPlace_id() {
        return place_id;
    }

    public void setPlace_id(long place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClass_type() {
        return class_type;
    }

    public void setClass_type(String class_type) {
        this.class_type = class_type;
    }

    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public LatLng getLocation() {
        return new LatLng(lat, lng);
    }


    public LatLngBounds getLatLngBounds() {
        return new LatLngBounds(new LatLng(this.latSV, this.lngSV), new LatLng(this.latNE, this.lngNE));
    }

    public void setLatSV(double latSV) {
        this.latSV = latSV;
    }

    public void setLatNE(double latNE) {
        this.latNE = latNE;
    }

    public void setLngSV(double lngSV) {
        this.lngSV = lngSV;
    }

    public void setLngNE(double lngNE) {
        this.lngNE = lngNE;
    }
}
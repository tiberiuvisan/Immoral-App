package ro.conceptapps.immoralapp.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by andreea on 5/13/2015.
 */
public class Pin implements Serializable, ClusterItem {

    public int id;
    public int userId;
    public String type;
    public String description;
    public double lat;
    public double lng;


    @Override
    public String toString() {
        return id + ", "
                +userId + ", "
                +type +", "
                +description +", "
                +lat +", "
                +lng;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }
}

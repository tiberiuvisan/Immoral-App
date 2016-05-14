package licenta.fastbanking.Objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tiberiu Visan on 5/8/2016.
 * Project: FastBanking
 */
public class Coordinate {

    public float lat;
    public float lng;


    public LatLng getLatLng(){
        return  new LatLng(lat,lng);
    }
}

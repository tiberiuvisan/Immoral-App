package ro.conceptapps.immoralapp.object;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;

public class CustomPolyline implements Serializable {

    private double latSV;
    private double latNE;
    private double lngSV;
    private double lngNE;
    private String encodedPolyline;

    public CustomPolyline() {
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

    public String getEncodedPolyline() {
        return encodedPolyline;
    }

    public void setEncodedPolyline(String encodedPolyline) {
        this.encodedPolyline = encodedPolyline;
    }
}

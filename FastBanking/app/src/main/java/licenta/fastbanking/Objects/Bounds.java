package licenta.fastbanking.Objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Tiberiu Visan on 5/8/2016.
 * Project: FastBanking
 */
public class Bounds {

    public Coordinate northeast;
    public Coordinate southwest;



    public LatLngBounds getLatLngBounds() {

        return new LatLngBounds(southwest.getLatLng(), northeast.getLatLng());
    }


}


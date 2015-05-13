package ro.conceptapps.immoralapp.map;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tiberiu Visan on 5/12/2015.
 * Project: Immoral-App
 */
public class MapUtils  {


    private static final String TAG = "MapUtils";
    private GoogleMap map;
    private Context ctx;
    private SharedPreferences sp;


    public MapUtils(Context ctx, GoogleMap map){
        this.ctx = ctx;
        this.map = map;
        sp = ctx.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);

    }


    public void zoomToLocation(LatLng latLng, int zoomLevel) {

        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(latLng).zoom(zoomLevel).build()), 1000, null);
    }

    public void setUpMap() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

            }
        });
    }
}

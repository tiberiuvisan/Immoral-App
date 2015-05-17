package ro.conceptapps.immoralapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.map.GPSLocation;
import ro.conceptapps.immoralapp.map.MapUtils;
import ro.conceptapps.immoralapp.utils.Pin;
import ro.conceptapps.immoralapp.utils.PinDbHelper;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MapActivity";
    private Toolbar toolbar;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng latLng;
    private SharedPreferences sp;
    private MapUtils mapUtils;
    private ArrayList<Pin> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        sp = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        setUpMapIfNeeded();
        markers = PinDbHelper.getPinsFromDatabase(this);

    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onResume() {
        //in momentul in care se revine in activitate, un nou set de coordonate este creeat din
        //ultimele valori puse. Astfel, utilizatorul nu va fi trimis niciodata pe coordonate (0,0)
        latLng = new LatLng(sp.getFloat("SHARED_LAT", 0), sp.getFloat("SHARED_LNG", 0));
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        //in momentul in care activitatea este oprita, se salveaza ultima locatie in SharedPreferences;
        sp.edit().putFloat("SHARED_LAT", (float) latLng.latitude).apply();
        sp.edit().putFloat("SHARED_LNG", (float) latLng.latitude).apply();
        GPSLocation.getLastInstance().disconnect();
        super.onPause();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mapUtils = new MapUtils(this, mMap);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mapUtils.setUpMap();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng centru = new LatLng(45.40, 25.10);
                mapUtils.setCenter(centru);
                getLocation();
                for (Pin marker : markers) {
                    mapUtils.addToCluster(marker);
                   /* mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(marker.lat, marker.lng))
                    .title(marker.type)
                    .snippet(marker.description));*/
                    mapUtils.recluster();
                }
                mapUtils.recluster();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Pin pin = new Pin();
                pin.lat = latLng.latitude;
                pin.lng = latLng.longitude;
                pin.description = "ASDF";
                pin.type = "1";
                pin.userId = 1;

                mapUtils.addToCluster(pin);
                mapUtils.recluster();

                addMarkerToDatabase(latLng);

            }
        });
    }

    private void getLocation() {
        Log.d(TAG, "in get Location");
        new GPSLocation(this, new GPSLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mapUtils.zoomToLocation(latLng, 15);
                    Log.d(TAG, "in get Location");
                    //GPSLocation.getLastInstance().disconnect();
                }
            }
        });
    }

    private void addMarkerToDatabase(LatLng latLng) {
        PinDbHelper.addPinToDatabase(this, 1, "ASfD", "asadfenad aisda", latLng.latitude, latLng.longitude);
    }
}

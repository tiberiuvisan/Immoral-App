package licenta.fastbanking.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import licenta.fastbanking.Managers.SessionManager;
import licenta.fastbanking.Objects.Pin;
import licenta.fastbanking.R;
import licenta.fastbanking.Utils.Constants;
import licenta.fastbanking.Utils.PinDbHelper;

public class MainActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";

    private GoogleMap mMap;
    private MapUtils mapUtils;
    private LatLng latLng;
    private ArrayList<Pin>pins;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        setupMapIfNeeded();
        pins = PinDbHelper.getPinsFromDatabase(this);

    }


    @Override
    protected void onResume() {
        //in momentul in care se revine in activitate, un nou set de coordonate este creeat din
        //ultimele valori puse. Astfel, utilizatorul nu va fi trimis niciodata pe coordonate (0,0)
        if (GPSLocation.getLastInstance() != null)
            GPSLocation.getLastInstance().connect();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (GPSLocation.getLastInstance() != null)
            GPSLocation.getLastInstance().disconnect();
        super.onPause();
    }

    private void setupMapIfNeeded() {
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

    // functia care adauga punctele din baza de date pe harta ( functiile ajutatoare de adaugare puncte
    // sunt din mapUtils, se vad mai jos care)

    private void setUpMap() {
        mapUtils.setUpMap();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng centru = new LatLng(45.40, 25.10);
                mapUtils.setCenter(centru);
                getLocation();
                for (Pin pin : pins) {
                    mapUtils.addToCluster(pin);
                    mapUtils.recluster();
                }
                mapUtils.recluster();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addMarker(latLng);

            }
        });
    }

    //salveaza ultima locatie din GPS periodic
    private void getLocation() {
        Log.d(TAG, "in get Location");
        new GPSLocation(this, new GPSLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    sp.edit().putFloat(Constants.LOCATION_LAST_LAT, (float) latLng.latitude).apply();
                    sp.edit().putFloat(Constants.LOCATION_LAST_LNG, (float) latLng.longitude).apply();
                    Log.d(TAG, "in get Location");
                }
            }
        });
    }

    private void addMarker(final LatLng latLng) {
        final Pin pin = new Pin();

        PinDbHelper.addPinToDatabase(MainActivity.this, SessionManager.getInstance().getId(), "lala", "lalala", latLng.latitude, latLng.longitude);
        pin.lat = latLng.latitude;
        pin.lng = latLng.longitude;
        pin.description = "lalala";
        pin.type = "lala";
        pin.userId = SessionManager.getInstance().getId();
        mapUtils.addToCluster(pin);
        mapUtils.recluster();
    }


}

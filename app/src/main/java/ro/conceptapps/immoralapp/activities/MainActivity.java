package ro.conceptapps.immoralapp.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.map.GPSLocation;
import ro.conceptapps.immoralapp.map.MapUtils;
import ro.conceptapps.immoralapp.utils.Constants;
import ro.conceptapps.immoralapp.utils.Pin;
import ro.conceptapps.immoralapp.utils.PinDbHelper;
import ro.conceptapps.immoralapp.utils.SessionManager;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MapActivity";
    private static final long DELAY_TIME = 5 * 1000;
    private Toolbar toolbar;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng latLng;
    private SharedPreferences sp;
    private MapUtils mapUtils;
    private ArrayList<Pin> markers;
    private static String activityType, activityDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        sp = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        setUpMapIfNeeded();
        markers = PinDbHelper.getPinsFromDatabase(this);
        handleIntent(getIntent());
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
        latLng = new LatLng(sp.getFloat(Constants.SHARED_PREFS_LASTLAT, 0), sp.getFloat(Constants.SHARED_PREFS_LASTLNG, 0));
        super.onResume();
    }

    @Override
    protected void onPause() {
        //in momentul in care activitatea este oprita, se salveaza ultima locatie in SharedPreferences;
        sp.edit().putFloat(Constants.SHARED_PREFS_LASTLAT, (float) latLng.latitude).apply();
        sp.edit().putFloat(Constants.SHARED_PREFS_LASTLNG, (float) latLng.longitude).apply();
        if(GPSLocation.getLastInstance()!=null)
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

    private void addMarker(final LatLng latLng) {
        AlertDialogWrapper.Builder adb = new AlertDialogWrapper.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog));
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_add_pin, null);

        final Spinner type = (Spinner) view.findViewById(R.id.type);
        final TextView desc = (TextView) view.findViewById(R.id.description);
        final Pin pin = new Pin();

        ArrayList<String> typeList = new ArrayList<>();
        final String immoralActivities[] = this.getResources().getStringArray(R.array.type);
        for (int i = 0; i < immoralActivities.length; i++) {
            typeList.add(immoralActivities[i]);
        }
        ArrayAdapter<String> data = new ArrayAdapter<String>(this, R.layout.spinner_item,
                typeList);
        data.setDropDownViewResource(R.layout.spinner_dropdown_item);
        type.setAdapter(data);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activityType = (String) type.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adb.setView(view)
                .autoDismiss(false)
                .setCancelable(false)
                .setTitle("Adauga pin")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        disableOkButton(dialog);
                        activityDesc = desc.getText().toString();
                        if(activityType.equals(immoralActivities[0])){
                            Toast.makeText(MainActivity.this,"Nu ati selectat cazul intalnit",Toast.LENGTH_LONG).show();
                            enableOkButton(dialog);
                            return;
                        }
                        Log.d(TAG, activityDesc);
                        Log.d(TAG, desc.getText().toString());
                        PinDbHelper.addPinToDatabase(MainActivity.this, SessionManager.getInstance().getId(), activityType, activityDesc, latLng.latitude, latLng.longitude);
                        pin.lat = latLng.latitude;
                        pin.lng = latLng.longitude;
                        pin.description = activityDesc;
                        pin.type = activityType;
                        pin.userId = SessionManager.getInstance().getId();
                        mapUtils.addToCluster(pin);
                        mapUtils.recluster();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = adb.create();
        d.findViewById(R.id.titleFrame).setBackgroundColor(this.getResources().getColor(R.color.dark_blue));
        ((TextView) d.findViewById(R.id.title)).setTextColor(Color.WHITE);
        d.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);
                }
            }
        });
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        int searchClose = android.support.v7.appcompat.R.id.search_close_btn;
        ImageView searchImg = (ImageView) searchView.findViewById(searchImgId);
        ImageView closeImg = (ImageView)searchView.findViewById(searchClose);
        searchImg.setImageResource(R.mipmap.ic_search);
        closeImg.setImageResource(R.mipmap.ic_clear);

        changeSearchViewTextColor(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder((new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog)));
            builder.setCancelable(true)
                    .setMessage("Logout?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            SessionManager.getInstance().logout();
                            finish();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog d = builder.create();
            ((TextView) d.findViewById(R.id.title)).setTextColor(Color.WHITE);
            d.show();

            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "in handle intent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, query);
            final Intent i = new Intent(this, SearchActivity.class);
            i.putExtra(Constants.SEARCH_TEXT, query);
            startActivityForResult(i, 100);
            Log.d(TAG, "in handle intent startActivity");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Log.d(TAG, data.toString());
            double returnLatitude = data.getDoubleExtra("returnLatitude", 0);
            double returnLongitude = data.getDoubleExtra("returnLongitude", 0);
            Marker marker = mMap.addMarker(new MarkerOptions().draggable(true)
                    .position(new LatLng(returnLatitude, returnLongitude)));
            marker.setSnippet("Navigheaza pana la aceasta pozitie");
            marker.setTitle("NAVIGARE");
            Log.d(TAG, "Position on return: " + new LatLng(returnLatitude, returnLongitude));

            if (requestCode == 100) {
                double returnLatNE = data.getDoubleExtra("returnLatNE", 0);
                double returnLngNE = data.getDoubleExtra("returnLngNE", 0);
                double returnLatSV = data.getDoubleExtra("returnLatSV", 0);
                double returnLngSV = data.getDoubleExtra("returnLngSV", 0);
                if (returnLatNE == returnLatSV && returnLngNE == returnLngSV)
                    mapUtils.zoomToLocation(new LatLng(returnLatitude, returnLongitude), 15);
                else
                    mapUtils.zoomToBoundingBox(
                            new LatLngBounds(new LatLng(returnLatSV, returnLngSV),
                                    new LatLng(returnLatNE, returnLngNE)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                ((TextView)view).setHintTextColor(getResources().getColor(R.color.immoral_search_grey));
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    public static void disableOkButton(DialogInterface dialog){
        final View ok = ((MaterialDialog) dialog).getActionButton(DialogAction.POSITIVE);
        ok.setEnabled(false);
    }

    public static void enableOkButton(DialogInterface dialog){
        final View ok = ((MaterialDialog) dialog).getActionButton(DialogAction.POSITIVE);
        ok.setEnabled(true);
    }

}

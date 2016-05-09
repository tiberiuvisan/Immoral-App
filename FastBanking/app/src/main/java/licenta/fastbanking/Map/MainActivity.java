package licenta.fastbanking.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import licenta.fastbanking.Activities.VideoActivity;
import licenta.fastbanking.Managers.SessionManager;
import licenta.fastbanking.Objects.Bank;
import licenta.fastbanking.Objects.CurrentUser;
import licenta.fastbanking.R;
import licenta.fastbanking.Utils.BankDbHelper;
import licenta.fastbanking.Utils.Constants;
import licenta.fastbanking.Utils.DialogBuilder;
import licenta.fastbanking.Utils.UserDbHelper;
import licenta.fastbanking.Utils.YoutubeExtractor;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private GoogleMap mMap;
    private MapUtils mapUtils;
    private LatLng latLng;
    private ArrayList<Bank> banks;
    SharedPreferences sp;


    /*CURRENT USER*/
    private CurrentUser currentUser;

    /*VIEWS*/
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    /*DRAWER VIEWS*/
    private TextView headerUsername;
    private TextView headerIsAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        getCurrentUser();
        initialiseUI();
        setupMapIfNeeded();


        banks = BankDbHelper.getBanksFromDatabase(this);

        //check for location service
        Log.d(TAG, "initLocation - location null");
        if (!MapUtils.checkLocationEnabled(this))
            DialogBuilder.showDialogEnableLocation(this);

    }

  /*  private void getCurrentUser() {
        currentUser = new CurrentUser();

        currentUser.username = getIntent().getStringExtra("username");
        currentUser.admin = UserDbHelper.checkAdmin(this, UserDbHelper.getId(this, currentUser.username));

        Log.d(TAG, "Current user: " + currentUser.toString());

    }*/

    private void getCurrentUser() {
        currentUser = new CurrentUser();

        currentUser.username = "TEST ";
        currentUser.admin = true;

        Log.d(TAG, "Current user: " + currentUser.toString());

    }


    //se fac legaturile dintre elementele din layout-uri si restul codului
    private void initialiseUI() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name) {


            @SuppressLint("NewApi")
            public void onDrawerClosed(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                    invalidateOptionsMenu();
            }

            @SuppressLint("NewApi")
            public void onDrawerOpened(View drawerView) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                    invalidateOptionsMenu();
            }

            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);
                // fix bug on android 2.3
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    drawerLayout.bringChildToFront(drawerView);
                    drawerLayout.requestLayout();
                }

            }
        };
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }

        });

        drawerLayout.setDrawerListener(drawerToggle);



        /*Elementele din drawer*/
        headerUsername = (TextView) findViewById(R.id.header_username);
        headerIsAdmin = (TextView) findViewById(R.id.header_admin);
        setHeaderValues();


    }

    private void setHeaderValues() {
        if (currentUser != null) {
            headerUsername.setText(headerUsername.getText().toString() + currentUser.username);
            if (currentUser.admin) {
                headerIsAdmin.setText(getString(R.string.yes));
                headerIsAdmin.setTextColor(getResources().getColor(R.color.fast_green));
            } else {
                headerIsAdmin.setText(getString(R.string.no));
                headerIsAdmin.setTextColor(getResources().getColor(R.color.fast_red));
            }

        }

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
                for (Bank bank : banks) {
                    mapUtils.addToCluster(bank);
                    mapUtils.recluster();
                }
                mapUtils.recluster();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (UserDbHelper.checkAdmin(MainActivity.this, SessionManager.getInstance().getId())) {
                    addMarker(latLng);
                } else {
                    return;
                }

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


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog));
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_bank, null);

        final EditText bank_name = (EditText) view.findViewById(R.id.bank_name);
        final EditText counters_number = (EditText) view.findViewById(R.id.bank_counters_number);
        final EditText waiting_time = (EditText) view.findViewById(R.id.bank_wait_time);
        final EditText total_people = (EditText) view.findViewById(R.id.bank_total_people);
        final Bank bank = new Bank();

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogBuilder.disableOkButton(dialog);
                        String bankName = bank_name.getText().toString();
                        int countersNumber = 0;
                        int waitingTime = 0;
                        int totalPeople = 0;

                        if (!counters_number.getText().toString().equals("")) {
                            countersNumber = Integer.parseInt(counters_number.getText().toString());
                        }
                        if (!waiting_time.getText().toString().equals("")) {
                            waitingTime = Integer.parseInt(waiting_time.getText().toString());
                        }
                        if (!total_people.getText().toString().equals("")) {
                            totalPeople = Integer.parseInt(total_people.getText().toString());
                        }
                        BankDbHelper.addBankToDatabase(MainActivity.this, bankName, countersNumber, waitingTime, totalPeople, latLng.latitude, latLng.longitude);

                        bank.lat = latLng.latitude;
                        bank.lng = latLng.longitude;
                        bank.name = bankName;
                        bank.countersNumber = countersNumber;
                        bank.waitTime = waitingTime;
                        bank.totalPeople = totalPeople;
                        mapUtils.addToCluster(bank);
                        mapUtils.recluster();
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }




}

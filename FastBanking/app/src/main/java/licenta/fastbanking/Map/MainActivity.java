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
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Random;

import licenta.fastbanking.Activities.LoginActivity;
import licenta.fastbanking.Managers.SessionManager;
import licenta.fastbanking.Objects.Bank;
import licenta.fastbanking.Objects.CurrentUser;
import licenta.fastbanking.Objects.Directions;
import licenta.fastbanking.R;
import licenta.fastbanking.Utils.BankDbHelper;
import licenta.fastbanking.Utils.Constants;
import licenta.fastbanking.Utils.DialogBuilder;
import licenta.fastbanking.Utils.NetworkUtils;
import licenta.fastbanking.Utils.OnCompleteListener;
import licenta.fastbanking.Utils.UserDbHelper;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int COUNTDOWN_TIMER_SECONDS = 50;
    private static final int REMOVE_COUNTDOWN_TIMER_SECCONDS = 60 * 7;

    private GoogleMap mMap;
    private MapUtils mapUtils;
    private LatLng latLng;
    private ArrayList<Bank> banks;
    SharedPreferences sp;


    private String transportMode;

    /*DIRECTIONS INFO*/
    public Directions currentDirections;


    /*CURRENT USER*/
    private CurrentUser currentUser;

    /*VIEWS*/
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Button btnClosestPin;
    private LinearLayout bottomBar;
    private Button btnStopNavigation;
    private TextView timeRemaining;
    private TextView distanceRemaining;
    private TextView updateInfoIn;


    /*DRAWER VIEWS*/
    private TextView headerUsername;
    private TextView headerIsAdmin;
    private TextView logout;
    private RadioButton walkingMode;
    private RadioButton drivingMode;
    private RadioButton transitMode;
    private RadioGroup transitRadioGroup;

    public int navigateBankId;
    private boolean isNavigating = false;

    /*Timer pentru adaugarea de oameni la ghiseu*/

    private CountDownTimer addPeopleCDT = new CountDownTimer(COUNTDOWN_TIMER_SECONDS * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            //nu face nimic
        }

        @Override
        public void onFinish() {
            int max = banks.size() - 1;
            Random peopleRandom = new Random();
            int randomID = peopleRandom.nextInt(max);
            banks.get(randomID).totalPeople++;
            Log.d("TIMER", "Added people to bank with id: " + randomID);

            addPeopleCDT.start();

        }
    };

    private CountDownTimer removePeopleCDT = new CountDownTimer(REMOVE_COUNTDOWN_TIMER_SECCONDS * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

            for (Bank bank : banks) {
                bank.totalPeople = bank.totalPeople - bank.countersNumber;
                if (bank.totalPeople < 0)
                    bank.totalPeople = 0;

                Log.d("TIMER", "Removed " + bank.countersNumber + " from bank with id: " + bank.id);
                Log.d("TIMER", "People remaining: " + bank.totalPeople);
            }
            removePeopleCDT.start();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        getCurrentUser();
        banks = BankDbHelper.getBanksFromDatabase(this);
        for (Bank bank : banks) {
            bank.id--;
        }
        initialiseUI();
        setupMapIfNeeded();

        //check for location service
        Log.d(TAG, "initLocation - location null");
        if (!MapUtils.checkLocationEnabled(this))
            DialogBuilder.showDialogEnableLocation(this);


    }


    private void getCurrentUser() {
        currentUser = new CurrentUser();

        currentUser.username = UserDbHelper.getUser(this, SessionManager.getInstance().getId());
        currentUser.admin = UserDbHelper.checkAdmin(this, UserDbHelper.getId(this,currentUser.username));

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
        logout = (TextView)findViewById(R.id.logout);

        setHeaderValues();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                SessionManager.getInstance().logout();
                finish();
            }
        });

        /*elementele din bara de jos*/

        btnClosestPin = (Button) findViewById(R.id.btn_closest_pin);

        bottomBar = (LinearLayout) findViewById(R.id.nav_bar);
        btnStopNavigation = (Button) findViewById(R.id.btn_stop_navigation);
        timeRemaining = (TextView) findViewById(R.id.tv_time_remaining);
        distanceRemaining = (TextView) findViewById(R.id.tv_distance_remaining);
        updateInfoIn = (TextView) findViewById(R.id.tv_update_info);

        btnClosestPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banks != null) {
                    if (banks.size() != 0) {
                        mapUtils.createDialogForNavigation(banks.get(getClosestBankPosition(banks)));

                    }
                }
            }
        });

        btnStopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNavViews();
                loadMapItems();
                countDownTimer.cancel();

            }
        });


        /*radio buttons din drawer*/
        walkingMode = (RadioButton) findViewById(R.id.radio_button_w);
        drivingMode = (RadioButton) findViewById(R.id.radio_button_d);
        transitMode = (RadioButton) findViewById(R.id.radio_button_t);
        transitRadioGroup = (RadioGroup) findViewById(R.id.traseu_radio_group);

        transitRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isNavigating) {
                    drawerLayout.closeDrawers();
                    countDownTimer.cancel();
                    getUpdatedInfo();
                }
            }
        });

    }

    public void enableNavViews() {
        isNavigating = true;
        btnClosestPin.setVisibility(View.GONE);
        bottomBar.setVisibility(View.VISIBLE);
        updateNavView(currentDirections.routes.get(0).formattedLegs.get(0).duration.text,
                currentDirections.routes.get(0).formattedLegs.get(0).distance.text);
    }

    public void disableNavViews() {
        if (mapUtils.trackPolyline != null) {
            mapUtils.trackPolyline.remove();
        }
        isNavigating = false;
        btnClosestPin.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.GONE);
    }

    private void updateNavView(String timeRemainingString, String distanceRemainingString) {
        timeRemaining.setText(String.format(getResources().getString(R.string.aproximate_time_remaining), timeRemainingString));
        distanceRemaining.setText(String.format(getResources().getString(R.string.remaining_distance), distanceRemainingString));


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
        if (banks != null) {
            addPeopleCDT.start();
            removePeopleCDT.start();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (GPSLocation.getLastInstance() != null)
            GPSLocation.getLastInstance().disconnect();
        disableNavViews();
        super.onPause();

        countDownTimer.cancel();
        addPeopleCDT.cancel();
        removePeopleCDT.cancel();

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
                loadMapItems();
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


    public void calculateDistanceForAllBanks(ArrayList<Bank> banks) {

        /*calculeaza locatia a 2 puncte in metri*/
        Location myLocation = new Location("myLoc");
        myLocation.setLatitude(mapUtils.getLastLocation().latitude);
        myLocation.setLongitude(mapUtils.getLastLocation().longitude);
        for (Bank bank : banks) {
            Location bankLocation = new Location("bank");
            bankLocation.setLatitude(bank.lat);
            bankLocation.setLongitude(bank.lng);
            bank.distance = myLocation.distanceTo(bankLocation);
            Log.d(TAG, "bank location: " + String.valueOf(bank.distance));

        }

    }

    public int getClosestBankPosition(ArrayList<Bank> banks) {
        int closestBankPosition = 0;
        if (banks.size() == 0) {
            return -1;
        }
        Bank closestBank = banks.get(0);
        for (int i = 0; i < banks.size(); i++) {
            if (banks.get(i).distance < closestBank.distance) {
                closestBankPosition = i;
            }

        }
        return closestBankPosition;
    }


    private void loadMapItems() {
        LatLng centru = new LatLng(45.40, 25.10);
        if (mapUtils.getLastLocation() != new LatLng(0, 0) || mapUtils != null) {
            centru = mapUtils.getLastLocation();
        }
        mapUtils.setCenter(centru);
        getLocation();
        for (Bank bank : banks) {
            mapUtils.addToCluster(bank);
            mapUtils.recluster();
        }
        calculateDistanceForAllBanks(banks);
        mapUtils.recluster();

        addPeopleCDT.start();

    }

    public String getTransportMode() {
        if (walkingMode.isChecked()) {
            transportMode = "walking";
        } else if (transitMode.isChecked()) {
            transportMode = "transit";
        } else if (drivingMode.isChecked()) {
            transportMode = "driving";
        }


        return transportMode;
    }


    public CountDownTimer countDownTimer = new CountDownTimer(COUNTDOWN_TIMER_SECONDS * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            updateInfoIn.setText(String.format(getResources().getString(R.string.update_info), String.valueOf(millisUntilFinished / 1000)));
        }

        @Override
        public void onFinish() {
            /*reupdate info */
            DialogBuilder.createProgressDialog(MainActivity.this);
            getUpdatedInfo();

        }
    };


    private void getUpdatedInfo() {
        NetworkUtils.getNetworkUtils(MainActivity.this).getDirections(mapUtils.getLastLocation(),
                banks.get(navigateBankId).getPosition(),
                getTransportMode(), new OnCompleteListener() {
                    @Override
                    public void onComplete(boolean status, Object data) {
                        DialogBuilder.dismissProgressDialog();
                            /*actualizeaza datele si reporneste cronometrul*/
                        if (status) {
                            currentDirections = (Directions) data;
                            updateNavView(currentDirections.routes.get(0).formattedLegs.get(0).duration.text,
                                    currentDirections.routes.get(0).formattedLegs.get(0).distance.text);
                            countDownTimer.start();
                        }
                    }
                });
    }


}

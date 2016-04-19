package ro.conceptapps.immoralapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.adapters.PlacesAdapter;
import ro.conceptapps.immoralapp.object.Data;
import ro.conceptapps.immoralapp.utils.Constants;
import ro.conceptapps.immoralapp.utils.NetworkUtils;

public class SearchActivity extends ActionBarActivity {
    private static final String TAG = "SearchActivity";
    private ListView lv;
    private String searchedText;
    private PlacesAdapter placesAdapter;

    //ELEMENTELE VIEW-ULUI SI TOATE ON CLICK-URILE SUNT GESTIONATE DE ADAPTORUL DE LISTA PlacesAdapter.java

    //API BROADCAST RECEIVERS
    //se apeleaza DOAR ATUNCI cand va primi broadcast-ul din NetworkUtils, functia de getPlaces();

    //va adauga elementele primite de pe server in lista creeata pe onCreate
    //de asemenea elementele din lista sunt sortate dupa distanta fata de locatia utilizatorului;
    private BroadcastReceiver getPlacesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra("error", true)) {
                for (int i = 0; i < Data.places.size(); i++) {
                    Log.d(TAG, "place is for " + i + " " + Data.places.get(i).getName());
                }
                placesAdapter = new PlacesAdapter(SearchActivity.this, Data.places);
                lv.setAdapter(placesAdapter);
                placesAdapter.sort();
            }
        }
    };

    //se creaza view-ul, elementul principal fiind ListView, care va contine lista de elemente.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        registerReceiver(getPlacesReceiver, new IntentFilter(Constants.ADD_PLACES_OPERATION));
        if (!searchedText.equals("") || searchedText != null)
            NetworkUtils.getNetworkUtils(this).getSearchedPlaces(searchedText);

    }


    private void initUI() {
        setContentView(R.layout.activity_search);
        lv = (ListView) findViewById(R.id.listViewSearch);
        lv.setDivider(null);
        lv.setDividerHeight(10);
        searchedText = getIntent().getStringExtra(Constants.SEARCH_TEXT);
        initToolbar();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(getPlacesReceiver);
        super.onDestroy();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        this.finish();
        super.onBackPressed();
    }
}

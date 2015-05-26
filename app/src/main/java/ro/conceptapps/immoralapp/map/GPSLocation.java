package ro.conceptapps.immoralapp.map;


        import android.content.Context;
        import android.content.Intent;
        import android.location.Location;
        import android.os.Bundle;
        import android.os.Handler;
        import android.util.Log;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;

public class GPSLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Class log tag
    private String TAG = "GPSLocation";
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 5;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    int counter = 0;

    LocationRequest locationRequest;
    LocationResult locationResult;

    Context ctx;
    public Location lastLocation = null;

    boolean done = false;
    //Checks if the location Runnable has started
    //if true, does not run the Handler anymore
    public boolean locationChanged = false;

    static GPSLocation lastInstance = null;
    private GoogleApiClient googleApiClient;

    public GPSLocation(Context ctx, LocationResult locationResult) {
        this.ctx = ctx;
        this.locationResult = locationResult;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        googleApiClient = new GoogleApiClient.Builder(ctx)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        connect();
        GPSLocation.lastInstance = this;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected()");

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        lastLocation = location;

        if (!done && location != null) {
            Log.d(TAG, "Have location");
        } else {
            Log.d(TAG, "Location is null");
        }

        locationResult.gotLocation(location);
        done = true;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }

    @Override
    public void onLocationChanged(final Location location) {

        if (lastLocation != null) {
                locationChanged = true;
                Log.d(TAG, "onLocationChanged() lat:" + location.getLatitude() + ", lng:" + location.getLongitude());
        }
        locationResult.gotLocation(location);
        lastLocation = location;
    }


    public static GPSLocation getLastInstance() {
        return lastInstance;
    }

    public void connect() {
        Log.d(TAG, "Location connect");
        googleApiClient.connect();
    }

    public void disconnect() {
        Log.d(TAG, "Location disconnect");
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Connection failed");
        locationResult.gotLocation(null);
        done = true;
    }


}
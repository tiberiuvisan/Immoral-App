package licenta.fastbanking.Map;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Network;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;

import licenta.fastbanking.Objects.Bank;
import licenta.fastbanking.Utils.Constants;
import licenta.fastbanking.Utils.DialogBuilder;
import licenta.fastbanking.Utils.NetworkUtils;
import licenta.fastbanking.Utils.OnCompleteListener;

public class MapUtils {


    private static final String TAG = "MapUtils";
    private GoogleMap map;
    private Context ctx;
    private SharedPreferences sp;
    private ClusterManager<Bank> mClusterManager;
    Polyline trackPolyline;


    public MapUtils(Context ctx, GoogleMap map) {
        this.ctx = ctx;
        this.map = map;
        sp = ctx.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);

    }

    //functii de zoom
    public void zoomToLocation(LatLng latLng, int zoomLevel) {

        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(latLng).zoom(zoomLevel).build()), 1000, null);
    }

    public void setCenter(LatLng point) {
        CameraPosition cameraPosition = new CameraPosition(point, 5.5f, 0, 0);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    //functie de setare harta si setarea cluster manager
    public void setUpMap() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.setMyLocationEnabled(true);
        setupClusterManager();
        mClusterManager.setRenderer(new PinRenderer(ctx, map, mClusterManager));
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterManager.onCameraChange(cameraPosition);

            }
        });
        map.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Bank>() {
            @Override
            public void onClusterItemInfoWindowClick(final Bank bank) {
                DialogBuilder.createProgressDialog(ctx);
                NetworkUtils.getNetworkUtils(ctx).getDirections(getLastLocation(),
                        bank.getPosition(),
                        "walking",
                        new OnCompleteListener() {
                            @Override
                            public void onComplete(boolean status, Object data) {
                                if (status) {
                                    DialogBuilder.dismissProgressDialog();
                                    DialogBuilder.DialogBankInfo(ctx, bank);
                                }

                            }
                        });
            }
        });


        /*
        * actiunea ce se va petrece in cazul in care se va da click pe infowindow-ul unui pin
        * daca tipul pinului este "NAVIGHEAZA", sa creeze traseul
        * altfel sa afiseze informatii despre eveniment
        * */
        /*mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Bank>() {
            @Override
            public void onClusterItemInfoWindowClick(Bank pin) {
                if (pin.type.equals("Navigheaza")) {
                    NetworkUtils.getNetworkUtils(ctx).getDirections(pin.getPosition(),
                            new LatLng(GPSLocation.getLastInstance().lastLocation.getLatitude(),
                                    GPSLocation.getLastInstance().lastLocation.getLongitude()));
                } else infoDialog(pin);
            }
        });*/

        map.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Bank>() {
            @Override
            public boolean onClusterClick(Cluster<Bank> cluster) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), map.getCameraPosition().zoom + 1), 300, null);
                return true;
            }
        });


    }


    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(ctx, map);
        mClusterManager.setAlgorithm(new GridBasedAlgorithm<Bank>());
    }



    /*public void infoDialog(Bank pin) {
        AlertDialogWrapper.Builder adb = new AlertDialogWrapper.Builder(new ContextThemeWrapper(ctx, R.style.Theme_AppCompat_Light_Dialog));
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_infowindow, null);

        final TextView user = (TextView) view.findViewById(R.id.user);
        final TextView desc = (TextView) view.findViewById(R.id.description);
        final TextView phone = (TextView) view.findViewById(R.id.phone);

        user.setText(UserDbHelper.getUserName(ctx, pin.userId));
        desc.setText(pin.description);
        if (UserDbHelper.getPhone(ctx, pin.userId).length() != 0) {
            phone.setText(UserDbHelper.getPhone(ctx, pin.userId));
            makeSmsLink(phone, pin);
        } else phone.setText("Numarul de telefon nu este disponibil");
        Log.d(TAG, "phone number is: " + phone.getText().toString().length());
        adb.setTitle(pin.type)
                .setView(view)
                .autoDismiss(false)
                .setCancelable(false)
                .setNegativeButton("Inchide", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        Dialog d = adb.create();
        d.findViewById(R.id.titleFrame).setBackgroundColor(ctx.getResources().getColor(R.color.dark_blue));
        ((TextView) d.findViewById(R.id.title)).setTextColor(Color.WHITE);
        d.show();


    }*/

    //functia care se apeleaza pentru userii care au numarul de telefon introdus
    //la click pe numar, utilizatorul va fi trimis in aplicatia de mesaje cu mesaj predefinit

    /*public void makeSmsLink(final TextView phone, final Bank pin) {
        phone.setTextColor(ctx.getResources().getColor(R.color.nav_drawer_color));
        phone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        phone.setClickable(true);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + phone.getText().toString());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Buna ziua! Imi puteti da mai multe detalii legate de evenimentul " + pin.type + "? Multumesc!");
                ctx.startActivity(intent);
            }
        });

    }*/

    public void addToCluster(Bank p) {
        mClusterManager.addItem(p);
    }

   /* public void addPolyline(CustomPolyline customPolyline) {
        if (trackPolyline != null) trackPolyline.remove();
        trackPolyline = map.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode(customPolyline.getEncodedPolyline()))
                .width(convertDpToPixel(5, ctx)).color(ctx.getResources().getColor(R.color.action_bar_color)));


        Log.d(TAG, "decoded poly: " + PolyUtil.decode(customPolyline.getEncodedPolyline()));
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(customPolyline.getLatLngBounds(), (int) convertDpToPixel(30, ctx)));
    }*/

    public void removeFromCluster(Bank bank) {
        mClusterManager.removeItem(bank);
    }

    public void recluster() {
        mClusterManager.cluster();
    }

    public static float distanceBetween(LatLng from, LatLng to) {
        float result[] = new float[2];
        Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, result);
        return result[0];
    }

    public void zoomToBoundingBox(LatLngBounds latLngBounds) {
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
        map.animateCamera(cu);
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }


    public static boolean checkLocationEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public LatLng getLastLocation() {
        float lastLat = sp.getFloat(Constants.LOCATION_LAST_LAT, 0);
        float lastLng = sp.getFloat(Constants.LOCATION_LAST_LNG, 0);
        return new LatLng(lastLat, lastLng);
    }
}

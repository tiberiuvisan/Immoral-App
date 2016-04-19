package ro.conceptapps.immoralapp.map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.object.CustomPolyline;
import ro.conceptapps.immoralapp.utils.Constants;
import ro.conceptapps.immoralapp.utils.NetworkUtils;
import ro.conceptapps.immoralapp.utils.Pin;
import ro.conceptapps.immoralapp.utils.UserDbHelper;

public class MapUtils {


    private static final String TAG = "MapUtils";
    private GoogleMap map;
    private Context ctx;
    private SharedPreferences sp;
    private ClusterManager<Pin> mClusterManager;
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


        /*
        * actiunea ce se va petrece in cazul in care se va da click pe infowindow-ul unui pin
        * daca tipul pinului este "NAVIGHEAZA", sa creeze traseul
        * altfel sa afiseze informatii despre eveniment
        * */
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Pin>() {
            @Override
            public void onClusterItemInfoWindowClick(Pin pin) {
                if (pin.type.equals("Navigheaza")) {
                    NetworkUtils.getNetworkUtils(ctx).getDirections(pin.getPosition(),
                            new LatLng(GPSLocation.getLastInstance().lastLocation.getLatitude(),
                                    GPSLocation.getLastInstance().lastLocation.getLongitude()));
                } else infoDialog(pin);
            }
        });

        map.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Pin>() {
            @Override
            public boolean onClusterClick(Cluster<Pin> cluster) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), map.getCameraPosition().zoom + 1), 300, null);
                return true;
            }
        });

    }


    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(ctx, map);
        mClusterManager.setAlgorithm(new GridBasedAlgorithm<Pin>());
    }



    public void infoDialog(Pin pin) {
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


    }

    //functia care se apeleaza pentru userii care au numarul de telefon introdus
    //la click pe numar, utilizatorul va fi trimis in aplicatia de mesaje cu mesaj predefinit

    public void makeSmsLink(final TextView phone, final Pin pin) {
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

    }

    public void addToCluster(Pin p) {
        mClusterManager.addItem(p);
    }

    public void addPolyline(CustomPolyline customPolyline) {
        if (trackPolyline != null) trackPolyline.remove();
        trackPolyline = map.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode(customPolyline.getEncodedPolyline()))
                .width(convertDpToPixel(5, ctx)).color(ctx.getResources().getColor(R.color.action_bar_color)));


        Log.d(TAG, "decoded poly: " + PolyUtil.decode(customPolyline.getEncodedPolyline()));
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(customPolyline.getLatLngBounds(), (int) convertDpToPixel(30, ctx)));
    }

    public void removeFromCluster(Pin pin) {
        mClusterManager.removeItem(pin);
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
}

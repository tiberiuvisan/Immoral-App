package ro.conceptapps.immoralapp.map;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;

import ro.conceptapps.immoralapp.utils.Pin;

/**
 * Created by Tiberiu Visan on 5/12/2015.
 * Project: Immoral-App
 */
public class MapUtils {


    private static final String TAG = "MapUtils";
    private GoogleMap map;
    private Context ctx;
    private SharedPreferences sp;
    private ClusterManager<Pin> mClusterManager;


    public MapUtils(Context ctx, GoogleMap map) {
        this.ctx = ctx;
        this.map = map;
        sp = ctx.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);

    }


    public void zoomToLocation(LatLng latLng, int zoomLevel) {

        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(latLng).zoom(zoomLevel).build()), 1000, null);
    }

    public void setCenter(LatLng point) {
        CameraPosition cameraPosition = new CameraPosition(point, 5.5f, 0, 0);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void setUpMap() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        setupClusterManager();
        mClusterManager.setRenderer(new PinRenderer(ctx, map, mClusterManager));
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

            }
        });
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterManager.onCameraChange(cameraPosition);

            }
        });

        map.setOnInfoWindowClickListener(mClusterManager);

        map.setOnMarkerClickListener(mClusterManager);

        // mClusterManager.setOnClusterItemClickListener();

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


    public void addToCluster(Pin p) {
        mClusterManager.addItem(p);
    }

    public void removeFromCluster(Pin pin) {
        mClusterManager.removeItem(pin);
    }

    public void recluster() {
        mClusterManager.cluster();
    }

}

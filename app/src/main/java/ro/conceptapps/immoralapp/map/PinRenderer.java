package ro.conceptapps.immoralapp.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ro.conceptapps.immoralapp.utils.Pin;

public class PinRenderer extends DefaultClusterRenderer<Pin> {

    Context context;

    public PinRenderer(Context context, GoogleMap map, ClusterManager<Pin> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(Pin item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        markerOptions.title(item.type);
        markerOptions.snippet(item.description);
        markerOptions.draggable(false);
    }
}

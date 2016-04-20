package licenta.fastbanking.Map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import licenta.fastbanking.Objects.Pin;
import licenta.fastbanking.R;

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
        markerOptions.snippet("apasati pentru detalii");
        markerOptions.draggable(false);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin));
    }


}

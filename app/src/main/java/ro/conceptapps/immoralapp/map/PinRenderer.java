package ro.conceptapps.immoralapp.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ro.conceptapps.immoralapp.R;
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
        if (!item.type.equals("Navigheaza"))
            markerOptions.snippet("apasati pentru detalii");
        else markerOptions.snippet("apasati pentru a genera traseu");
        markerOptions.draggable(false);
        setPOIIconByType(item,markerOptions);
    }

    private void setPOIIconByType(Pin item, MarkerOptions markerOptions) {
        String[] types = context.getResources().getStringArray(R.array.type);
        if(item.type.equals(types[1])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_light_green));
        }else if(item.type.equals(types[2])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_dark_green));
        }else if(item.type.equals(types[3])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_orange));
        }else if(item.type.equals(types[4])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_yellow));
        }else if(item.type.equals(types[5])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_light_blue));
        }else if(item.type.equals(types[6])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_blue));
        }else if(item.type.equals(types[7])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_magenta));
        }else if(item.type.equals(types[8])){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_purple));
        }else markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_red));

    }
}

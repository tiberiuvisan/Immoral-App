package licenta.fastbanking.Map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import licenta.fastbanking.Objects.Bank;
import licenta.fastbanking.R;

public class PinRenderer extends DefaultClusterRenderer<Bank> {

    Context context;

    public PinRenderer(Context context, GoogleMap map, ClusterManager<Bank> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(Bank item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        markerOptions.title(item.name);
        markerOptions.snippet("apasati pentru detalii");
        markerOptions.draggable(false);
        changePin(item, markerOptions);

    }


    public void changePin(Bank bank, MarkerOptions markerOptions){
        if(bank.calculateWaitTime()<5){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_green));
        }else if(bank.calculateWaitTime()<15){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_orange));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_red));
        }

    }


}

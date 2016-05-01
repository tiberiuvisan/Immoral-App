package licenta.fastbanking.Objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by Andreea on 4/20/2016.
 */
public class Bank implements Serializable, ClusterItem {



    public int id;
    public String name;
    public int countersNumber;
    public int waitTime;
    public int totalPeople;
    public double lat;
    public double lng;

    public int calculateWaitTime(){
        int totalWait=0;

        totalWait = (this.waitTime*this.totalPeople)/this.countersNumber;

        return totalWait;
    }


    @Override
    public String toString() {
        return id + ", "
                +name + ", "
                +countersNumber +", "
                +waitTime +", "
                +totalPeople+", "
                +lat +", "
                +lng;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }
}


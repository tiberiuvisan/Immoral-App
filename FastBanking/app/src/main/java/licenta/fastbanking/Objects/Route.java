package licenta.fastbanking.Objects;


import java.util.ArrayList;

/**
 * Created by Tiberiu Visan on 5/8/2016.
 * Project: FastBanking
 */
public class Route {

    public Bounds bounds;

    public OverviewPolyline overview_polyline;

    public String summary;

    public ArrayList<Leg> legs;

    public ArrayList<Leg> formattedLegs;

}

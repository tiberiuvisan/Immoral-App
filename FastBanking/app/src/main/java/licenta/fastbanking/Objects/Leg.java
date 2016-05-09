package licenta.fastbanking.Objects;

/**
 * Created by Tiberiu Visan on 5/8/2016.
 * Project: FastBanking
 */
public class Leg {


    public RouteInfo distance;
    public RouteInfo duration;

    public String end_address;
    public Coordinate end_location;

    public String start_address;
    public Coordinate start_location;

    //+more


    @Override
    public String toString() {
        return "Leg{" +
                "distance=" + distance +
                ", duration=" + duration +
                ", end_address='" + end_address + '\'' +
                ", end_location=" + end_location +
                ", start_address='" + start_address + '\'' +
                ", start_location=" + start_location +
                '}';
    }
}

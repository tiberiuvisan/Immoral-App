package ro.conceptapps.immoralapp.utils;

/**
 * Created by andreea on 5/13/2015.
 */
public class Pin {

    public int id;
    public int userId;
    public String type;
    public String description;
    public float lat;
    public float lng;


    @Override
    public String toString() {
        return id + ", "
                +userId + ", "
                +type +", "
                +description +", "
                +lat +", "
                +lng;
    }
}

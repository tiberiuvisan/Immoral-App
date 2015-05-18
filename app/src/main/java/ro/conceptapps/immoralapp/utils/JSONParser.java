package ro.conceptapps.immoralapp.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ro.conceptapps.immoralapp.object.Places;

public class JSONParser {

    private static final String TAG = "JSONParser";

    public static ArrayList<Places> parsePlaces(String response) throws JSONException {
        ArrayList<Places> results = new ArrayList<>();

        JSONArray array = new JSONArray(response);
        Log.d(TAG,"response on places: " + response);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
            String place_id = obj.getString("place_id");
            String type = obj.getString("type");
            String class_type = obj.getString("class");
            double importance = obj.getDouble("importance");
            String lat = obj.getString("lat");
            String lng = obj.getString("lon");
            String name = obj.getString("display_name");
            JSONArray boundingBox = obj.getJSONArray("boundingbox");

            //add element in Place
            Places place = new Places();
            place.setClass_type(class_type);
            place.setPlace_id(Long.parseLong(place_id));
            place.setType(type);
            place.setName(name);
            place.setImportance(importance);
            place.setLat(Double.parseDouble(lat));
            place.setLng(Double.parseDouble(lng));
            place.setLatSV(Double.parseDouble(boundingBox.getString(0)));
            place.setLatNE(Double.parseDouble(boundingBox.getString(1)));
            place.setLngSV(Double.parseDouble(boundingBox.getString(2)));
            place.setLngNE(Double.parseDouble(boundingBox.getString(3)));
            Log.d(TAG,"response latLngBounds" + place.getLatLngBounds());
            results.add(place);
        }
        return results;
    }
}

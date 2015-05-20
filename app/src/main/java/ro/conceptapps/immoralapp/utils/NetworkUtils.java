package ro.conceptapps.immoralapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ro.conceptapps.immoralapp.object.Data;

public class NetworkUtils {

    public static final String TAG = "NetworkUtils";
    private RequestQueue requestQueue;
    private static NetworkUtils instance;
    private Context ctx;
    private final int LIST_LIMIT = 25;


    public static NetworkUtils getNetworkUtils(Context ctx) {
        if (instance == null) {
            instance = new NetworkUtils(ctx);
        }
        return instance;
    }

    private NetworkUtils(Context ctx) {
        this.ctx = ctx;
        requestQueue = Volley.newRequestQueue(ctx);
    }


    public void getSearchedPlaces(String searchedText) {
        StringRequest request = new StringRequest(Request.Method.GET, buildPath(searchedText), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "get Searched places response : " + response);
                Intent i = new Intent(Constants.ADD_PLACES_OPERATION);
                try {
                    Data.places = JSONParser.parsePlaces(response);
                    i.putExtra("error", false);
                    ctx.sendBroadcast(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    i.putExtra("error", true);
                    ctx.sendBroadcast(i);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent i = new Intent(Constants.ADD_PLACES_OPERATION);
                i.putExtra("error", true);
                ctx.sendBroadcast(i);
            }
        });
        requestQueue.add(request);
    }

    public void getDirections(LatLng from, LatLng to) {
        StringRequest request = new StringRequest(Request.Method.GET, buildLocationPath(from, to)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "get Direction response : " + response);
                Intent i = new Intent(Constants.ADD_DIRECTIONS_OPERATION);
                try {
                    Data.polylines = JSONParser.parseDirections(response);
                    i.putExtra("error", false);
                    ctx.sendBroadcast(i);
                } catch (JSONException e) {
                    i.putExtra("error", true);
                    ctx.sendBroadcast(i);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent i = new Intent(Constants.ADD_DIRECTIONS_OPERATION);
                i.putExtra("error", true);
                ctx.sendBroadcast(i);
            }
        });
        requestQueue.add(request);
    }


    private String buildPath(String searchedText) {
        String request;
        try {
            request = Constants.BASE_PLACE_URL;
            request += "q=" + URLEncoder.encode(searchedText, "UTF-8");
            request += "&format=json";
            request += "&countrycodes=ro";
            request += "&limit=" + LIST_LIMIT;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            request = "";
        }
        Log.d(TAG, "Request link " + request);
        return request;


    }


    private String buildLocationPath(LatLng from, LatLng to) {
        String path = Constants.DIRECTION_BASE_URL;
        String origin = "origin=" + from.latitude + "," + from.longitude;
        String destination = "destination=" + to.latitude + "," + to.longitude;
        String mode = "mode=walking";
        path += origin + "&" + destination + "&" + mode;
        Log.d(TAG, "Request link " + path);
        return path;
    }

}

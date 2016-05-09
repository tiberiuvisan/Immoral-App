package licenta.fastbanking.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import licenta.fastbanking.Objects.Directions;
import licenta.fastbanking.Objects.GeocodedWaypoint;
import licenta.fastbanking.Objects.Leg;

/**
 * Created by Tiberiu Visan on 5/8/2016.
 * Project: FastBanking
 */
public class NetworkUtils {

    public static final String TAG = "NetworkUtils";
    private RequestQueue requestQueue;
    private static NetworkUtils instance;
    private Context ctx;

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


    public void getDirections(LatLng from, LatLng to, String mode, final OnCompleteListener onCompleteListener) {
        StringRequest request = new StringRequest(Request.Method.GET, buildLocationPath(from, to, mode)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "get Direction response : " + response);
                Gson gson = new Gson();
                Directions directions = gson.fromJson(response, Directions.class);
                if (directions.status.equalsIgnoreCase("OK")) {
                    directions.routes.get(0).formattedLegs = new ArrayList<>();
                    for (int i = 0; i < directions.routes.get(0).legs.size(); i++) {
                        String formattedLeg = gson.toJson(directions.routes.get(0).legs.get(i));
                        Leg leg;
                        leg = gson.fromJson(formattedLeg, Leg.class);

                        directions.routes.get(0).formattedLegs.add(leg);
                        Log.d(TAG, "Formatted leg: " + formattedLeg);
                    }


                    Log.d(TAG, "direction route: " + directions.routes.get(0).legs.toString());
                    onCompleteListener.onComplete(true, directions);
                } else {
                    onCompleteListener.onComplete(false, null);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                onCompleteListener.onComplete(false, null);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1));
        requestQueue.add(request);
    }


    private String buildLocationPath(LatLng from, LatLng to, String travelMode) {
        String path = Constants.DIRECTION_BASE_URL;
        String origin = "origin=" + from.latitude + "," + from.longitude;
        String destination = "destination=" + to.latitude + "," + to.longitude;
        String mode = "mode=" + travelMode;
        path += origin + "&" + destination + "&" + mode;
        Log.d(TAG, "Request link " + path);
        return path;
    }
}

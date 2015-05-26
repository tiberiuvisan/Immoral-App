package ro.conceptapps.immoralapp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.map.MapUtils;
import ro.conceptapps.immoralapp.utils.Constants;
import ro.conceptapps.immoralapp.utils.Pin;
import ro.conceptapps.immoralapp.utils.PinDbHelper;
import ro.conceptapps.immoralapp.utils.UserDbHelper;

public class AlertManager {

    Context context;
    private GoogleMap map;
    private LinearLayout alertLayout;
    private ArrayList<Pin> markers;
    SharedPreferences sp;
    LatLng currentLocation;
    TextView alertDistance, alertTitle, alertReportedBy, alertDismiss;
    private Pin showingPin;
    MediaPlayer mp;
    Animation eventDown, eventUp;
    //Distanta de la care utilizatorul se atentioneaza;
    int WARNING_DISTANCE = 200;

    public AlertManager(Context context, GoogleMap map, LinearLayout alertLayout,
                        TextView alertDistance, TextView alertTitle, TextView alertReportedBy,
                        TextView alertDismiss) {

        this.context = context;
        this.map = map;
        this.alertLayout = alertLayout;
        this.alertDistance = alertDistance;
        this.alertTitle = alertTitle;
        this.alertReportedBy = alertReportedBy;
        this.alertDismiss = alertDismiss;
        sp = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        initArrayListElements();
        double lat = (double) sp.getFloat(Constants.SHARED_PREFS_LASTLAT, 0);
        double lng = (double) sp.getFloat(Constants.SHARED_PREFS_LASTLNG, 0);
        currentLocation = new LatLng(lat, lng);
        setAnimations();
    }

    private void setAnimations() {
        eventDown = AnimationUtils.loadAnimation(context, R.anim.anim_event_down);
        eventUp = AnimationUtils.loadAnimation(context, R.anim.anim_event_up);
    }

    private void animateLayout(boolean show) {
        if (show) {
            alertLayout.startAnimation(eventDown);
            //mp.start();
            alertLayout.setVisibility(View.VISIBLE);
        } else {
            alertLayout.startAnimation(eventUp);
            alertLayout.setVisibility(View.GONE);
        }
    }

    public void initArrayListElements() {
        Log.d("AlertManager", "Elements readded");
        markers = new ArrayList<>();
        markers = PinDbHelper.getPinsFromDatabase(context);
    }


    public void checkPoints(LatLng latLng) {
        if (markers.size() != 0) {
            for (int i = 0; i < markers.size(); i++) {
                markers.get(i).distance = MapUtils.distanceBetween(latLng, markers.get(i).getPosition());
            }
            Collections.sort(markers, comparatorDistance);
            Pin closestPin = markers.get(0);
            if (closestPin.distance < WARNING_DISTANCE) {
                if (showingPin == null) {
                    animateLayout(true);
                    showingPin = closestPin;
                    updateAlertInfo(showingPin);
                } else {
                    if (closestPin.id == showingPin.id) {
                        alertDistance.setText((int) closestPin.distance + " m");
                    } else {
                        Log.d("AlertManager", "changed closest point");
                        //mp.start();
                        showingPin = closestPin;
                        updateAlertInfo(showingPin);
                        if (alertLayout.getVisibility() != View.VISIBLE) {
                            animateLayout(true);
                        }
                    }
                }
            } else {
                if (alertLayout.getVisibility() == View.VISIBLE) animateLayout(false);
            }
            Log.d("AlertManager", "closest point: " + closestPin.distance);
        } /*else
            Toast.makeText(context, "Nu exista niciun eveniment immoral adaugat pe harta", Toast.LENGTH_LONG).show();*/
    }

    private void updateAlertInfo(Pin pin) {
        alertDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLayout(false);
            }
        });
        alertTitle.setText(pin.type);
        alertDistance.setText(((int) pin.distance) + " m");
        alertReportedBy.setText("Raportat de: " + UserDbHelper.getUserName(context, pin.userId));
    }

    private Comparator<Pin> comparatorDistance = new Comparator<Pin>() {
        @Override
        public int compare(Pin lhs, Pin rhs) {
            if (lhs.distance < rhs.distance)
                return -1;
            else if (lhs.distance > rhs.distance)
                return 1;
            else return 0;
        }
    };

    public int getNumberOfEvents(String encodedPolyline, Context context) {
        List<LatLng> polylinePins = PolyUtil.decode(encodedPolyline);
        ArrayList<Pin> markersForEvents = PinDbHelper.getPinsFromDatabase(context);
        ArrayList<Pin> finalPins = new ArrayList<>();
        Pin closestPin;
        //pentru fiecare punct din polyline pins sa verific fiecare punct
        //din markers for events, iar dupa sa verific fiecare element din markersFinal
        if (polylinePins.size() != 0) {
            for (int i = 0; i < polylinePins.size(); i++) {
                for (int j = 0; j < markersForEvents.size(); j++) {
                    markersForEvents.get(j).distance = MapUtils.distanceBetween(polylinePins.get(i),
                            markersForEvents.get(j).getPosition());
                }
                Collections.sort(markersForEvents, comparatorDistance);
                if (markersForEvents.size() != 0) {
                    closestPin = markersForEvents.get(0);
                    if (closestPin.distance < WARNING_DISTANCE) {
                        if (finalPins.size() == 0) finalPins.add(closestPin);
                        else {
                            boolean addPin = true;
                            for (int k = 0; k < finalPins.size(); k++) {
                                addPin = true;
                                if (closestPin.id == finalPins.get(k).id) {
                                    addPin = false;
                                    Log.d("AlertManager", "closest pin id: " + closestPin.id + " final pins id: " + finalPins.get(k).id + " " + addPin);
                                }
                            }
                            if (addPin) finalPins.add(closestPin);
                        }
                    }
                }
            }
        }
        return finalPins.size();
    }

}

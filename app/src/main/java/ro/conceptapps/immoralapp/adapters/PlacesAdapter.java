package ro.conceptapps.immoralapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.map.MapUtils;
import ro.conceptapps.immoralapp.object.Places;
import ro.conceptapps.immoralapp.utils.Constants;

public class PlacesAdapter extends BaseAdapter {

    private static final String TAG = "PlacesAdapter" ;
    Context ctx;
    ArrayList<Places> placesArrayList;
    private LayoutInflater inflater;
    SharedPreferences sp;

    public PlacesAdapter(Context ctx, ArrayList<Places> places) {
        this.ctx = ctx;
        placesArrayList = new ArrayList<Places>();
        placesArrayList.addAll(places);
        inflater = LayoutInflater.from(ctx);
        sp = ctx.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return placesArrayList.size();
    }

    @Override
    public Places getItem(int position) {
        return placesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return placesArrayList.get(position).getPlace_id();
    }

    public void sort() {
        Collections.sort(placesArrayList, importanceComparator);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.title);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.mainLayout = (LinearLayout) convertView.findViewById(R.id.mainLayout);
            holder.locate = (LinearLayout) convertView.findViewById(R.id.locate);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.name.setText(getItem(position).getName());
        float distance = MapUtils.distanceBetween(getCurrentLocation(), placesArrayList.get(position).getLocation());
        if (distance < 1000)
            holder.distance.setText(ctx.getResources().getString(R.string.distance_m, distance));
        else
            holder.distance.setText(ctx.getResources().getString(R.string.distance_km, distance / 1000));

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("returnLatitude", getItem(position).getLocation().latitude);
                returnIntent.putExtra("returnLongitude", getItem(position).getLocation().longitude);
                returnIntent.putExtra("returnLatNE", getItem(position).getLatLngBounds().northeast.latitude);
                returnIntent.putExtra("returnLngNE", getItem(position).getLatLngBounds().northeast.longitude);
                returnIntent.putExtra("returnLatSV", getItem(position).getLatLngBounds().southwest.latitude);
                returnIntent.putExtra("returnLngSV", getItem(position).getLatLngBounds().southwest.longitude);
                /*double returnLatNE;
                    double returnLngNE;
                    double returnLatSV;
                    double returnLngSV;*/
                Log.d(TAG,"Position : " + getItem(position).getLocation());
                ((Activity) ctx).setResult(Activity.RESULT_OK, returnIntent);
                ((Activity) ctx).finish();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView distance;
        LinearLayout locate;
        LinearLayout mainLayout;
    }

    private Comparator<Places> importanceComparator = new Comparator<Places>() {
        @Override
        public int compare(Places lhs, Places rhs) {
            if (lhs.getImportance() < rhs.getImportance())
                return -1;
            else if (lhs.getImportance() > rhs.getImportance())
                return 1;
            else
                return 0;
        }
    };

    private LatLng getCurrentLocation() {
        double lat = (double) sp.getFloat(Constants.SHARED_PREFS_LASTLAT, 0);
        double lng = (double) sp.getFloat(Constants.SHARED_PREFS_LASTLNG, 0);
        return new LatLng(lat, lng);
    }
}

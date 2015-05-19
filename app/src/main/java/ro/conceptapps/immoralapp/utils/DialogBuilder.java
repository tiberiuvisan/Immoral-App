package ro.conceptapps.immoralapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ro.conceptapps.immoralapp.R;

/**
 * Created by andreea on 5/19/2015.
 */
public class DialogBuilder {
    public static String activityType, activityDesc;

    public static void addPinPopup(final Context context, final LatLng latLng){

        AlertDialogWrapper.Builder adb = new AlertDialogWrapper.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_add_pin, null);

        final Spinner type = (Spinner)view.findViewById(R.id.type);
        final TextView desc = (TextView)view.findViewById(R.id.description);

        ArrayList<String> typeList = new ArrayList<>();
        String immoralActivities[] = context.getResources().getStringArray(R.array.type);
        for (int i = 0; i < immoralActivities.length; i++) {
            typeList.add(immoralActivities[i]);
        }
        ArrayAdapter<String> data = new ArrayAdapter<String>(context, R.layout.spinner_item,
                typeList);
        data.setDropDownViewResource(R.layout.spinner_item);
        type.setAdapter(data);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activityType = (String) type.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        activityDesc = desc.getText().toString();

        adb.setView(view)
                .autoDismiss(false)
                .setCancelable(false)
                .setTitle("Adauga pin")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        PinDbHelper.addPinToDatabase(context, 1, activityType, activityDesc, latLng.latitude, latLng.longitude);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = adb.create();
        d.findViewById(R.id.titleFrame).setBackgroundColor(context.getResources().getColor(R.color.dark_blue));
        ((TextView) d.findViewById(R.id.title)).setTextColor(Color.WHITE);

}
}

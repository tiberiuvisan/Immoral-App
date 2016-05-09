package licenta.fastbanking.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import licenta.fastbanking.Objects.Bank;
import licenta.fastbanking.Objects.Directions;
import licenta.fastbanking.Objects.Leg;
import licenta.fastbanking.R;

/**
 * Created by Andreea on 4/25/2016.
 */
public class DialogBuilder {

    private static ProgressDialog progressDialog;

    public static void createProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Se obtin datele...");
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog.isShowing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public static void disableOkButton(DialogInterface dialog) {
        final View ok = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setEnabled(false);
    }

    public static void enableOkButton(DialogInterface dialog) {
        final View ok = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setEnabled(true);
    }

    public static void DialogBankInfo(Context context, Bank bank, Directions directions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_Dialog));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bank_info, null);

        TextView name = (TextView) view.findViewById(R.id.bank_name);
        TextView counters = (TextView) view.findViewById(R.id.bank_counters_number);
        TextView total_people = (TextView) view.findViewById(R.id.bank_total_people);
        TextView waitTime = (TextView) view.findViewById(R.id.bank_wait_time);
        TextView travelTime = (TextView) view.findViewById(R.id.bank_total_travel_time);
        TextView totalTime = (TextView) view.findViewById(R.id.bank_total_time);
        changeTitleColor(context, name, bank);
        name.setText(bank.name);
        counters.setText(String.valueOf(bank.countersNumber));
        total_people.setText(String.valueOf(bank.totalPeople));
        waitTime.setText(String.valueOf(bank.waitTime));
        int travelTimeInt = 0;
        for (Leg leg : directions.routes.get(0).formattedLegs) {
            //am facut conversie din secunde in minute
            travelTimeInt += (leg.duration.value / 60);
        }
        travelTime.setText(String.valueOf(travelTimeInt));
        totalTime.setText(String.valueOf(travelTimeInt + bank.calculateWaitTime()));

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public static void changeTitleColor(Context context, TextView title, Bank bank) {
        if (bank.calculateWaitTime() < 5) {
            title.setBackgroundColor(context.getResources().getColor(R.color.fast_green_light));
        } else if (bank.calculateWaitTime() < 15) {
            title.setBackgroundColor(context.getResources().getColor(R.color.fast_orange));
        } else {
            title.setBackgroundColor(context.getResources().getColor(R.color.fast_red));

        }

    }


    /*
    * Dialog pentru cererea de informatii GPS
    * */


    private static boolean requestGPS = false;

    public static void showDialogEnableLocation(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        alertDialogBuilder.setView(inflater.inflate(R.layout.dialog_location, null));

        alertDialogBuilder.setNegativeButton("Anulare", null)
                .setPositiveButton("Continua", null);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                //set buttons
                Button ok = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setTextSize(17);
                ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            Intent gpsOptionsIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                            context.startActivity(gpsOptionsIntent);
                            alertDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                //set buttons
                Button cancel = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setTextSize(17);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        // show it
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            alertDialog.show();
        }

    }
}

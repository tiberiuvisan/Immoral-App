package licenta.fastbanking.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import licenta.fastbanking.Objects.Bank;
import licenta.fastbanking.R;

/**
 * Created by Andreea on 4/25/2016.
 */
public class DialogBuilder {


    public static void disableOkButton(DialogInterface dialog) {
        final View ok = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setEnabled(false);
    }

    public static void enableOkButton(DialogInterface dialog) {
        final View ok = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setEnabled(true);
    }

    public static void DialogBankInfo(Context context, Bank bank){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_Dialog));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bank_info, null);

        TextView name  = (TextView)view.findViewById(R.id.bank_name);
        TextView counters = (TextView)view.findViewById(R.id.bank_counters_number);
        TextView total_people = (TextView)view.findViewById(R.id.bank_total_people);
        TextView time = (TextView)view.findViewById(R.id.bank_wait_time);
        changeTitleColor(context, name, bank);
        name.setText(bank.name);
        counters.setText(String.valueOf(bank.countersNumber));
        total_people.setText(String.valueOf(bank.totalPeople));
        time.setText(String.valueOf(bank.waitTime));

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public static void changeTitleColor(Context context, TextView title, Bank bank){
        if(bank.calculateWaitTime()<5){
            title.setBackgroundColor(context.getResources().getColor(R.color.fast_yellow_light));
        }else if(bank.calculateWaitTime()<15){
            title.setBackgroundColor(context.getResources().getColor(R.color.fast_orange));
        }else{
            title.setBackgroundColor(context.getResources().getColor(R.color.fast_red));

        }

    }
}

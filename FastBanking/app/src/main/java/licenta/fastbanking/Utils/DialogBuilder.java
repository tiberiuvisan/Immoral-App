package licenta.fastbanking.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

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
}

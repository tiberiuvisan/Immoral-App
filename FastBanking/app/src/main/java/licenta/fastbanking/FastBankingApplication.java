package licenta.fastbanking;

import android.app.Application;

import licenta.fastbanking.Managers.SessionManager;

/**
 * Created by Andreea on 4/20/2016.
 */
public class FastBankingApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SessionManager.initSharedPreferences(getApplicationContext());
    }
}

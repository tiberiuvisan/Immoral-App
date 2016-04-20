package licenta.fastbanking.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import licenta.fastbanking.Utils.Constants;

/**
 * Created by Tiberiu Visan on 4/11/2016.
 * Project: FastBanking
 */
public class SessionManager {

    private static Context context;
    private static SessionManager sm;

    private static SharedPreferences authSharedPrefs;
    private static SharedPreferences.Editor authSharedPrefsEditor;





    public static SessionManager getInstance() {

        if (sm == null) {
            sm = new SessionManager();
        }

        return sm;
    }
    public static void initSharedPreferences(Context context) {
        authSharedPrefs = context.getSharedPreferences(Constants.AUTH_IS_LOGGED_IN, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return authSharedPrefs != null && authSharedPrefs.getBoolean(Constants.AUTH_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        authSharedPrefsEditor = authSharedPrefs.edit();
        authSharedPrefsEditor.putBoolean(Constants.AUTH_IS_LOGGED_IN, isLoggedIn);
        authSharedPrefsEditor.apply();
    }

    public int getId(){
        return authSharedPrefs.getInt(Constants.AUTH_USER_ID, -1);
    }

    public  void setId(int id){
        authSharedPrefsEditor = authSharedPrefs.edit();
        authSharedPrefsEditor.putInt(Constants.AUTH_USER_ID, id);
        authSharedPrefsEditor.apply();
    }

}

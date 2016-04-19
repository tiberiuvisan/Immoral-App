package licenta.fastbanking.Managers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tiberiu Visan on 4/11/2016.
 * Project: FastBanking
 */
public class SessionManager {

    private static Context context;
    private static SessionManager sm;

    private static SharedPreferences authSharedPrefs;
    private static SharedPreferences.Editor authSharedPrefsEditor;
    private static final String AUTH_SHARED_PREFS_KEY = "authSharedPrefs";
    private static final String AUTH_IS_LOGGED_IN = "authSharedPrefsLogin";



    public static SessionManager getInstance() {

        if (sm == null) {
            sm = new SessionManager();
        }

        return sm;
    }
    public static void initSharedPreferences(Context context) {
        authSharedPrefs = context.getSharedPreferences(AUTH_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    public static boolean isLoggedIn() {
        return authSharedPrefs != null && authSharedPrefs.getBoolean(AUTH_IS_LOGGED_IN, false);
    }

    public static void setLoggedIn(boolean isLoggedIn) {
        authSharedPrefsEditor = authSharedPrefs.edit();
        authSharedPrefsEditor.putBoolean(AUTH_IS_LOGGED_IN, isLoggedIn);
        authSharedPrefsEditor.apply();
    }

}

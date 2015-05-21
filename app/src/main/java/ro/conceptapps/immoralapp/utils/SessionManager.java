package ro.conceptapps.immoralapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by andreea on 5/20/2015.
 */
public class SessionManager  {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static SessionManager sm;
    private static String TAG = "SessionManager";

    protected SessionManager() {
    }

    public static SessionManager getInstance() {

        if (sm == null) {
            sm = new SessionManager();
        }

        return sm;
    }

    public static void initInstance(Context context) {
        getInstance().sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        getInstance().editor = getInstance().sharedPreferences.edit();
    }

    public void saveId(int id) {
        editor.putInt(Constants.SHARED_PREFS_USER_ID, id).commit();
        Log.d(TAG, "Id saved");

    }

    public int getId() {
        return sharedPreferences.getInt(Constants.SHARED_PREFS_USER_ID, -1);
    }

    public void saveLoggedIn(boolean loggedIn) {
        editor.putBoolean(Constants.SHARED_PREFS_LOGGED_IN, loggedIn).commit();
        Log.d(TAG, "loggedIn saved");

    }

    public boolean getLoggedIn() {
        return sharedPreferences.getBoolean(Constants.SHARED_PREFS_LOGGED_IN, false);
    }

    public boolean isLoggedIn() {
        if (sharedPreferences.contains(Constants.SHARED_PREFS_LOGGED_IN)) {
            boolean isLoggedIn = SessionManager.getInstance().getLoggedIn();
            if (isLoggedIn) {
                Log.d(TAG,"Has token");
                return true;
            }
        }
        Log.d(TAG,"No token");
        return false;
    }

    public void logout() {
        editor.clear().commit();
        saveLoggedIn(false);
    }


}

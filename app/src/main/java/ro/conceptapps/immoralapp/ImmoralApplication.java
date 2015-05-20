package ro.conceptapps.immoralapp;

import android.app.Application;

import ro.conceptapps.immoralapp.utils.SessionManager;

/**
 * Created by andreea on 5/20/2015.
 */
public class ImmoralApplication extends Application {


    @Override
    public void onCreate()
    {
        super.onCreate();
        SessionManager.initInstance(getApplicationContext());
    }

}

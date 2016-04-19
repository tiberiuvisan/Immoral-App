package ro.conceptapps.immoralapp;

import android.app.Application;

import ro.conceptapps.immoralapp.utils.SessionManager;

public class ImmoralApplication extends Application {


    @Override
    public void onCreate()
    {
        super.onCreate();
        SessionManager.initInstance(getApplicationContext());
    }

}

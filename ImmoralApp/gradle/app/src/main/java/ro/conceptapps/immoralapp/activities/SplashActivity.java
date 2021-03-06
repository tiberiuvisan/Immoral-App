package ro.conceptapps.immoralapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.utils.SessionManager;

public class SplashActivity extends Activity {

    boolean isShown = true;
    @Override


    // Exista aplicatia care se lanseaza prima;
    // Decide (in functie daca utilizatorul este logat sau nu) ce activitate porneste urmatoarea;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        int secondsDelayed = 2;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isShown) {
                    if (SessionManager.getInstance().isLoggedIn()) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        }, secondsDelayed * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShown = true;
    }

    @Override
    protected void onPause() {
        super.onDestroy();
        isShown = false;
    }
}


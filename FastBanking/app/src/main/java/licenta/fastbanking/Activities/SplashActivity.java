package licenta.fastbanking.Activities;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.TextView;

import licenta.fastbanking.Managers.SessionManager;
import licenta.fastbanking.Map.MainActivity;
import licenta.fastbanking.R;


public class SplashActivity extends Activity {

    //Views
    private TextView logo;

    //Constants
    private int ANIMATION_DURATION = 2000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initialiseUI();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initialiseUI() {
        logo = (TextView) findViewById(R.id.splash_logo);

        if (logo != null) {
            logo.animate().alpha(1).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(SessionManager.getInstance().isLoggedIn()){
                        startMainActivity();
                    }else {
                        startLoginActivity();

                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                SplashActivity.this.finish();
            }
        }, ANIMATION_DURATION);

    }

    private void startLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                i.putExtra("title", "TITLE");

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this, logo, "logo");
                ActivityCompat.startActivity(SplashActivity.this, i, options.toBundle());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SplashActivity.this.finish();
                    }
                },1000);

            }
        }, ANIMATION_DURATION);

    }
}

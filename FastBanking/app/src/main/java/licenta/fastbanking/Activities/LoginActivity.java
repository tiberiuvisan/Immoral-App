package licenta.fastbanking.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import licenta.fastbanking.Managers.SessionManager;
import licenta.fastbanking.Map.MainActivity;
import licenta.fastbanking.R;
import licenta.fastbanking.Utils.UserDbHelper;

/**
 * Created by Tiberiu Visan on 4/11/2016.
 * Project: FastBanking
 */
public class LoginActivity extends AppCompatActivity {


    private TextInputEditText username;
    private TextInputEditText password;
    private Button login;
    private Button register;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setTransitionName(findViewById(R.id.login_title),"logo");
        initialiseUI();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_login));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initialiseUI() {
        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.btn_login);
        register = (Button) findViewById(R.id.btn_register);
        title = (TextView) findViewById(R.id.login_title);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(LoginActivity.this, title, "logo");
                ActivityCompat.startActivity(LoginActivity.this, i, options.toBundle());

            }
        });
    }

    private void doLogin() {
        if (UserDbHelper.checkLogin(this, username.getText().toString().trim(), password.getText().toString()) != -1) {
            SessionManager.getInstance().setId(UserDbHelper.getId(LoginActivity.this, username.getText().toString()));
            SessionManager.getInstance().setLoggedIn(true);
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("username",username.getText().toString().trim());
            startActivity(i);
            LoginActivity.this.finish();
        } else {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }
}

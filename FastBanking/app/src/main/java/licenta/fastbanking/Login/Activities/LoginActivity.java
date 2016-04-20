package licenta.fastbanking.Login.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


    EditText username;
    EditText password;
    Button login;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        username.setText("");
        password.setText("");
        username.requestFocus();
    }

    private void initialiseUI() {
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.btn_login);
        register = (Button)findViewById(R.id.btn_register);

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
                startActivity(i);
            }
        });
    }

    private void doLogin() {
        if(UserDbHelper.checkLogin(this, username.getText().toString(), password.getText().toString())!=-1){
            SessionManager.getInstance().setId(UserDbHelper.getId(LoginActivity.this, username.getText().toString()));
            SessionManager.getInstance().setLoggedIn(true);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }else{
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT ).show();
        }
    }
}

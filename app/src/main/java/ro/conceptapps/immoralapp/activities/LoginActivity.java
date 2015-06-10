package ro.conceptapps.immoralapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.utils.SessionManager;
import ro.conceptapps.immoralapp.utils.UserDbHelper;

//Activitatea in care se face log in

public class LoginActivity extends ActionBarActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    Toolbar toolbar;

    //Functia in care se creeaza view-ul
    //folosim layout-ul activity_login
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usernameEditText.setText("");
        passwordEditText.setText("");
        usernameEditText.requestFocus();
    }


    private void initView() {
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.btn_login);
        registerButton = (Button) findViewById(R.id.btn_register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
        setToolbar();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(i);
            }
        });

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    //functia in care se face login
    //folosim checkLogin pentru a verifica daca utilizatorul cu usernameul si parola
    //introduse exista in baza de date sau nu.
    //in cazul in care exista, salvam id-ul userului(prin functia saveId) si setam booleana isLogged
    //(prin functia saveLoggedIn) in ca true. ea va retine faptul ca s-a facut autentificarea.
    //apoi pornim activitatea principala MainActivity si o inchidem pe cea de Login
    //in cazul in care nu exista userul in baza de date, afisam mesajul "Utilizator gresit".

    private void doLogin() {
        if (UserDbHelper.checkLogin(LoginActivity.this, usernameEditText.getText().toString(), passwordEditText.getText().toString()) != -1) {
            SessionManager.getInstance().saveId(UserDbHelper.getId(LoginActivity.this, usernameEditText.getText().toString()));
            SessionManager.getInstance().saveLoggedIn(true);
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        } else {
            Toast.makeText(LoginActivity.this, "Utilizator gresit", Toast.LENGTH_SHORT).show();
        }
    }
}

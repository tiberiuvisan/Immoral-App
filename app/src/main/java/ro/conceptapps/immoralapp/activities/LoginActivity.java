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
import ro.conceptapps.immoralapp.utils.SQLiteHelper;


public class LoginActivity extends ActionBarActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private Button skipButton;

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
        skipButton = (Button) findViewById(R.id.btn_skip);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }


    private void doLogin() {
        /*if (usernameEditText.getText().toString().equals("admin") &&
                passwordEditText.getText().toString().equals("admin")) {
            doAdminLogin();
        } else {*/

        if (SQLiteHelper.checkLogin(LoginActivity.this, usernameEditText.getText().toString(), passwordEditText.getText().toString()) != -1) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        } else {
            Toast.makeText(LoginActivity.this, "User invalid", Toast.LENGTH_SHORT).show();
        }


    }

    private void doAdminLogin() {
        //  startActivity(new Intent(this, AdminActivity.class));
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }*/
}

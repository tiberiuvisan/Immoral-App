package ro.conceptapps.immoralapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ro.conceptapps.immoralapp.R;
import ro.conceptapps.immoralapp.utils.UserDbHelper;

public class RegisterActivity extends ActionBarActivity {
    EditText username;
    EditText password;
    EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {
        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        final Button register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }


    private void registerUser() {
        if (username.getText().toString().length() > 0 && password.getText().toString().length() > 0) {

            UserDbHelper.addUserToDatabase(this, username.getText().toString(), password.getText().toString(), phoneNumber.getText().toString());
            Toast.makeText(this, "Utilizatorul a fost adaugat", Toast.LENGTH_SHORT).show();
            RegisterActivity.this.finish();
        } else {
            Toast.makeText(this, "Nu ati completat toate campurile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

}

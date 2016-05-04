package licenta.fastbanking.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import licenta.fastbanking.R;
import licenta.fastbanking.Utils.UserDbHelper;

/**
 * Created by Andreea on 4/19/2016.
 */
public class RegisterActivity extends AppCompatActivity {
    TextInputEditText user;
    TextInputEditText password;
    TextInputEditText phone;
    CheckBox isAdmin;
    Button register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewCompat.setTransitionName(findViewById(R.id.register_title), "logo");
        initialiseUI();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_activity_register));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    private void initialiseUI() {

        user = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        phone = (TextInputEditText) findViewById(R.id.phone);
        isAdmin = (CheckBox) findViewById(R.id.isAdmin);

        register = (Button) findViewById(R.id.btn_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        if (user.getText().toString().length() > 0 && password.getText().toString().length() > 0) {

            UserDbHelper.addUserToDatabase(this, user.getText().toString(), password.getText().toString(), phone.getText().toString(), isAdmin.isChecked());
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            startLoginActivity();
        } else {
            Toast.makeText(this, R.string.register_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void startLoginActivity() {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
        RegisterActivity.this.finish();

    }


}

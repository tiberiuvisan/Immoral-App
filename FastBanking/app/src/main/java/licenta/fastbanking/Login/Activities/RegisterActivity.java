package licenta.fastbanking.Login.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
public class RegisterActivity extends AppCompatActivity{
    EditText user;
    EditText password;
    EditText phone;
    CheckBox isAdmin;
    Button register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialiseUI();
    }

    private void initialiseUI() {

        user = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        phone = (EditText)findViewById(R.id.phone);
        isAdmin = (CheckBox)findViewById(R.id.isAdmin);

        register = (Button)findViewById(R.id.btn_register);

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

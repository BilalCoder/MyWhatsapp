package com.example.mywhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText lName, lPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lName = findViewById(R.id.lName);
        lPassword = findViewById(R.id.lPassword);
        lPassword.setOnKeyListener(new View.OnKeyListener() {                   // this is for the enter key pressed on the touchpad
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    logInUser(view);
                }
                return false;
            }
        });
    }

    public void openSignupActivity(View view) {
       Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
       startActivity(intent);
    }

    public void logInUser(View view) {
        if (lName.getText().toString().isEmpty())
            lName.setError("This filed cant be empty");
        else if (lPassword.getText().toString().isEmpty())
            lPassword.setError("This field cant be enpty");
        else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Logging you in");
            progressDialog.show();
            ParseUser.logInInBackground(lName.getText().toString(), lPassword.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null && e == null) {
                        Toast.makeText(getApplicationContext(), "You are logged in sucessfully", Toast.LENGTH_SHORT).show();
                        openMainActivity();
                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void openMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}

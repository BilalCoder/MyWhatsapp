package com.example.mywhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    private EditText sName, sEmail, sPassword, sPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sName = findViewById(R.id.sName);
        sPassword = findViewById(R.id.sPassword);
        sPhone = findViewById(R.id.sPhone);
        sEmail = findViewById(R.id.sEmail);
        sPassword.setOnKeyListener(new View.OnKeyListener() {                   // this is for the enter key pressed on the touchpad
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    signUpUser(view);
                }
                return false;
            }
        });
    }

    public void signUpUser(View view) {
        if (!(sName.getText().toString().equals("") || sPassword.getText().toString().equals("") || sEmail.getText().toString().equals(""))) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registering you up");
            progressDialog.show();

            ParseUser user = new ParseUser();
            user.setUsername(sName.getText().toString());
            user.setPassword(sPassword.getText().toString());
            user.setEmail(sEmail.getText().toString());

// other fields can be set just like with ParseObject
            user.put("Phone", sPhone.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        Toast.makeText(getApplicationContext(), "Registration Sucessful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
        else{
            if(sName.getText().toString().isEmpty())
                sName.setError("This field cant be empty");
            if(sPassword.getText().toString().isEmpty())
                sPassword.setError("This field cant be empty");
            if(sEmail.getText().toString().isEmpty())
                sEmail.setError("This field cant be empty");
        }
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}

package com.test.stream.stream.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.stream.stream.R;

public class SignUpScreen extends AppCompatActivity implements View.OnClickListener{

    EditText enterNewName, enterNewUsername,  enterNewPassword;
    static EditText enterNewEmail;
    TextView signInTitle;
    Button continueSignUp;
    public static String newEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        //Initializations
        signInTitle = (TextView) findViewById(R.id.signUpTitle);
        enterNewName = (EditText) findViewById(R.id.enterNewName);
        enterNewUsername = (EditText) findViewById(R.id.enterNewUsername);
        enterNewEmail = (EditText) findViewById(R.id.enterNewEmail);
        enterNewPassword = (EditText) findViewById(R.id.enterNewPasswords);
        continueSignUp = (Button) findViewById(R.id.continueWithSignUp);

        //Syncopate
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        signInTitle.setTypeface(Syncopate);
        enterNewName.setTypeface(Syncopate);
        enterNewUsername.setTypeface(Syncopate);
        enterNewEmail.setTypeface(Syncopate);
        enterNewPassword.setTypeface(Syncopate);
        continueSignUp.setTypeface(Syncopate);

        //Set onClickListener
        continueSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.continueWithSignUp:
                //newEmail = enterNewEmail.getText().toString();
                startActivity(new Intent(SignUpScreen.this, VerificationCode.class));
        }
    }

    public static String getEmail() {
        return newEmail = enterNewEmail.getText().toString();
    }
}

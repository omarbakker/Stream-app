package com.test.stream.stream.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.stream.stream.R;

import static com.test.stream.stream.R.string.email_not_valid;
import static com.test.stream.stream.R.string.error_field_required;
import static com.test.stream.stream.R.string.password_not_valid;

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
               verifyInformation();
        }
    }

    public static String getEmail() {
        return newEmail = enterNewEmail.getText().toString();
    }

    public void verifyInformation(){
        View focusView = null;
        boolean informationCorrect = true;
        String email = enterNewEmail.getText().toString();
        String password = enterNewPassword.getText().toString();
        String name = enterNewName.getText().toString();
        String username = enterNewUsername.getText().toString();

        if(!TextUtils.isEmpty(email)) {
            enterNewEmail.setError(getText(error_field_required));
            focusView = enterNewEmail;
            informationCorrect = false;
        }
        if (!isEmailValid(email)) {
            enterNewEmail.setError(getText(email_not_valid));
            informationCorrect = false;
        }
        if (!TextUtils.isEmpty(password)) {
            enterNewPassword.setError(getText(error_field_required));
            focusView = enterNewPassword;
            informationCorrect = false;
        }
        if (!isPasswordValid(password)) {
            enterNewPassword.setError(getText(password_not_valid));
            informationCorrect = false;
        }
        if (!TextUtils.isEmpty((name))) {
            enterNewName.setError(getText(error_field_required));
            informationCorrect = false;
        }
        if (!TextUtils.isEmpty((username))) {
            enterNewUsername.setError(getText(error_field_required));
            informationCorrect = false;
        }
        if(informationCorrect){
            startActivity(new Intent (SignUpScreen.this, VerificationCode.class));
        }
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}

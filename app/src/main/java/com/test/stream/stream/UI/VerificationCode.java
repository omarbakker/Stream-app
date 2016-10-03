package com.example.rohinigoyal.loginstream;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.example.rohinigoyal.loginstream.R.id.enterEmail;

public class VerificationCode extends AppCompatActivity {

    TextView whichEmail, verificationCodeExplanation, emailEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        whichEmail = (TextView) findViewById(R.id.textView);
        verificationCodeExplanation = (TextView) findViewById(R.id.textView2);
        emailEntered = (TextView) findViewById(R.id.emailEntered);

        whichEmail.setText("We just sent you an email at");
        emailEntered.setText(SignUpScreen.getEmail());


        //Syncopate
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        whichEmail.setTypeface(Syncopate);
        verificationCodeExplanation.setTypeface(Syncopate);
        emailEntered.setTypeface(Syncopate);
    }


}

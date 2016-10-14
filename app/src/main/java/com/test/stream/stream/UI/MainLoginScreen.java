package com.test.stream.stream.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.test.stream.stream.R;

import java.util.Arrays;

public class MainLoginScreen extends AppCompatActivity implements View.OnClickListener {


    TextView signup, forgotPassword, orDifferentLogin;
    Button login;
    AppCompatEditText enterEmail;
    EditText enterPassword;
    Switch rememberMe;
    LoginButton loginWithFacebook;
    private View mLoginFormView;
    private View mProgressView;
    CallbackManager callbackManager;
    Profile userProfile;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_login_screen);

        //Initializations
        signup = (TextView) findViewById(R.id.signUp);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        loginWithFacebook = (LoginButton) findViewById(R.id.loginWithFacebook);
        login = (Button) findViewById(R.id.login);
        enterEmail = (AppCompatEditText) findViewById(R.id.enterEmail);
        enterPassword = (EditText) findViewById(R.id.enterNewPassword);
        rememberMe = (Switch) findViewById(R.id.rememberMe);
        orDifferentLogin = (TextView) findViewById(R.id.orDifferentLogin);


        //Changing font to Syncopate
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Syncopate-Regular.ttf");
        Typeface SyncopateBold = Typeface.createFromAsset(this.getAssets(), "Syncopate-Bold.ttf");
        signup.setTypeface(Syncopate);
        forgotPassword.setTypeface(Syncopate);
        login.setTypeface(Syncopate);
        enterEmail.setTypeface(Syncopate);
        enterPassword.setTypeface(Syncopate);
        rememberMe.setTypeface(Syncopate);
        orDifferentLogin.setTypeface(SyncopateBold);


        signup.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        loginWithFacebook.setOnClickListener(this);
        login.setOnClickListener(this);

        //Facebook login set-up
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //if login succeeds, the loginResult has the new AccessToken and most recently granted or declined permissions
                AccessToken accessToken = loginResult.getAccessToken();
                userName = userProfile.getFirstName() + " " + userProfile.getLastName();
                startActivity(new Intent(MainLoginScreen.this, SignUpScreen.class));
            }

            @Override
            public void onCancel() {
                startActivity(new Intent(MainLoginScreen.this, MainLoginScreen.class));
            }

            @Override
            public void onError(FacebookException error) {
                startActivity(new Intent(MainLoginScreen.this, MainLoginScreen.class));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signUp:
                startActivity(new Intent(MainLoginScreen.this, SignUpScreen.class));
                break;
            case R.id.forgotPassword:
                //startActivity(new Intent(MainLoginScreen.this, ForgotPassword.class));
                break;
            case R.id.loginWithFacebook:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
                break;
            case R.id.login:
                attemptLogin();
                startActivity(new Intent(MainLoginScreen.this, SignUpScreen.class));
                break;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void attemptLogin() {

        boolean cancel = false;
        View focusView = null;
        // Store values at the time of the login attempt.
        String email = enterEmail.getText().toString();
        String password = enterPassword.getText().toString();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            enterPassword.setError("@string/password_not_valid");
            focusView = enterPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            enterEmail.setError("@string/error_field_required");
            focusView = enterEmail;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            enterEmail.setError("@string/email_username_not_valid");
            focusView = enterEmail;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
        }
        else {
            showProgress(true);
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,data);
    }




}

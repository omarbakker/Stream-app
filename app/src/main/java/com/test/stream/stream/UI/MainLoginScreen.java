package com.test.stream.stream.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String TAG;


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

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };

        //Facebook login set-upx
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken(); //LoginResult has the new access token and granted permissions of login succeeds.
                handleFacebookAccessToken(accessToken);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,data);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
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
            enterPassword.setError("Password is not valid");
            focusView = enterPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            enterEmail.setError("This field is required");
            focusView = enterEmail;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            enterEmail.setError("This email or username is invalid");
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

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                        else
                        {
                            Toast.makeText(MainLoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser()
    {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = new User(mUser.getUid(), mUser.getDisplayName());
        DatabaseManager.getInstance().writeObject(DatabaseFolders.Users, user);
    }

    private void getUser(String uid){
        DatabaseReference myRef = DatabaseManager.getInstance().getReference("users");
        Query usernameQuery = myRef.orderByChild("uid").equalTo(uid);

        usernameQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                        {
                            createUser();
                        }
                        else
                        {
                            Toast.makeText(MainLoginScreen.this, "User already exists",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

    }
}

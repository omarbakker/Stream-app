package com.test.stream.stream.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;

import static com.test.stream.stream.Controllers.UserManager.createUserIfNotExist;


public class SignUpScreen extends AppCompatActivity implements View.OnClickListener {

    SignUpScreen context;
    EditText enterNewName, enterNewUsername, enterNewPassword;
    static EditText enterNewEmail;
    TextView signInTitle, signUpDivider;
    Button continueSignUp;
    public static String newEmail;
    LoginButton signUpWithFacebook;
    CallbackManager callbackManager;
    //add tag
    public static final String TAG = "SignUpScreen";

    //setting up firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseInstanceId firebaseInstance;

    private boolean thread_running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        context = this;

        setContentView(R.layout.activity_sign_up_screen);

        //Initializations
        signInTitle = (TextView) findViewById(R.id.signUpTitle);
        enterNewName = (EditText) findViewById(R.id.enterNewName);
        enterNewUsername = (EditText) findViewById(R.id.enterNewUsername);
        enterNewEmail = (EditText) findViewById(R.id.enterNewEmail);
        enterNewPassword = (EditText) findViewById(R.id.enterNewPasswords);
        continueSignUp = (Button) findViewById(R.id.continueWithSignUp);
        signUpWithFacebook = (LoginButton) findViewById(R.id.loginWithFacebookSignup);
        signUpDivider = (TextView) findViewById(R.id.textView3);


        //Syncopate
        Typeface Syncopate = Typeface.createFromAsset(this.getAssets(), "Raleway-Regular.ttf");
        Typeface RalewayBold = Typeface.createFromAsset(this.getAssets(), "Raleway-Bold.ttf");
        signInTitle.setTypeface(RalewayBold);
        enterNewName.setTypeface(Syncopate);
        enterNewUsername.setTypeface(Syncopate);
        enterNewEmail.setTypeface(Syncopate);
        enterNewPassword.setTypeface(Syncopate);
        continueSignUp.setTypeface(Syncopate);
        signUpDivider.setTypeface(Syncopate);

        //Set onClickListener
        continueSignUp.setOnClickListener(this);
        signUpWithFacebook.setOnClickListener(this);

        //Initialize firebase authentication variables
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Facebook login set-upx
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("LOGIN WAS SUCCESSFUL");
                AccessToken accessToken = loginResult.getAccessToken(); //LoginResult has the new access token and granted permissions of login succeeds.
                handleFacebookAccessTokenSignup(accessToken);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("LOGIN WAS NOT SUCCESSFUL");

            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,data);
    }

    /**
     * Creates a new user using the information that the user has inputted
     * @param email email that the user has entered for creating their new account
     * @param password password that the user has entered for creating their new account
     * @param name the name of the user
     * @param username username that the user has entered for creating their new account
     * @return flag that the user was successfully created
     */
    private boolean createAccount(final String email, String password, final String name, final String username) {
        boolean valid = false;
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return valid;
        }

        //Disable the button if we are creating a user.
        continueSignUp.setEnabled(false);

        // [START create_user_with_email] firebase users
        System.out.println(email + " " + password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            UserManager.createUserIfNotExist(username, name, email, new FetchUserCallback() {
                                @Override
                                public void onDataRetrieved(User result) {
                                    Intent intent = new Intent(SignUpScreen.this, ProjectsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    context.finish();
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(SignUpScreen.this, e.getReason(),
                                        Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(SignUpScreen.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(SignUpScreen.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }

                        }
                    }
                });
        


        // [START_EXCLUDE]
        //hideProgressDialog();
        // [END_EXCLUDE]
        //     }
        //});

        valid = true;
        //TODO: in the future a callback function from writeObject must be used to check validity of write

        return valid;

    }

    /**
     * Checks to see that all of the sign-up fields are valid and not empty
     * @return flag indicated that all of the information is valid
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = enterNewEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            enterNewEmail.setError("Required.");
            valid = false;
        } else {
            enterNewEmail.setError(null);
        }

        String password = enterNewPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            enterNewPassword.setError("Required.");
            valid = false;
        } else {
            enterNewPassword.setError(null);
        }

        String name = enterNewName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            enterNewName.setError("Required.");
            valid = false;
        } else {
            enterNewName.setError(null);
        }

        String username = enterNewUsername.getText().toString();
        //TODO: Enforce username duplicate error
        if (TextUtils.isEmpty(username)) {
            enterNewUsername.setError("Required.");
            valid = false;
        } else {
            enterNewUsername.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueWithSignUp:
                //newEmail = enterNewEmail.getText().toString();
                //startActivity(new Intent(SignUpScreen.this, VerificationCode.class));
                Log.v("click", "creating new email-user");
                createAccount(enterNewEmail.getText().toString(),
                        enterNewPassword.getText().toString(),
                        enterNewName.getText().toString(),
                        enterNewUsername.getText().toString());
                break;
            case R.id.loginWithFacebookSignup:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                System.out.println("LOGIN WITH FACEBOOK BUTTON WAS CLICKED");
        }

    }


    /**
     * Returns the email that the user entered
     * @return String for the email that the user entered
     */
    public static String getEmail() {
        return newEmail = enterNewEmail.getText().toString();
    }

    private void handleFacebookAccessTokenSignup(AccessToken token) {
        System.out.println("handleFacebookAccessTokensSignup was called");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createUserIfNotExist(new FetchUserCallback() {
                                @Override
                                public void onDataRetrieved(User result) {
                                    System.out.println("CHANGE SCREEN TO PROJECTS PAGE");
                                    Intent intent = new Intent(SignUpScreen.this, ProjectsActivity.class);
                                    startActivity(intent);
                                }});

                        }
                        else
                        {
                            Toast.makeText(SignUpScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


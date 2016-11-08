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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.test.stream.stream.R;
import com.test.stream.stream.Services.MyFirebaseInstanceIDService;

public class SignUpScreen extends AppCompatActivity implements View.OnClickListener{

    EditText enterNewName, enterNewUsername,  enterNewPassword;
    static EditText enterNewEmail;
    TextView signInTitle;
    Button continueSignUp;
    public static String newEmail;
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
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            if(!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(SignUpScreen.this, e.getReason(),
                                            Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(SignUpScreen.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(SignUpScreen.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        } else if(task.isSuccessful()){

                            Intent intent = new Intent(SignUpScreen.this, ToolbarActivity.class);
                            startActivity(intent);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

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

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.continueWithSignUp:
                //newEmail = enterNewEmail.getText().toString();
                //startActivity(new Intent(SignUpScreen.this, VerificationCode.class));
                Log.v("click","creating new email-user");
                createAccount(enterNewEmail.getText().toString(), enterNewPassword.getText().toString());
        }
    }

    public static String getEmail() {
        return newEmail = enterNewEmail.getText().toString();
    }
}

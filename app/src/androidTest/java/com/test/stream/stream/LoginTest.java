package com.test.stream.stream;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.UI.MainLoginScreen;
import com.test.stream.stream.UI.ProjectsActivity;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;
/**
 * Created by robyn on 2016-11-07.
 */

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    static User user = null;

    /**
     * Sign in the user
     */
    @Before
    public void userSignInSetup(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // sign out of current possibly logged in user
        mAuth.signOut();
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                UserManager.sharedInstance().InitializeUser(new ReadDataCallback() {
                    @Override
                    public void onDataRetrieved(DataSnapshot result) {
                        AtomicBoolean once = new AtomicBoolean(false);
                        for (DataSnapshot child:result.getChildren()){
                            if (!once.getAndSet(true)) {
                                user = child.getValue(User.class);
                            }
                        }
                    }
                });
            }
        };
        mAuth.addAuthStateListener(listener);
        // login to test user
        mAuth.signInWithEmailAndPassword("unit@test.com","123456");
    }

    /**
     * Trigers a callable when a new user is added.
     *
     * @return Triggers the callable if a new user has been added. No trigger otherwise.
     */
    private Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }

    /**
     *  Ensure that the user logged in is the expected user.
     */
    @Test
    public void LoginTest () {
        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());

        assertEquals(user.getEmail(), "unit@test.com");
    }

    /**
     * Ensure that the user can successfully log out.
     */
    @Test
    public void LogoutTest (){

        UserManager.sharedInstance().logout();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        assertEquals(mAuth.getCurrentUser(), null);
    }
}

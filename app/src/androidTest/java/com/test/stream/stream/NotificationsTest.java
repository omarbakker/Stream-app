package com.test.stream.stream;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
/**
 * Created by robyn on 2016-11-07.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationsTest {

    static User user = null;

    @Before
    public void userSignInSetup(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

    @Before
    public void createProjectSetup(){

    }

    @Before
    public void createTaskSetup(){

        
    }

    /**
     * verifies that the notification is sent to server correctly
     */
    @Test
    public void sendNotificationTest(){

    }

    /**
     * verifies that the devices receive the notifcation correctly
     */
    @Test
    public void receiveNotificationTest(){

    }


}

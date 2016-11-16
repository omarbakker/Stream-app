package com.test.stream.stream;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.awaitility.Awaitility.await;

/**
 * Created by cathe on 2016-11-15.
 */

public class UserTest {


    User createdUser;
    private static FirebaseAuth mAuth;

    final String username = "test";
    final String name = "testName";
    final String email = "newuser@test.com";

    //User must be signed in to write to the database
    @Before
    public void userSignInSetup() {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
        // login to test user
        mAuth.signInWithEmailAndPassword(email, "123456");
    }


    private Callable<Boolean> userIsLoggedIn() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return mAuth.getCurrentUser() != null; // The condition that must be fulfilled
            }
        };
    }

    private void createUserInDatabase()
    {
        final AtomicBoolean userCreated = new AtomicBoolean(false);

        UserManager.createUserIfNotExist(username, name, email, new FetchUserCallback() {
            @Override
            public void onDataRetrieved(User result) {
                createdUser = result;
                userCreated.set(true);
            }
        });

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilTrue(userCreated);

    }

    private boolean deleteTestUser(String id)
    {
        //Get the database reference of a task
        DatabaseReference refToDelete = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Users.toString())
                .child(id);

        refToDelete.removeValue();

        return true;
    }

    private void initializeUserManager()
    {
        final AtomicBoolean userFound = new AtomicBoolean(false);

        UserManager.sharedInstance().InitializeUser(new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {
                if(result.exists())
                {
                    AtomicBoolean once = new AtomicBoolean(false);
                    for (DataSnapshot child : result.getChildren()) {
                        if (!once.getAndSet(true)) {
                            createdUser = child.getValue(User.class);
                            userFound.set(true);
                        }
                    }
                }
            }
        });

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilTrue(userFound);
    }


    /**
     * Confirm that a new user can be successfully created and can be used to initialize the user manager.
     * Note that only the email users are tested since we cannot properly test the Facebook SDK.
     */
    @Test
    public void createUserAndLogIn()
    {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(userIsLoggedIn());

        createUserInDatabase();
        initializeUserManager();
        UserManager.sharedInstance().logout();
        deleteTestUser(createdUser.getUid());
    }

    /**
     * Confirm that a the new user has correct information
     */
    @Test
    public void userDetails()
    {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(userIsLoggedIn());

        createUserInDatabase();
        initializeUserManager();

        assertEquals(createdUser.getUid(), mAuth.getCurrentUser().getUid());
        assertEquals(createdUser.getUsername(), username);
        assertEquals(createdUser.getName(), name);
        assertEquals(createdUser.getEmail(), email);

        UserManager.sharedInstance().logout();
        deleteTestUser(createdUser.getUid());
    }


    /**
     * Confirm that a the user is correctly logged out.
     */
    @Test
    public void logout()
    {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(userIsLoggedIn());

        createUserInDatabase();
        initializeUserManager();
        UserManager.sharedInstance().logout();

        assertNull(UserManager.sharedInstance().getCurrentUser());
        assertNull(mAuth.getCurrentUser());

        deleteTestUser(createdUser.getUid());
    }

    @Test
    public void fetchByUsername()
    {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(userIsLoggedIn());
        createUserInDatabase();

        final AtomicBoolean userFound = new AtomicBoolean(false);
        UserManager.sharedInstance().fetchUserByUserName(username, new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {
                AtomicBoolean once = new AtomicBoolean(false);
                for (DataSnapshot child : result.getChildren()) {
                    if (!once.getAndSet(true)) {
                        createdUser = child.getValue(User.class);
                        userFound.set(true);
                    }
                }
            }
        });

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilTrue(userFound);

        assertEquals(createdUser.getUid(), mAuth.getCurrentUser().getUid());
        assertEquals(createdUser.getUsername(), username);

        UserManager.sharedInstance().logout();
        deleteTestUser(createdUser.getUid());
    }


    @Test
    public void fetchByUid()
    {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(userIsLoggedIn());
        createUserInDatabase();

        final AtomicBoolean userFound = new AtomicBoolean(false);
        UserManager.sharedInstance().fetchUserByUid(mAuth.getCurrentUser().getUid(), new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {
                AtomicBoolean once = new AtomicBoolean(false);
                for (DataSnapshot child : result.getChildren()) {
                    if (!once.getAndSet(true)) {
                        createdUser = child.getValue(User.class);
                        userFound.set(true);
                    }
                }
            }
        });

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilTrue(userFound);

        assertEquals(createdUser.getUid(), mAuth.getCurrentUser().getUid());

        UserManager.sharedInstance().logout();
        deleteTestUser(createdUser.getUid());
    }








}

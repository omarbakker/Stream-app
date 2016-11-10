package com.test.stream.stream.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A manager that ensures that the logged in user is tracked and accessible to
 * all functionalities in the project. This class also handles user creation in the
 * database.
 *
 * Created by Catherine Lee on 2016-10-16.
 * Updated by Omar on 2016-10-28
 */

public class UserManager {
    private static UserManager instance = new UserManager();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private User currentUser;
    private String userKey;

    /**
     * Ensure that UserManager can only be instantiated within the class.
     */
    private UserManager(){};

    /**
     *
     * @return the only instance of this class (singleton)
     */
    public static UserManager getInstance(){ return instance; }

    /**
     *
     * @return the only instance of this class (singleton)
     */
    public User getCurrentUser()
    {
        return currentUser;
    }

    /**
     * Checks if the user is logged in via Firebase authentication
     *
     * @return true if the user is logged into Firebase, false otherwise
     */
    public boolean isUserLoggedin()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        return (mFirebaseUser != null);
    }

    /**
     * Fetches the user from the database
     *
     * @param callback An event triggered when the user has been intalized
     */
    public void InitializeUser(final ReadDataCallback callback)
    {
        if(!isUserLoggedin())
        {
            return;
        }

        if (currentUser == null) {
            DatabaseManager.getInstance().fetchObjectByChild(DatabaseFolders.Users, "uid", mFirebaseUser.getUid(), new ReadDataCallback() {
                @Override
                public void onDataRetrieved(DataSnapshot result) {
                    if(result.exists())
                    {
                        for(DataSnapshot user: result.getChildren())
                        {
                            currentUser = user.getValue(User.class);
                            userKey = user.getKey();
                        }

                        callback.onDataRetrieved(result);
                    }
                }
            });
        }
    }

    /**
     * Update the user object
     *
     * @param user The updated user object
     */
    public void updateUser(User user)
    {
        currentUser = user;
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Users, userKey, currentUser);

    }

    /**
     * Reset variables storing the user to null when the user logs out
     */
    public void logout()
    {
        mFirebaseAuth = null;
        mFirebaseUser = null;
        currentUser = null;
    }


    /**
     * Check if a username exists in the database
     * @param uDescription
     * A description for a stream username/email entered by the app user.
     */
    public void checkUserExists(String uDescription, final ReadDataCallback callback){
        DatabaseManager.getInstance().fetchObjectByChild(DatabaseFolders.Users, "username", uDescription,callback);
    }

    /**
     * Create a user object for a provided username and email
     * in the database if the current user is not tracked by the database.
     *
     * @param username the username selected by the user
     * @param email the user's email address
     */
    public static void createUserIfNotExist(String username, String name, String email)
    {
        FirebaseUser user = getFirebaseUser();

        if(user != null)
        {
            User newUser = new User();
            newUser.setName(name);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setUid(user.getUid());

            createUser(newUser);
        }
    }

    /**
     * Create a user object in the database if the current user is not tracked by the database,
     * assuming all user information is stored in the Firebase user
     */
    public static void createUserIfNotExist()
    {
        FirebaseUser user = getFirebaseUser();
        createUserIfNotExist(user.getDisplayName(), "", "");
    }

    /**
     * Get the user current logged into firebase
     *
     * @return the Firebae user object
     */
    private static FirebaseUser getFirebaseUser()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        return mFirebaseAuth.getCurrentUser();
    }

    /**
     * Create a user in the database for the given user object
     *
     * @param user the user to write to the database
     */
    private static void createUser(final User user)
    {
        DatabaseManager.getInstance().fetchObjectByChild(DatabaseFolders.Users, "uid", user.getUid(), new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {

                final AtomicBoolean oneUserHandled = new AtomicBoolean(false);

                if(!result.exists())
                {
                    if (!oneUserHandled.getAndSet(true)) {
                        DatabaseManager.getInstance().writeObject(DatabaseFolders.Users, user);
                    }
                }
            }
        });
    }



}
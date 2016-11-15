package com.test.stream.stream.Controllers;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;
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

    /**
     * Ensure that UserManager can only be instantiated within the class.
     */
    private UserManager(){};

    /**
     *
     * @return the only instance of this class (singleton)
     */
    public static UserManager sharedInstance(){ return instance; }

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
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Users, currentUser.getUid(), currentUser);

    }

    /**
     * Reset user manager when the user logs out.
     */
    public void logout()
    {
        for(UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData())
        {
            if(user.getProviderId().equals("facebook.com"))
            {
                LoginManager.getInstance().logOut();
            }
        }

        FirebaseAuth.getInstance().signOut();

        currentUser = null;
        mFirebaseAuth = null;
        mFirebaseUser = null;

        instance = new UserManager();
        ProjectManager.sharedInstance().destroy();

    }


    /**
     * Retrieves a user in the callback if the user exists
     * @param userName the username of the user to retrieve
     * A description for a stream username/email entered by the app user.
     */
    public void fetchUserByUserName(String userName, final ReadDataCallback callback){
        DatabaseManager.getInstance().fetchObjectByChild(DatabaseFolders.Users, "username", userName,callback);
    }

    /**
     * Retrieves a user in the callback if the user exists
     * @param uid the uid of the user
     * @param callback the callback to trigger when the user has been retrieved.
     */
    public void fetchUserByUid(String uid, final ReadDataCallback callback){
        DatabaseManager.getInstance().fetchObjectByKey(DatabaseFolders.Users, uid, callback);
    }

    /**
     * Create a user object for a provided username and email
     * in the database if the current user is not tracked by the database.
     *
     * @param username the username selected by the user
     * @param email the user's email address
     */
    public static void createUserIfNotExist(String username, String name, String email, final FetchUserCallback callback)
    {
        FirebaseUser user = getFirebaseUser();

        if(user != null)
        {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setUid(user.getUid());

            createUser(newUser, callback);
        }
    }

    /**
     * Create a user object in the database if the current user is not tracked by the database,
     * assuming all user information is stored in the Firebase user
     */
    public static void createUserIfNotExist(FetchUserCallback callback)
    {
        FirebaseUser user = getFirebaseUser();
        createUserIfNotExist(user.getDisplayName(), "", "", callback);
    }

    /**
     * Get the user current logged into firebase
     *
     * @return the Firebase user object
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
    private static void createUser(final User user, final FetchUserCallback callback)
    {
        DatabaseManager.getInstance().fetchObjectByChild(DatabaseFolders.Users, "uid", user.getUid(), new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {

                final AtomicBoolean oneUserHandled = new AtomicBoolean(false);

                if(!result.exists())
                {
                    if (!oneUserHandled.getAndSet(true)) {
                        DatabaseManager.getInstance().writeObjectWithKey(DatabaseFolders.Users, user.getUid(), user);
                        callback.onDataRetrieved(user);
                    }
                }
                else
                {
                    callback.onDataRetrieved(result.getValue(User.class));
                }
            }
        });
    }



}
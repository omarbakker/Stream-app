package com.test.stream.stream.Controllers;

import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.UI.MainLoginScreen;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;
import com.test.stream.stream.Utilities.ReadDataCallback;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.test.stream.stream.R.id.user;

/**
 * Created by cathe on 2016-10-16.
 * Updated by Omar on 2016-10-28
 */

public class UserManager {
    private static UserManager instance = new UserManager();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

   // final static AtomicBoolean oneUserHandled = new AtomicBoolean(false);

    private User currentUser;
    private String userKey;

    private UserManager(){};

    public static UserManager getInstance(){ return instance; }

    public User getCurrentUser()
    {
        return currentUser;
    }

    public boolean isUserLoggedin()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        return (mFirebaseUser != null);
    }

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

    public void updateUser(User user)
    {
        currentUser = user;
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Users, userKey, currentUser);

    }

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

    public static void createUserIfNotExist()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser == null)
        {
            return;
        }

        DatabaseManager.getInstance().fetchObjectByChild(DatabaseFolders.Users, "uid", mFirebaseUser.getUid(), new ReadDataCallback() {
            @Override
            public void onDataRetrieved(DataSnapshot result) {

                final AtomicBoolean oneUserHandled = new AtomicBoolean(false);

                if(!result.exists())
                {
                    if (!oneUserHandled.getAndSet(true)) {
                        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                        User newUser = new User();
                        newUser.setUid(mUser.getUid());
                        newUser.setName(mUser.getDisplayName());
                        DatabaseManager.getInstance().writeObject(DatabaseFolders.Users, newUser);
                   }
                }
            }
        });

    }


}
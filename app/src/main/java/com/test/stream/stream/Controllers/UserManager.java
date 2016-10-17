package com.test.stream.stream.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;

/**
 * Created by cathe on 2016-10-16.
 */

public class UserManager {
    private static UserManager instance = new UserManager();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private User currentUser;

    private UserManager(){};

    public static UserManager getInstance(){ return instance; }

    public boolean isUserLoggedin()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        return (mFirebaseUser != null);
    }

    public void getCurrentUser(final FetchUserCallback callback)
    {
        if(!isUserLoggedin())
        {
            return;
        }

        if(currentUser == null)
        {
            DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.Users.toString());
            Query query = myRef.orderByChild("uid").equalTo(mFirebaseUser.getUid());

            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    currentUser = dataSnapshot.getValue(User.class);
                    callback.onDataRetrieved(currentUser);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    currentUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            callback.onDataRetrieved(currentUser);
        }
    }

    public void logout()
    {
        mFirebaseAuth = null;
        mFirebaseUser = null;
        currentUser = null;
    }





}

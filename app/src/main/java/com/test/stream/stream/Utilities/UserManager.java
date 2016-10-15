package com.test.stream.stream.Utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Projects.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robyn on 2016-10-14.
 */

public class UserManager {
    private static UserManager instance = new UserManager();
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Map<String, String> projects =  new HashMap<>();

    private UserManager(){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        DatabaseReference userRef = databaseManager.getReference(this.userID);

        Query userQuery = userRef.orderByChild("projects");
        ChildEventListener userListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                projects.put(dataSnapshot.getKey(),(String)dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        };
        userQuery.addChildEventListener(userListener);
    };

    public static UserManager getInstance() {return instance;}

    /**
     *
     * @return
     */
    public String getUserID() {return userID; }

    /**
     *
     * @return
     */
    public Map<String, String> getProjects() {return new HashMap<String, String>(projects); }

}

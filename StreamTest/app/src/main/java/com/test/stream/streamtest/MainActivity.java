package com.test.stream.streamtest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.test.stream.streamtest.Objects.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textViewPersons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewPersons = (TextView) findViewById(R.id.textViewPersons);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child("Robyn");

        createUser("Test3", "test3@gmail.com", database);
        getUser("Test", database);

    }

/*Triggered functions*/

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

/*Database write functions*/

    /**
     * @param username
     * @param email
     * @param database
     */
    private static void createUser(String username, String email, FirebaseDatabase database) {
        DatabaseReference myRef = database.getReference("users");
        User user = new User(username, email);
        DatabaseReference newRef = myRef.push();
        newRef.setValue(user);
    }
/*Database read functions*/

    private void getUser(String username, FirebaseDatabase database){
        DatabaseReference myRef = database.getReference("users");
        Query usernameQuery = myRef.orderByChild("username").equalTo(username);

        usernameQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                textViewPersons.setText(user.getUsername());
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
        });
    }
}

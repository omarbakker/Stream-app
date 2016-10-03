package com.test.stream.stream.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textViewPersons;
    private String userKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewPersons = (TextView) findViewById(R.id.textViewPersons);

        //make a user
        User user = new User("Testfinish3gmailcom", "Finish3");
        String userKey = DatabaseManager.getInstance().writeObject("users", user);

        //make a project
        Project project = new Project();
        String projectKey = DatabaseManager.getInstance().writeObject("projects", project);
        project.CreateProject("project3", projectKey, user, userKey);

        String taskGroupKey = project.getTaskGroupId();
        Task task = new Task("task3", "taskdescription", new Date(), taskGroupKey);
        String taskKey = DatabaseManager.getInstance().writeObject("tasks", task);
        //TODO: Actually add this task to the correct taskGroupKey by retrieiving the taskgroup object with a read

        DatabaseManager.getInstance().updateObject("projects", projectKey, project);

        user.addProject(projectKey, project.getName());
        DatabaseManager.getInstance().updateObject("users", userKey, user);




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
     * @param uid
     * @param username
     */
    private static User createUser(String uid, String username) {

        User user = new User(uid, username);
        DatabaseManager.getInstance().writeObject("users", user);
        return user;
    }
/*Database read functions*
/*Unsure if this one should be extracted or not.
 */

    private void getUser(String uid){
        DatabaseReference myRef = DatabaseManager.getInstance().getReference("users");
        Query usernameQuery = myRef.orderByChild("uid").equalTo(uid);

        usernameQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                textViewPersons.setText(user.getUid() + dataSnapshot.getKey());
                userKey = dataSnapshot.getKey();
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

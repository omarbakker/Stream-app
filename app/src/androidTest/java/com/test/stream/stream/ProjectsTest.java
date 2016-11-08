package com.test.stream.stream;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.UI.MainLoginScreen;
import com.test.stream.stream.UI.ProjectsActivity;

import org.junit.*;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.awaitility.Awaitility.await;


/**
 * Created by robyn on 2016-11-07.
 * Tests ProjectManager ability to create and edit projects
 */
@RunWith(AndroidJUnit4.class)
public class ProjectsTest {

    FirebaseUser user = null;

    @Before
    public void userSignInSetup(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };
        mAuth.addAuthStateListener(listener);
        mAuth.signInWithEmailAndPassword("unit@test.com","123456");
    }

    private Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }

    @Test
    public void addProject() throws Exception {

        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com",user.getEmail());


    }

    @Test
    public void deleteProject() throws Exception {

    }

    @Test
    public void joinProject() throws Exception {

    }

    @Test
    public void editProject() throws Exception {

    }

}

package com.test.stream.stream;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.UI.MainLoginScreen;
import com.test.stream.stream.UI.ProjectsActivity;
import com.test.stream.stream.Utilities.Callbacks.FetchUserProjectsCallback;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;

import org.junit.*;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.awaitility.Awaitility.await;


/**
 * Created by robyn on 2016-11-07.
 * Tests ProjectManager ability to create and edit projects
 * Implemented by omarBakker
 */
@RunWith(AndroidJUnit4.class)
public class ProjectsTest {

    static User user = null;

    @Before
    public void userSignInSetup(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // sign out of current possibly logged in user
        mAuth.signOut();
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                UserManager.getInstance().InitializeUser(new ReadDataCallback() {
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

    private Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }


    @Test
    public void verifySignedIn(){
        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com",user.getEmail());
        assertEquals(user.getEmail(),UserManager.getInstance().getCurrentUser().getEmail());
    }

    @Test
    public void addProject() throws Exception {

        ProjectManager manager = ProjectManager.sharedInstance();
        Project testProject = new Project();
        testProject.setName("Test Project");
        testProject.addMember(user,true);
        testProject.setDueDate(12,12,2012);
        manager.CreateProject(testProject);

        final AtomicBoolean projectFetched = new AtomicBoolean(false);
        final List<Project> fetchedProjectList = new ArrayList<>();
        manager.fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                projectFetched.set(true);
                fetchedProjectList.addAll(projects);
            }
        });
        await().atMost(10,TimeUnit.SECONDS).untilTrue(projectFetched);
        assertEquals(testProject.getName(),fetchedProjectList.get(0).getName());
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

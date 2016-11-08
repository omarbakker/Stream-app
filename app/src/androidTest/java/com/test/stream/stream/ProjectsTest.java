package com.test.stream.stream;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.primitives.Booleans;
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
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

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
    static List<Project> fetchedProjectList;

    @Before
    public void userSignInSetup(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                UserManager.sharedInstance().InitializeUser(new ReadDataCallback() {
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
        assertEquals(user.getEmail(),UserManager.sharedInstance().getCurrentUser().getEmail());
    }

    @Test
    public void addProject() throws Exception {

        ProjectManager manager = ProjectManager.sharedInstance();
        int initialNumberOfProjects;

        // Get the initial list of projects
        final AtomicBoolean projectFetched1 = new AtomicBoolean(false);
        fetchedProjectList = new ArrayList<>();
        manager.fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                projectFetched1.set(true);
                fetchedProjectList.addAll(projects);
            }
        });
        await().atMost(10,TimeUnit.SECONDS).untilTrue(projectFetched1);
        initialNumberOfProjects = fetchedProjectList.size();

        // Create a new project
        Project testProject = new Project();
        testProject.setName("Test Project");
        testProject.addMember(user,true);
        testProject.setDueDate(12,12,2012);

        // add that project to the database
        manager.CreateProject(testProject);

        // update the projects list
        final AtomicBoolean projectFetched2 = new AtomicBoolean(false);
        fetchedProjectList = new ArrayList<>();
        manager.fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                projectFetched2.set(true);
                fetchedProjectList.addAll(projects);
            }
        });
        await().atMost(10,TimeUnit.SECONDS).untilTrue(projectFetched2);

        // verify the new project was added to the database
        assertEquals(initialNumberOfProjects+1,fetchedProjectList.size());
    }


    @Test
    public void joinProject() throws Exception {

        ProjectManager manager = ProjectManager.sharedInstance();

        // Get the initial list of projects
        final AtomicBoolean projectFetched1 = new AtomicBoolean(false);
        fetchedProjectList = new ArrayList<>();
        manager.fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                projectFetched1.set(true);
                fetchedProjectList.addAll(projects);
            }
        });
        await().atMost(10,TimeUnit.SECONDS).untilTrue(projectFetched1);
        Project projectToTest = fetchedProjectList.get(0);

        // create a test user
        User newMember = new User();
        newMember.setUid("AioSdhNNXSM86kxnsZDPwThlbF02");

        // add the test user to the dataBase
        projectToTest.addMember(newMember,true);

        // update the database
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,projectToTest.getId(),projectToTest);

        // Update list of projects
        final AtomicBoolean projectFetched2 = new AtomicBoolean(false);
        fetchedProjectList = new ArrayList<>();
        manager.fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                projectFetched2.set(true);
                fetchedProjectList.addAll(projects);
            }
        });
        await().atMost(10,TimeUnit.SECONDS).untilTrue(projectFetched2);

        // Assert user was added
        Project projectAfterTest = fetchedProjectList.get(0);
        assertEquals(true,projectAfterTest.getMembers().containsKey(newMember.getUid()));

        // Assert the user was added as an admin
        boolean isAdmin = projectAfterTest.getMembers().get(newMember.getUid());
        assertEquals(true,isAdmin);

    }

    @Test
    public void editProject() throws Exception {
        ProjectManager manager = ProjectManager.sharedInstance();

        // Get the initial list of projects
        final AtomicBoolean projectFetched1 = new AtomicBoolean(false);
        fetchedProjectList = new ArrayList<>();
        manager.fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                projectFetched1.set(true);
                fetchedProjectList.addAll(projects);
            }
        });
        await().atMost(10,TimeUnit.SECONDS).untilTrue(projectFetched1);
        Project projectToTest = fetchedProjectList.get(0);

        // get the initial project numOfActiveTasks
        int initialNumOfActiveTasks = projectToTest.getNumberOfActiveTasks();

        // add the test user to the dataBase
        projectToTest.setNumberOfActiveTasks(initialNumOfActiveTasks + 1);

        // update the database
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,projectToTest.getId(),projectToTest);

        // Update list of projects
        final AtomicBoolean projectFetched2 = new AtomicBoolean(false);
        fetchedProjectList = new ArrayList<>();
        manager.fetchCurrentUserProjects(new FetchUserProjectsCallback() {
            @Override
            public void onUserProjectsListRetrieved(List<Project> projects) {
                projectFetched2.set(true);
                fetchedProjectList.addAll(projects);
            }
        });
        await().atMost(10,TimeUnit.SECONDS).untilTrue(projectFetched2);

        // Assert that the number of active tasks was updated correctly
        Project projectAfterTest = fetchedProjectList.get(0);
        assertEquals(initialNumOfActiveTasks+1, projectAfterTest.getNumberOfActiveTasks());

    }


    // Unimplemented
    @Test
    public void deleteProject() throws Exception {

    }



}

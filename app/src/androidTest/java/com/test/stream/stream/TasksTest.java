package com.test.stream.stream;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.UIFragments.expand_task;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.google.android.gms.common.stats.zzd.Eq;
import static junit.framework.Assert.assertEquals;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by robyn on 2016-11-07.
 */
@RunWith(AndroidJUnit4.class)
public class TasksTest {

    List<Task> tasks = null;
    AtomicBoolean AddedTask = new AtomicBoolean(false);
    AtomicBoolean MarkedAsComplete = new AtomicBoolean(false);

    //test data
    String test_name = "test name";
    String test_description = "this is a test description";
    String test_newTaskAssignee = "john jingleheimer schmidt";
    int[] test_DueDate = {12,12,2012};
    boolean complete = false;

    static User user = null;

    @Before
    public void userSignInSetup() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                UserManager.sharedInstance().InitializeUser(new ReadDataCallback() {

                    @Override
                    public void onDataRetrieved(DataSnapshot result) {
                        AtomicBoolean once = new AtomicBoolean(false);
                        for (DataSnapshot child : result.getChildren()) {
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
        mAuth.signInWithEmailAndPassword("unit@test.com", "123456");
    }

    private Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }


    @Test
    public void verifySignedIn() {
        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com", user.getEmail());
        assertEquals(user.getEmail(), UserManager.sharedInstance().getCurrentUser().getEmail());
    }


    /**
     * Determine if the task is marked as complete in the database
     *Creating a new task with the given data and asserts a new task has been added to the database
     */
    @Test
    public void initialize_task(){
        Project project = new Project();
        project.setTaskGroupId("-KW10RkZhInwcdn-hVIw");
        ProjectManager.sharedInstance().setCurrentProject(project);

        // set up the change listener
        final AtomicInteger taskCount = new AtomicInteger(0);
        final AtomicBoolean dataChanged = new AtomicBoolean(false);

        TaskManager.sharedInstance().Initialize(new DataEventListener() {
            @Override
            public void onDataChanged() {
                tasks = TaskManager.sharedInstance().GetTasksInProject();
                dataChanged.set(true);
                taskCount.set(tasks.size());
            }
        });

        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);
        dataChanged.set(false);
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, test_newTaskAssignee, test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }


    /**
     * Delete a task and then ensure the task was deleted
     * @throws Exception
     */
    @Test
    public void deleteTask() throws Exception {

        Project project = new Project();
        project.setTaskGroupId("-KW10RkZhInwcdn-hVIw");
        ProjectManager.sharedInstance().setCurrentProject(project);

        // set up the change listener
        final AtomicInteger taskCount = new AtomicInteger(0);
        final AtomicBoolean dataChanged = new AtomicBoolean(false);

        TaskManager.sharedInstance().Initialize(new DataEventListener() {
            @Override
            public void onDataChanged() {
                tasks = TaskManager.sharedInstance().GetTasksInProject();
                dataChanged.set(true);
                taskCount.set(tasks.size());
            }
        });

        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);
        dataChanged.set(false);
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Task taskToDelete = tasks.get(0);
        int initialTaskCount = tasks.size();

        boolean deleted = TaskManager.sharedInstance().DeleteTask(taskToDelete);
        assert(deleted);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount - 1));

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }


    @Test
    public void taskDetailsTest() throws Exception{

        Project project = new Project();
        project.setTaskGroupId("-KW10RkZhInwcdn-hVIw");
        ProjectManager.sharedInstance().setCurrentProject(project);

        // set up the change listener
        final AtomicInteger taskCount = new AtomicInteger(0);
        final AtomicBoolean dataChanged = new AtomicBoolean(false);

        TaskManager.sharedInstance().Initialize(new DataEventListener() {
            @Override
            public void onDataChanged() {
                tasks = TaskManager.sharedInstance().GetTasksInProject();
                dataChanged.set(true);
                taskCount.set(tasks.size());
            }
        });

        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);
        dataChanged.set(false);
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, test_newTaskAssignee, test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // we have now guaranteed that a new task exists.
        // Test the correctness of the details of the task.
        assertEquals(test_name,fetchedNewTask.getName());
        //assertEquals(test_newTaskAssignee,fetchedNewTask.getAssigneeUid());
        assertEquals(test_DueDate[0],fetchedNewTask.getDueDay());
        assertEquals(complete,fetchedNewTask.getComplete());

        // Modify the task details and update
        fetchedNewTask.setComplete(!fetchedNewTask.getComplete());
        boolean updatedCompleteValue = fetchedNewTask.getComplete();
        dataChanged.set(false);

        // update the database
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Tasks,fetchedNewTask.getId(),fetchedNewTask);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Task updatedTask = tasks.get(tasks.size() - 1);
        assertEquals(updatedCompleteValue, updatedTask.getComplete());

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();

    }

}

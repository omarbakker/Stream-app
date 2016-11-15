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
import static junit.framework.Assert.assertNotNull;
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
    int[] test_DueDate = {12,12,2012};
    boolean complete = false;

    //data to modify to
    String test_name2 = "unit test";
    String test_description2 = "new test description";
    int[] test_DueDate2 = {15,10,2014};

    static User user = null;

     //User must be signed in to write to the database
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

    @Before
    public void setProject()
    {
        Project project = new Project();
        project.setId("-KW_lArl8Gz3u6mqljKx");
        project.setTaskGroupId("-KW_lArtkBK80ukIal2w");
        ProjectManager.sharedInstance().setCurrentProject(project);
    }

    private Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }


    /**
     * @return A user for testing with
     */
    private User getUser()
    {
        User user = new User();
        user.setName("unit");
        user.setEmail("unit@test.com");
        user.setUsername("unit");
        user.setUid("g1tnS1lCYtcBvCHjnzgSBApLG082");

        return user;
    }

    /**
     * A second user to swap with the first
     * @return
     */
    private User getUser2()
    {
        User user = new User();
        user.setName("unity");
        user.setEmail("unity@test.com");
        user.setUsername("unity");
        user.setUid("unituid");

        return user;
    }

    /**
     * Confirms that TaskManager has been successfully initialized.
     */
    private void initializeTaskManager(final AtomicInteger taskCount,
                                       final AtomicInteger dataChangeCount, final AtomicBoolean dataChanged)
    {

        //Confirm initialization
        TaskManager.sharedInstance().Initialize(new DataEventListener() {
            @Override
            public void onDataChanged() {
                tasks = TaskManager.sharedInstance().GetTasksInProject();
                dataChangeCount.incrementAndGet();
                dataChanged.set(true);
                taskCount.set(tasks.size());
            }
        });

        await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(2));
    }


    @Test
    public void verifySignedIn() {
        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com", user.getEmail());
        assertEquals(user.getEmail(), UserManager.sharedInstance().getCurrentUser().getEmail());
    }

    /**
     * Determine if we can successfully add a task to the database.
     */
    @Test
    public void addTask(){

        AtomicInteger taskCount = new AtomicInteger(0);
        initializeTaskManager(taskCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Check if creating a task is successful
        int initialTaskCount = tasks.size();
        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description,
                        getUser(),
                        test_DueDate, complete);
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

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        initializeTaskManager(taskCount, new AtomicInteger(0), new AtomicBoolean(false));

        //Get a task to delete
        Task taskToDelete = tasks.get(0);
        int initialTaskCount = tasks.size();

        //Delete the task
        boolean deleted = TaskManager.sharedInstance().DeleteTask(taskToDelete);
        assert(deleted);

        // assert the task was deleted;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount - 1));

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }


    /**
     * Confirm that the task's contents are the expected values.
     * @throws Exception
     */
    @Test
    public void taskDetails() throws Exception{

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeTaskManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getUser(), test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // we have now guaranteed that a new task exists.
        // Test the correctness of the details of the task.
        assertEquals(test_name,fetchedNewTask.getName());
        assertEquals(test_DueDate[0],fetchedNewTask.getDueDay());
        assertEquals(test_DueDate[1],fetchedNewTask.getDueMonth());
        assertEquals(test_DueDate[2],fetchedNewTask.getDueYear());
        assertEquals(complete,fetchedNewTask.getComplete());


        //Delete the task
        boolean deleted = TaskManager.sharedInstance().DeleteTask(fetchedNewTask);
        assert(deleted);


        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }

    /**
     * Confirm that the task's contents can be successfully set as complete.
     * @throws Exception
     */
    @Test
    public void editTask() throws Exception{

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeTaskManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getUser(), test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // Modify the task details and update
        fetchedNewTask.setName(test_name2);
        fetchedNewTask.setDescription(test_description2);
        fetchedNewTask.setDueDay(test_DueDate2[0]);
        fetchedNewTask.setDueMonth(test_DueDate2[1]);
        fetchedNewTask.setDueYear(test_DueDate2[2]);

        dataChanged.set(false);

        // update the database
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Tasks,fetchedNewTask.getId(),fetchedNewTask);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Task updatedTask = tasks.get(tasks.size() - 1);

        // Test the correctness of the details of the updated task.
        assertEquals(test_name2,fetchedNewTask.getName());
        assertEquals(test_DueDate2[0],fetchedNewTask.getDueDay());
        assertEquals(test_DueDate2[1],fetchedNewTask.getDueMonth());
        assertEquals(test_DueDate2[2],fetchedNewTask.getDueYear());


        //Delete the task
        boolean deleted = TaskManager.sharedInstance().DeleteTask(updatedTask);
        assert(deleted);

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }

    @Test
    public void setTaskCompletion()
    {
        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeTaskManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getUser(), test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        assertEquals(fetchedNewTask.getComplete(), complete);

        // Modify the task details and update
        fetchedNewTask.setComplete(!complete);
        dataChanged.set(false);

        // update the database
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Tasks,fetchedNewTask.getId(),fetchedNewTask);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Task updatedTask = tasks.get(tasks.size() - 1);
        assertEquals(!complete, updatedTask.getComplete());

        //Delete the task
        boolean deleted = TaskManager.sharedInstance().DeleteTask(updatedTask);
        assert(deleted);

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }

    @Test
    public void assignTask()
    {
        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeTaskManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getUser(), test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        //Confirm that the user is the expected user
        assertEquals(fetchedNewTask.getAssignee(), getUser().getUsername());
        assertEquals(fetchedNewTask.getAssigneeUid(), getUser().getUid());

        //Delete the task
        boolean deleted = TaskManager.sharedInstance().DeleteTask(fetchedNewTask);
        assert(deleted);

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }

    @Test
    public void changeTaskAssignee()
    {
        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeTaskManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getUser(), test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // Modify the task details and update
        fetchedNewTask.setUser(getUser2());
        dataChanged.set(false);

        // update the database
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Tasks,fetchedNewTask.getId(),fetchedNewTask);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Task updatedTask = tasks.get(tasks.size() - 1);
        assertEquals(getUser2().getUsername(), updatedTask.getAssignee());
        assertEquals(getUser2().getUid(), updatedTask.getAssigneeUid());

        //Delete the task
        boolean deleted = TaskManager.sharedInstance().DeleteTask(updatedTask);
        assert(deleted);

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }

}

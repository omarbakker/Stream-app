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
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.google.android.gms.common.stats.zzd.Eq;
import static com.test.stream.stream.ProjectsTest.mAuth;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TasksTest {
    //Information one test task.
    String test_name = "test name";
    String test_description = "this is a test description";
    int[] test_DueDate = {12,12,2012};
    boolean complete = false;

    //Data to modify the task to or to act as a second task.
    String test_name2 = "unit test";
    String test_description2 = "new test description";
    int[] test_DueDate2 = {15,10,2014};

    //Global variables for tests
    static User user = null;
    List<Task> tasks = null;

    /**
     * Sign in the user before writing anything to the database
     * so that the user will have write permission.
     */
    @BeforeClass
    public static void userSignInSetup() {

        mAuth = FirebaseAuth.getInstance();
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

        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com", user.getEmail());
        assertEquals(user.getEmail(), UserManager.sharedInstance().getCurrentUser().getEmail());
    }

    /**
     * Manually set the project board ID to access a stub project.
     */
    @Before
    public void setProject()
    {
        Project project = new Project();
        project.setName("tasktest");
        project.setId("-KWfxqsLbZW-0ROkhVMR");
        project.setTaskGroupId("-KWfxqsShX1zpFghWY8U");
        project.setCalendarId("-KWfxqsWkNIaUGRj24B6");
        project.setBoardId("-KWfxqsQwa84ZR1eVQsa");
        ProjectManager.sharedInstance().setCurrentProject(project);
    }

    /**
     * Confirm that the user has been added.
     * @return a callable object that will trigger a callback when
     * the user successfully logs into Stream.
     */
    private static Callable<Boolean> newUserIsAdded() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return user != null; // The condition that must be fulfilled
            }
        };
    }

    /**
     * @return The test stub user.
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
     * @return A user other than the current logged in user to test with
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
     * Confirm that TaskManager has been successfully initialized from the current project.
     * @param taskCount An integer that reflects the number of tasks in the project.
     * @param dataChangeCount An integer that reflects the number of times data has been updated,
     *                        according to the listener.
     * @param dataChanged A boolean that confirms that data has been changed.
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

        await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(1));
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
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, equalTo(initialTaskCount + 1));

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }


    /**
     * Delete a task and then ensure the task was deleted
     */
    @Test
    public void deleteTask(){

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
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, equalTo(initialTaskCount - 1));

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }


    /**
     * Confirm that the task's contents are the expected values.
     */
    @Test
    public void taskDetails(){

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeTaskManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getUser(), test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // we have now guaranteed that a new task exists.
        // Test the correctness of the details of the task.
        assertEquals(ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId(), fetchedNewTask.getTaskGroupId());
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
     */
    @Test
    public void editTask(){

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        initializeTaskManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getUser(), test_DueDate, complete);
        assert(created);

        // assert the task was created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // Modify the task details and update
        fetchedNewTask.setName(test_name2);
        fetchedNewTask.setDescription(test_description2);
        fetchedNewTask.setDueDay(test_DueDate2[0]);
        fetchedNewTask.setDueMonth(test_DueDate2[1]);
        fetchedNewTask.setDueYear(test_DueDate2[2]);

        dataChanged.set(false);

        // update the database
        TaskManager.sharedInstance().UpdateTask(fetchedNewTask);

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

    /**
     * Confirm that tasks can be set as complete.
     */
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
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        assertEquals(fetchedNewTask.getComplete(), complete);

        // Modify the task details and update
        fetchedNewTask.setComplete(!complete);
        dataChanged.set(false);

        // update the database
        TaskManager.sharedInstance().UpdateTask(fetchedNewTask);

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

    /**
     * Confirm that tasks are correctly assigned.
     */
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
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, equalTo(initialTaskCount + 1));
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

    /**
     * Confirm that the task's assignee can be successfully changed.
     */
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
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, equalTo(initialTaskCount + 1));
        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // Modify the task details and update
        fetchedNewTask.setUser(getUser2());
        dataChanged.set(false);

        // update the database
        TaskManager.sharedInstance().UpdateTask(fetchedNewTask);

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

    /**
     * Log the current user out after the test.
     */
    @AfterClass
    public static void clean()
    {
        mAuth.signOut();
    }
}

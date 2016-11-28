package com.test.stream.stream;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.HomeManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.test.stream.stream.ProjectsTest.mAuth;
import static junit.framework.Assert.assertEquals;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by cathe on 2016-11-14.
 */

public class HomeTest {
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
        project.setName("hometest");
        project.setId("-KWfzMZHcllR5fiuMn1F");
        project.setTaskGroupId("-KWfzMZOwa1oMbESsmOx");
        project.setCalendarId("-KWfzMZRl9ClFs-tk6HD");
        project.setBoardId("-KWfzMZLXqaoVXYcJSWZ");
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
     * @return A user other than the current logged in user to test with
     */
    private User getOtherUser()
    {
        User user = new User();
        user.setName("unity");
        user.setEmail("unity@test.com");
        user.setUsername("unity");
        user.setUid("unituid");

        return user;
    }

    /**
     * Confirm that HomeManager has been successfully initialized from the current project.
     * @param taskCount An integer that reflects the number of tasks assigned to the current user in the project.
     * @param dataChangeCount An integer that reflects the number of times data has been updated,
     *                        according to the listener.
     * @param dataChanged A boolean that confirms that data has been changed.
     */
    private void initializeHomeManager(final AtomicInteger taskCount, final AtomicInteger dataChangeCount, final AtomicBoolean dataChanged)
    {
       HomeManager.sharedInstance().setUIListener(new DataEventListener() {
           @Override
           public void onDataChanged() {
               tasks = HomeManager.sharedInstance().getUserTasks();
               dataChangeCount.incrementAndGet();
               dataChanged.set(true);
               taskCount.set(tasks.size());
           }
       });

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAtomic(dataChangeCount, Matchers.equalTo(1));
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

        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, Matchers.equalTo(1));
    }

    /**
     * Helper function to delete all tasks associated with this test.
     */
    private void clearTasks()
    {
        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        initializeTaskManager(taskCount, new AtomicInteger(0), new AtomicBoolean(false));

        for(Task taskToDelete: tasks)
        {
            //Delete the task
            boolean deleted = TaskManager.sharedInstance().DeleteTask(taskToDelete);
            assert(deleted);
        }

        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, Matchers.equalTo(0));

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }

    /**
     * Confirm that only the user's own tasks, and not all tasks of the project are
     * fetched for the Home Screen
     */
    @Test
    public void verifyOnlyOwnTasks()
    {
        //Start with no tasks
        clearTasks();

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);

        //Add two tasks, one to the currently logged in user, one to another user.
        initializeTaskManager(taskCount, new AtomicInteger(0), new AtomicBoolean(false));
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, UserManager.sharedInstance().getCurrentUser(), test_DueDate, complete);
        assert(created);

        boolean created2 = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, getOtherUser(), test_DueDate, complete);

        assert(created2);

        // assert both tasks were created;
        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, Matchers.equalTo(initialTaskCount + 2));

        TaskManager.sharedInstance().Destroy();

        AtomicInteger dataChangeCount = new AtomicInteger(0);
        initializeHomeManager(new AtomicInteger(0), dataChangeCount, new AtomicBoolean(false));

        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, Matchers.equalTo(1));

        assertEquals(HomeManager.sharedInstance().getUserTasks().size(), 1);

        //Destroy home manager
        HomeManager.sharedInstance().Destroy();
    }

    /**
     * Helper function to create a task for the user.
     * @param isComplete true if the task to create is a completed task. False otherwise.
     * @param taskCount Will be the number of tasks in the project.
     */
    private void createUserTask(Boolean isComplete, AtomicInteger taskCount)
    {
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, UserManager.sharedInstance().getCurrentUser(), test_DueDate, isComplete);
        assert(created);

        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, Matchers.equalTo(initialTaskCount + 1));
    }

    /**
     * Check that the HomeManager correctly calculates the user's progress.
     */
    @Test
    public void verifyUserProgress()
    {
        //Start with no tasks
        clearTasks();

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);

        //Add two tasks, one to the currently logged in user, one to another user.
        initializeTaskManager(taskCount, new AtomicInteger(0), new AtomicBoolean(false));

        for(int i = 0; i < 2; i++)
        {
            createUserTask(true, taskCount);
        }

        for(int i = 0; i < 3; i++)
        {
            createUserTask(false, taskCount);
        }


        TaskManager.sharedInstance().Destroy();

        AtomicInteger dataChangeCount = new AtomicInteger(0);
        initializeHomeManager(new AtomicInteger(0), dataChangeCount, new AtomicBoolean(false));

        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, Matchers.equalTo(1));

        int expectedProgress = (int)((2.0/5.0)*100);
        assertEquals(HomeManager.sharedInstance().getUserProgress(), expectedProgress);

        //Destroy home manager
        HomeManager.sharedInstance().Destroy();
    }


    /**
     * Confirm that the HomeManager will allow tasks to be correctly added.
     */
    @Test
    public void addTask()
    {
        //Start with no tasks
        clearTasks();

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);

        //Add two tasks, one to the currently logged in user, one to another user.
        initializeHomeManager(taskCount, new AtomicInteger(0), new AtomicBoolean(false));
        int initialTaskCount = tasks.size();

        boolean created = HomeManager.sharedInstance().CreateTask(test_name, test_description, test_DueDate);
        assert(created);

        // assert both tasks were created;
        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, Matchers.equalTo(initialTaskCount + 1));

        Task newestTask = tasks.get(tasks.size()-1);

        assertEquals(newestTask.getAssignee(), UserManager.sharedInstance().getCurrentUser().getUsername());
        assertEquals(newestTask.getAssigneeUid(), UserManager.sharedInstance().getCurrentUser().getUid());
        assertEquals(newestTask.getTaskGroupId(), ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());
        assertEquals(newestTask.getName(), test_name);
        assertEquals(newestTask.getDescription(), test_description);
        assertEquals(newestTask.getDueDay(), test_DueDate[0]);
        assertEquals(newestTask.getDueMonth(), test_DueDate[1]);
        assertEquals(newestTask.getDueYear(), test_DueDate[2]);

        HomeManager.sharedInstance().Destroy();

    }

    /**
     * Confirm that tasks can be edited from the HomeManager.
     */
    @Test
    public void editTask()
    {
        //Start with no tasks
        clearTasks();

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        //Add two tasks, one to the currently logged in user, one to another user.
        initializeHomeManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = HomeManager.sharedInstance().CreateTask(test_name, test_description, test_DueDate);
        assert(created);

        // assert both tasks were created;
        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, Matchers.equalTo(initialTaskCount + 1));

        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // Modify the task details and update
        fetchedNewTask.setDescription(test_description2);
        fetchedNewTask.setName(test_name2);
        fetchedNewTask.setDueDay(test_DueDate2[0]);
        fetchedNewTask.setDueMonth(test_DueDate2[1]);
        fetchedNewTask.setDueYear(test_DueDate2[2]);

        dataChanged.set(false);
        HomeManager.sharedInstance().UpdateTask(fetchedNewTask);

        // wait for the listener to receive an update
        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Task newestTask = tasks.get(tasks.size()-1);
        assertEquals(newestTask.getAssignee(), UserManager.sharedInstance().getCurrentUser().getUsername());
        assertEquals(newestTask.getAssigneeUid(), UserManager.sharedInstance().getCurrentUser().getUid());
        assertEquals(newestTask.getTaskGroupId(), ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());
        assertEquals(newestTask.getName(), test_name2);
        assertEquals(newestTask.getDescription(), test_description2);
        assertEquals(newestTask.getDueDay(), test_DueDate2[0]);
        assertEquals(newestTask.getDueMonth(), test_DueDate2[1]);
        assertEquals(newestTask.getDueYear(), test_DueDate2[2]);

        HomeManager.sharedInstance().Destroy();

    }

    /**
     * Confirm that tasks can be marked as complete.
     */
    @Test
    public void markTaskComplete()
    {
        //Start with no tasks
        clearTasks();

        // set up the change listener
        AtomicInteger taskCount = new AtomicInteger(0);
        AtomicBoolean dataChanged = new AtomicBoolean(false);

        //Add two tasks, one to the currently logged in user, one to another user.
        initializeHomeManager(taskCount, new AtomicInteger(0), dataChanged);
        int initialTaskCount = tasks.size();

        boolean created = HomeManager.sharedInstance().CreateTask(test_name, test_description, test_DueDate);
        assert(created);

        // assert both tasks were created;
        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount, Matchers.equalTo(initialTaskCount + 1));

        Task fetchedNewTask = tasks.get(tasks.size() - 1);
        fetchedNewTask.setComplete(true);

        dataChanged.set(false);
        HomeManager.sharedInstance().UpdateTask(fetchedNewTask);

        // wait for the listener to receive an update
        Awaitility.await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        assertEquals(tasks.size(), initialTaskCount);

        HomeManager.sharedInstance().Destroy();

    }

    /**
     * Sign the user out after all tests.
     */
    @AfterClass
    public static void clean()
    {
        mAuth.signOut();
    }

}

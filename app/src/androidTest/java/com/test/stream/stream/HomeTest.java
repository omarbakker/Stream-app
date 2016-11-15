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

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by cathe on 2016-11-14.
 */

public class HomeTest {

    List<Task> tasks = null;

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
    HomeManager mHomeManager = null;

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
        project.setId("-KWdhJigIJsWVOmwuCq4");
        project.setTaskGroupId("-KWdhJilzL_US-FLGAYR");
        project.setCalendarId("-KWdhJioNliFxIYgWADC");
        project.setBoardId("-KWdhJijucj4wGTe2zvj");
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
     * Confirms that TaskManager has been successfully initialized.
     */
    private void initializeHomeManager(final AtomicInteger taskCount, final AtomicInteger dataChangeCount, final AtomicBoolean dataChanged)
    {
        mHomeManager = new HomeManager(new DataEventListener() {
            @Override
            public void onDataChanged() {
                tasks = mHomeManager.getUserTasks();
                dataChangeCount.incrementAndGet();
                dataChanged.set(true);
                taskCount.set(tasks.size());
            }
        });

        await().atMost(10, TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(1));
    }

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


    @Test
    public void verifySignedIn() {
        await().atMost(10, TimeUnit.SECONDS).until(newUserIsAdded());
        assertEquals("unit@test.com", user.getEmail());
        assertEquals(user.getEmail(), UserManager.sharedInstance().getCurrentUser().getEmail());
    }


    public void clearTasks()
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

        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(0));

        // deregister all listeners
        TaskManager.sharedInstance().Destroy();
    }

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
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 2));

        TaskManager.sharedInstance().Destroy();

        AtomicInteger dataChangeCount = new AtomicInteger(0);
        initializeHomeManager(new AtomicInteger(0), dataChangeCount, new AtomicBoolean(false));

        await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(1));

        assertEquals(mHomeManager.getUserTasks().size(), 1);

        //Destroy home manager
        mHomeManager.Destroy();
    }

    public void createUserTask(Boolean isComplete, AtomicInteger taskCount)
    {
        int initialTaskCount = tasks.size();

        boolean created = TaskManager.sharedInstance()
                .CreateTask(test_name, test_description, UserManager.sharedInstance().getCurrentUser(), test_DueDate, isComplete);
        assert(created);

        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));
    }

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

        await().atMost(10,TimeUnit.SECONDS).untilAtomic(dataChangeCount, equalTo(1));

        int expectedProgress = (int)((2.0/5.0)*100);
        assertEquals(mHomeManager.getUserProgress(), expectedProgress);

        //Destroy home manager
        mHomeManager.Destroy();
    }


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

        boolean created = mHomeManager.CreateTask(test_name, test_description, test_DueDate);
        assert(created);

        // assert both tasks were created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));

        Task newestTask = tasks.get(tasks.size()-1);

        assertEquals(newestTask.getAssignee(), UserManager.sharedInstance().getCurrentUser().getUsername());
        assertEquals(newestTask.getAssigneeUid(), UserManager.sharedInstance().getCurrentUser().getUid());
        assertEquals(newestTask.getTaskGroupId(), ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());
        assertEquals(newestTask.getName(), test_name);
        assertEquals(newestTask.getDescription(), test_description);
        assertEquals(newestTask.getDueDay(), test_DueDate[0]);
        assertEquals(newestTask.getDueMonth(), test_DueDate[1]);
        assertEquals(newestTask.getDueYear(), test_DueDate[2]);

        mHomeManager.Destroy();

    }


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

        boolean created = mHomeManager.CreateTask(test_name, test_description, test_DueDate);
        assert(created);

        // assert both tasks were created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));

        Task fetchedNewTask = tasks.get(tasks.size() - 1);

        // Modify the task details and update
        fetchedNewTask.setDescription(test_description2);
        fetchedNewTask.setName(test_name2);
        fetchedNewTask.setDueDay(test_DueDate2[0]);
        fetchedNewTask.setDueMonth(test_DueDate2[1]);
        fetchedNewTask.setDueYear(test_DueDate2[2]);

        dataChanged.set(false);
        mHomeManager.UpdateTask(fetchedNewTask);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        Task newestTask = tasks.get(tasks.size()-1);
        assertEquals(newestTask.getAssignee(), UserManager.sharedInstance().getCurrentUser().getUsername());
        assertEquals(newestTask.getAssigneeUid(), UserManager.sharedInstance().getCurrentUser().getUid());
        assertEquals(newestTask.getTaskGroupId(), ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());
        assertEquals(newestTask.getName(), test_name2);
        assertEquals(newestTask.getDescription(), test_description2);
        assertEquals(newestTask.getDueDay(), test_DueDate2[0]);
        assertEquals(newestTask.getDueMonth(), test_DueDate2[1]);
        assertEquals(newestTask.getDueYear(), test_DueDate2[2]);

        mHomeManager.Destroy();

    }



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

        boolean created = mHomeManager.CreateTask(test_name, test_description, test_DueDate);
        assert(created);

        // assert both tasks were created;
        await().atMost(10,TimeUnit.SECONDS).untilAtomic(taskCount,equalTo(initialTaskCount + 1));

        Task fetchedNewTask = tasks.get(tasks.size() - 1);
        fetchedNewTask.setComplete(true);

        dataChanged.set(false);
        mHomeManager.UpdateTask(fetchedNewTask);

        // wait for the listener to receive an update
        await().atMost(10,TimeUnit.SECONDS).untilTrue(dataChanged);

        assertEquals(tasks.size(), initialTaskCount);

        mHomeManager.Destroy();

    }

}

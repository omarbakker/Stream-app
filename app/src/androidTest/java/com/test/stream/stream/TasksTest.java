package com.test.stream.stream;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.UIFragments.expand_task;
import com.test.stream.stream.Utilities.Callbacks.ReadDataCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
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
    public void userSignInSetup(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
    public void initialize_task(){
        TaskManager.getInstance().Initialize(null);
        TaskManager.getInstance().CreateTask(test_name, test_description, test_newTaskAssignee, test_DueDate, complete);
        await().atMost(10,TimeUnit.SECONDS).untilTrue(TaskManager.getInstance().tasksLoaded);
        tasks = TaskManager.getInstance().GetTasksInProject();
        assert(tasks.size() == 1);
        AddedTask.set(true);
    }

//    @Test
//    public void addTask() throws Exception {
//        await().atMost(10, TimeUnit.SECONDS).until(taskCreationFinished());
//        int i = tasks.size()-1;
//        Task test_task = tasks.get(0);
//        assertEquals(test_task.getName(), test_name);
//        assertEquals(test_task.getDescription(), test_description);
//        assertEquals(test_task.getAssignee(), test_newTaskAssignee);
//        assertEquals(test_task.getDueDay(), test_DueDate[0]);
//        assertEquals(test_task.getDueMonth(), test_DueDate[1]);
//        assertEquals(test_task.getDueYear(), test_DueDate[2]);
//        assertEquals(test_task.getComplete(), complete);
//
//    }

    @Test
    public void completeTask() throws Exception {
        await().atMost(10, TimeUnit.SECONDS).untilTrue(AddedTask);
        tasks = TaskManager.getInstance().GetTasksInProject();
        int i = tasks.size()-1;
        Task task = tasks.get(i);
        task.getComplete();
        task.setComplete(true);
        assertEquals(task.getComplete(), true);
        MarkedAsComplete.set(true);
    }

    @Test
    public void editTask() throws Exception {


    }

    @Test
    public void deleteTask() throws Exception {
        await().atMost(10, TimeUnit.SECONDS).untilTrue(MarkedAsComplete);
        tasks = TaskManager.getInstance().GetTasksInProject();
        int i = tasks.size();
        Task task = tasks.get(i);
        String name = task.getName();
        TaskManager.getInstance().DeleteTask(task);
        List<Task> tasks_new = TaskManager.getInstance().GetTasksInProject();
        int a = tasks_new.size()-1;
        assertEquals(i-1, a);

    }
}

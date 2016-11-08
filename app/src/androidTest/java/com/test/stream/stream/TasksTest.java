package com.test.stream.stream;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import android.view.View;

import com.test.stream.stream.Controllers.TaskManager;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.UIFragments.expand_task;

import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by robyn on 2016-11-07.
 */
@RunWith(AndroidJUnit4.class)
public class TasksTest {

    @Test
    public void addTask() throws Exception {
        String test_name = "test name";
        String test_description = "this is a test description";
        String test_newTaskAssignee = "john jingleheimer schmidt";
        int[] test_DueDate = {12,12,2012};
        boolean complete = false;
        TaskManager.getInstance().CreateTask(test_name, test_description, test_newTaskAssignee, test_DueDate, complete);
        List<Task> tasks = TaskManager.getInstance().GetTasksInProject();
        int i = tasks.size()-1;
        Task test_task = tasks.get(i);
        assertEquals(test_task.getName(), test_name);
        assertEquals(test_task.getDescription(), test_description);
        assertEquals(test_task.getAssignee(), test_newTaskAssignee);
        assertEquals(test_task.getDueDay(), test_DueDate[0]);
        assertEquals(test_task.getDueMonth(), test_DueDate[1]);
        assertEquals(test_task.getDueYear(), test_DueDate[2]);
        assertEquals(test_task.getComplete(), complete);

    }

    @Test
    public void completeTask() throws Exception {
        List<Task> tasks = TaskManager.getInstance().GetTasksInProject();
        int i = tasks.size()-1;
        Task task = tasks.get(i);
        task.getComplete();
        task.setComplete(true);
        assertEquals(task.getComplete(), true);


    }

    @Test
    public void editTask() throws Exception {


    }

    @Test
    public void deleteTask() throws Exception {
        List<Task> tasks = TaskManager.getInstance().GetTasksInProject();
        int i = tasks.size()-1;
        Task task = tasks.get(i);
        String name = task.getName();
        TaskManager.getInstance().DeleteTask(task);
        List<Task> tasks_new = TaskManager.getInstance().GetTasksInProject();
        int a = tasks_new.size()-1;
        assertEquals(i-1, a);

    }
}

package com.test.stream.stream.Controllers;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.test.stream.stream.Objects.Chat.ChatGroup;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Tasks.TaskGroup;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-10-23.
 */

public class TaskManager {
    private TaskGroup currentTaskGroup;
    private ConcurrentHashMap<Task, String> tasksInCurrentProject = new ConcurrentHashMap<Task, String>(); //Task - task I

    public void InitializeTasks(Context context) //Note: Change context to your activity class & do it for the private functions
    {
        registerTaskGroup(context);
    }

    //Assumes a project exists.
    private void registerTaskGroup(final Context context)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.TaskGroups.toString());
        Query query = myRef.orderByKey().equalTo(ProjectManager.currentProject.getTaskGroupId());

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentTaskGroup = dataSnapshot.getValue(TaskGroup.class);
                    registerTasks(context);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentTaskGroup = dataSnapshot.getValue(TaskGroup.class);
                    registerTasks(context);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentTaskGroup = dataSnapshot.getValue(TaskGroup.class);
                    registerTasks(context);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);

    }

    private void registerTasks(Context context)
    {
        for(String id : currentTaskGroup.getTasks().keySet())
        {
            if(!tasksInCurrentProject.containsValue(id))
            {
                registerTask(id, context);
            }

        }
    }

    private void registerTask(final String taskId, final Context context)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.Tasks.toString());
        Query query = myRef.orderByKey().equalTo(taskId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasksInCurrentProject.put(task, taskId);
                    //Do whatever you need with the "context" ie. call the updateUI function

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasksInCurrentProject.put(task, taskId);
                    //Do whatever you need with the "context" ie. call the updateUI function
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasksInCurrentProject.put(task, taskId);
                    //Do whatever you need with the "context" ie. call the updateUI function
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);


    }

    public boolean UpdateTask(Task task)
    {
        if(currentTaskGroup == null || !tasksInCurrentProject.containsKey(task))
        {
            return false;
        }

        DatabaseManager.getInstance().updateObject(DatabaseFolders.Tasks, tasksInCurrentProject.get(task), task);
        return true;
    }

    public boolean CreateTask(String taskName, String description, String user, int[] dueDate, boolean complete)
    {
        if(currentTaskGroup == null)
        {
            return false; //Cannot create a task without the project selected.
        }

        Task task = new Task();

        //Set inputted information
        task.setTaskDueDate(dueDate);
        task.setName(taskName);
        task.setDescription(description);
        task.setAssignee(user);
        task.setComplete(complete);

        task.setTaskGroupId(ProjectManager.currentProject.getTaskGroupId());
        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Tasks, task);

        //Store the task in the taskgroup.
        currentTaskGroup.addTask(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.TaskGroups, ProjectManager.currentProject.getTaskGroupId(), currentTaskGroup);

        return true;
    }

    public void DeleteTask(Task task)
    {

    }

    public void Destroy() //Call only when you don't need the tasks anymore.
    {

    }





}

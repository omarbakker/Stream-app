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
    private static TaskManager instance = new TaskManager();
    public static TaskManager getInstance() { return instance; }

    private TaskGroup currentTaskGroup;
    private ConcurrentHashMap<Task, String> tasksInCurrentProject = new ConcurrentHashMap<Task, String>(); //Task - task ID

    private ConcurrentHashMap<Query, ChildEventListener> listenerCollection = new ConcurrentHashMap<Query, ChildEventListener>();;

    private TaskManager(){};

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
                   // CreateTask("TestTaskName", "description", "Merinoe", new int[]{1, 2, 1995}, false);
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
                    currentTaskGroup = null;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);
        listenerCollection.put(query, listener);
    }

    private void registerTasks(Context context)
    {
        for(String id : currentTaskGroup.getTasks().keySet()) //Ensure that each task only is register once.
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
                    //Probably pass in tasksInCurrentProject.keySet() for your keys
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasksInCurrentProject.put(task, taskId);
                    System.out.println("Changed " + taskId);
                    DeleteTask(task);
                    //Do whatever you need with the "context" ie. call the updateUI function
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasksInCurrentProject.remove(task);

                    System.out.println("Deleted " + taskId);
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
        listenerCollection.put(query, listener);
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

    public boolean DeleteTask(Task task)
    {
        if(!tasksInCurrentProject.containsKey(task))
        {
            return false;
        }

        String taskId = tasksInCurrentProject.get(task);

        DatabaseReference refToDelete = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Tasks.toString())
                .child(tasksInCurrentProject.get(task));

        refToDelete.removeValue();

        currentTaskGroup.removeTask(taskId);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.TaskGroups, ProjectManager.currentProject.getTaskGroupId(), currentTaskGroup);

        return true;

    }

    public void Destroy() //Call only when you don't need the tasks anymore.
    {
        //De-register all listeners
        for(Query query: listenerCollection.keySet())
        {
            query.removeEventListener(listenerCollection.get(query));
        }

        instance = new TaskManager(); //Refresh the instance

    }
}

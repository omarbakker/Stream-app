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
import com.test.stream.stream.UIFragments.TasksFragment;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.ArrayList;
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
    private ConcurrentHashMap<String, Task> tasksInCurrentProject = new ConcurrentHashMap<String, Task>(); //Task Id - task

    private ConcurrentHashMap<Query, ChildEventListener> listenerCollection = new ConcurrentHashMap<Query, ChildEventListener>();;

    private TaskManager(){};

    public List<Task> GetTasksInProject()
    {
        List<Task> tasks = new ArrayList();
        tasks.addAll(tasksInCurrentProject.values());

        return tasks;
    }

    public void InitializeTasks(TasksFragment context) //Note: Change context to your activity class & do it for the private functions
    {
        registerTaskGroup(context);

    }

    //Assumes a project exists.
    private void registerTaskGroup(final TasksFragment context)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.TaskGroups.toString());
        Query query = myRef.orderByKey().equalTo(ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentTaskGroup = dataSnapshot.getValue(TaskGroup.class);
                    registerTasks(context);
                    context.updateUI();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentTaskGroup = dataSnapshot.getValue(TaskGroup.class);
                    registerTasks(context);
                    context.updateUI();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                currentTaskGroup = null;
                context.updateUI();
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

    private void registerTasks(TasksFragment context)
    {
        for(String id : currentTaskGroup.getTasks().keySet()) //Ensure that each task only is register once.
        {
            if(!tasksInCurrentProject.containsValue(id))
            {
                registerTask(id, context);
            }
        }
    }

    private void registerTask(final String taskId, final TasksFragment context)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.Tasks.toString());
        Query query = myRef.orderByKey().equalTo(taskId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasksInCurrentProject.put(taskId, task);
                    context.updateUI();
                    //Do whatever you need with the "context" ie. call the updateUI function
                    //Probably pass in tasksInCurrentProject.keySet() for your keys
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasksInCurrentProject.put(taskId, task);
                    System.out.println("Changed " + taskId);
                    context.updateUI();
                    //Do whatever you need with the "context" ie. call the updateUI function
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    tasksInCurrentProject.remove(taskId);
                    System.out.println("Deleted " + taskId);
                    context.updateUI();
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

        DatabaseManager.getInstance().updateObject(
                DatabaseFolders.Tasks,
                task.getId(),
                task);

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

        task.setTaskGroupId(ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());
        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Tasks, task);

        //Store the firebase object key as the object id.
        task.setId(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Tasks, objectKey, task);

        //Store the task in the taskgroup.
        currentTaskGroup.addTask(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.TaskGroups, ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId(), currentTaskGroup);

        return true;
    }

    public boolean DeleteTask(Task task)
    {
        String taskId = task.getId();

        if(!tasksInCurrentProject.containsKey(taskId))
        {
            return false;
        }

        DatabaseReference refToDelete = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Tasks.toString())
                .child(taskId);

        refToDelete.removeValue();

        currentTaskGroup.removeTask(taskId);
        tasksInCurrentProject.remove(task);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.TaskGroups, ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId(), currentTaskGroup);

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
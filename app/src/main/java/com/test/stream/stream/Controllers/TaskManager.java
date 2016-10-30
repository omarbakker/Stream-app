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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-10-23.
 */

public class TaskManager extends DataManager{
    private static TaskManager instance = new TaskManager();
    public static TaskManager getInstance() { return instance; }

    private TasksFragment context;
    private TaskGroup currentTaskGroup;
    private ConcurrentHashMap<String, Task> tasksInCurrentProject = new ConcurrentHashMap<String, Task>(); //Task Id - task

    private TaskManager(){};

    public List<Task> GetTasksInProject()
    {
        List<Task> tasks = new ArrayList();

        List<String> keys = new ArrayList();
        keys.addAll(tasksInCurrentProject.keySet());
        Collections.sort(keys);

        for(String key: keys)
        {
            tasks.add(tasksInCurrentProject.get(key));
        }

        return tasks;
    }

    public void Initialize(TasksFragment context) //Note: Change context to your activity class & do it for the private functions
    {
        this.context = context;
        super.registerParent(DatabaseFolders.TaskGroups, ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());
    }

    @Override
    public void parentUpdated(DataSnapshot dataSnapshot) {
        currentTaskGroup = dataSnapshot.getValue(TaskGroup.class);
        registerTasks();
        context.updateUI();
    }

    @Override
    public void parentDeleted() {
        currentTaskGroup = null;
        context.updateUI();
    }

    @Override
    public void childUpdated(DataSnapshot dataSnapshot) {
        Task task = dataSnapshot.getValue(Task.class);
        tasksInCurrentProject.put(task.getId(), task);

        if(tasksInCurrentProject.size() == currentTaskGroup.getTasks().size())
        {
            context.updateUI();
        }
    }

    @Override
    public void childDeleted(String id) {
        tasksInCurrentProject.remove(id);
        context.updateUI();
    }

    private void registerTasks()
    {
        for(String id : currentTaskGroup.getTasks().keySet()) //Ensure that each task only is register once.
        {
            if(!tasksInCurrentProject.containsValue(id))
            {
                super.registerChild(id, DatabaseFolders.Tasks);
            }
        }
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
}
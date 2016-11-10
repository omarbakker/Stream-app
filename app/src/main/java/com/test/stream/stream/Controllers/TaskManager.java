package com.test.stream.stream.Controllers;

import android.content.Context;
import android.util.Log;

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
import com.test.stream.stream.UIFragments.TaskMain;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  A controller class for the Tasks functionality
 *
 * Created by Catherine Lee on 2016-10-23.
 */

public class TaskManager extends DataManager{
    private static TaskManager instance = new TaskManager();
    private static final String TAG = TaskMain.class.getSimpleName();
    private TasksFragment context;
    public final AtomicBoolean tasksLoaded = new AtomicBoolean(false);
    private TaskGroup currentTaskGroup;
    private ConcurrentHashMap<String, Task> tasksInCurrentProject = new ConcurrentHashMap<String, Task>(); //Task Id - task

    /**
     * Ensure that TaskManager can only be instantiated within the class.
     */
    private TaskManager(){};

    /**
     *
     * @return the only instance of this class (singleton)
     */
    public static TaskManager getInstance() { return instance; }

    /**
     * Fetches a list of task items of a project sorted by creation time.
     *
     * @return a list of task objects, sorted by time order
     */
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

    /**
     * Initializes the BoardManager so that it can maintain updated information of pins in
     * the current project.
     *
     * @param context The java class of the view (ui) controlled by the TaskManager
     */
    public void Initialize(TasksFragment context)
    {
        this.context = context;
        super.registerParent(DatabaseFolders.TaskGroups, ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());

    }


    /**
     * Triggered by an update of the parent taskGroup object, this updates the
     * UI accordingly
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    @Override
    public void parentUpdated(DataSnapshot dataSnapshot) {
        currentTaskGroup = dataSnapshot.getValue(TaskGroup.class);
        registerTasks();
        context.updateUI();
    }

    /**
     * Triggered by the deletion of the parent taskGroup object, this updates the UI
     * accordingly
     */
    @Override
    public void parentDeleted() {
        currentTaskGroup = null;
        context.updateUI();
    }

    /**
     * Triggered by an update of a task object, this updates the
     * UI accordingly
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    @Override
    public void childUpdated(DataSnapshot dataSnapshot) {
        Task task = dataSnapshot.getValue(Task.class);
        tasksInCurrentProject.put(task.getId(), task);

        if(tasksInCurrentProject.size() == currentTaskGroup.getTasks().size())
        {
            if (context != null) {
                context.updateUI();
            }
            tasksLoaded.set(true);

        }
    }

    /**
     * Triggered by the deletion of a task object, this updates the
     * UI accordingly
     */
    @Override
    public void childDeleted(String id) {
        tasksInCurrentProject.remove(id);
        context.updateUI();
    }

    /**
     * Registers a listener to each task not already stored in the BoardManager
     */
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

    /**
     * Update a task.
     *
     * @param task the updated task object
     * @return true if the update request was made to Firebase. False otherwise.
     */
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

    /**
     * Creates task and writes it to the database from
     * the provided parameters
     *
     * @param taskName The name of the task
     * @param description the contents of the task
     * @param user User that the task is assigned to
     * @param dueDate Deadline of the task
     * @param complete true if the task is complete. False otherwise
     * @return True if the new task has been created and added to the databae. False otherwise.
     */
    public boolean CreateTask(String taskName, String description, User user, int[] dueDate, boolean complete)
    {
        if(currentTaskGroup == null)
        {
            return false; //Cannot create a task without the project selected.
        }

        Task task = new Task();

        //Set inputted information
        task.setName(taskName);
        task.setDescription(description);
        task.setUser(user);
        task.setComplete(complete);
        task.setDueDay(dueDate[0]);
        task.setDueMonth(dueDate[1]);
        task.setDueYear(dueDate[2]);

        task.setTaskGroupId(ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId());
        String objectKey = DatabaseManager.getInstance().writeObject(DatabaseFolders.Tasks, task);

        //Store the firebase object key as the object id.
        task.setId(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Tasks, objectKey, task);

        //Update the user with the task
        //TODO: support adding other users, not just current user.
        if (user.getUid() == UserManager.getInstance().getCurrentUser().getUid()) {
            user.addTask(objectKey, ProjectManager.sharedInstance().getCurrentProject().getId());
            UserManager.getInstance().updateUser(user); //Note: we're assuming we're handling the correct user here.
        }
        
        //Store the task in the taskgroup.
        currentTaskGroup.addTask(objectKey);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.TaskGroups, ProjectManager.sharedInstance().getCurrentProject().getTaskGroupId(), currentTaskGroup);

        // update projects active tasks
        Project currentProject = ProjectManager.sharedInstance().getCurrentProject();
        currentProject.setNumberOfActiveTasks(currentProject.getNumberOfActiveTasks()+1);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects,currentProject.getId(),currentProject);
        tasksLoaded.set(true);
        return true;
    }

    //TODO: Remove this overload of create task when create task supports assigned users properly
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
        task.setDueDay(dueDate[0]);
        task.setDueMonth(dueDate[1]);
        task.setDueYear(dueDate[2]);

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

    /**
     * Removes the provided task from the database
     *
     * @param task the task object to remove from Firebase
     * @return true if the pin exists and a removal request was sent to Firebase. False otherwise.
     */
    public boolean DeleteTask(Task task)
    {
        String taskId = task.getId();

        if(!tasksInCurrentProject.containsKey(taskId))
        {
            return false;
        }

        //Get the database reference of a task
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
package com.test.stream.stream.Controllers;

import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controls the home screen
 */

public class HomeManager {

    private ConcurrentHashMap<Task, Integer> userTasks = new ConcurrentHashMap<>(); //Task - due priority
    private int numberCompletedTasks = 0;

    private DataEventListener uiListener;
    private DataEventListener listener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            FetchTasks();
        }
    };

    /**
     * Initialize the home page with a listener
     *
     * @param listener A listener to call back whenever data is updated.
     */
    public HomeManager(DataEventListener listener)
    {
        uiListener = listener;
        TaskManager.sharedInstance().Initialize(this.listener);
    }

    /**
     * Retrieve all tasks that are assigned to the current user and
     * notify listeners when the data has changed.
     */
    private void FetchTasks()
    {
        List<Task> currentTasks = TaskManager.sharedInstance().GetTasksInProject();

        numberCompletedTasks = 0;
        userTasks.clear();

        for(Task currTask: currentTasks)
        {
            if(currTask.getAssigneeUid()
                    .equals(UserManager.sharedInstance().getCurrentUser().getUid()))
            {
                if(!currTask.getComplete())
                {
                    userTasks.put(currTask, currTask.getDuePriority());
                }
                else
                {
                    numberCompletedTasks++;
                }
            }
        }

        uiListener.onDataChanged();

    }

    /**
     * @return all the tasks assigned to the user
     */
    public List<Task> getUserTasks()
    {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(userTasks.keySet());

        return tasks;
    }

    /**
     *
     * @return The percentage of their tasks in the project that the user has completed
     */
    public int getUserProgress()
    {
        double percentage = numberCompletedTasks/(numberCompletedTasks + userTasks.size());
        return (int)(percentage*100.0);
    }


    /**
     * Create a new task with the provided information
     *
     * @param taskName the name of the task
     * @param description the contents of the task
     * @param dueDate the day in which the task is due.
     * @return true if the task has been successfully created. False otherwise.
     */
    public boolean CreateTask(String taskName, String description, int[] dueDate)
    {
        return TaskManager.sharedInstance().CreateTask(taskName, description,
                UserManager.sharedInstance().getCurrentUser(), dueDate, false);
    }

    /**
     * Update the provided task
     *
     * @param task the task to updated, with the updated information
     * @return true if the task was updated. False otherwise.
     */
    public boolean UpdateTask(Task task)
    {
        return TaskManager.sharedInstance().UpdateTask(task);
    }

}

package com.test.stream.stream.Controllers;

import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-11-10.
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

    public HomeManager(DataEventListener listener)
    {
        uiListener = listener;
        TaskManager.getInstance().Initialize(this.listener);
    }

    private void FetchTasks()
    {
        List<Task> currentTasks = TaskManager.getInstance().GetTasksInProject();

        numberCompletedTasks = 0;
        userTasks.clear();

        for(Task currTask: currentTasks)
        {
            if(currTask.getAssigneeUid()
                    .equals(UserManager.getInstance().getCurrentUser().getUid()))
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

    public List<Task> getUserTasks()
    {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(userTasks.keySet());

        return tasks;
    }

    public int getUserProgress()
    {
        double percentage = numberCompletedTasks/(numberCompletedTasks + userTasks.size());
        return (int)(percentage*100.0);
    }


    public boolean CreateTask(String taskName, String description, int[] dueDate)
    {
        return TaskManager.getInstance().CreateTask(taskName, description,
                UserManager.getInstance().getCurrentUser(), dueDate, false);
    }

    public boolean UpdateTask(Task task)
    {
        return TaskManager.getInstance().UpdateTask(task);
    }

}

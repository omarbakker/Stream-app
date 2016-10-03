package com.test.stream.stream.Objects.Tasks;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cathe on 2016-10-01.
 */

public class TaskGroup {
    //region Variables
    private String name;
    private String parentProjectId;
    private int numActiveTasks = 0;
    private int numTasksCompleted = 0;
    private Map<String, Boolean> tasks = new HashMap<String, Boolean>();
    //endregion

    //region Setters and Getters
    public String getName() {
        return name;
    }

    public String getParentProjectId() {
        return parentProjectId;
    }

    public int getNumActiveTasks() {
        return numActiveTasks;
    }

    public int getNumTasksCompleted() {
        return numTasksCompleted;
    }

    public Map<String, Boolean> getTasks() {
        return tasks;
    }

    public void setNumActiveTasks(int numActiveTasks) {
        this.numActiveTasks = numActiveTasks;
    }

    public void setNumTasksCompleted(int numTasksCompleted) {
        this.numTasksCompleted = numTasksCompleted;
    }

    //endregion

    //region Constructors
    public TaskGroup()
    {
        name = "";
        parentProjectId = "";
    }

    public TaskGroup(String parentId, String name)
    {
        this.name = name;
        this.parentProjectId = parentId;
    }

    //endregion

    //region Core Functions
    public boolean addTask(String taskId)
    {
        if(hasTask(taskId))
        {
            return false;
        }

        tasks.put(taskId, true);
        return true;
    }

    public boolean removeTask(String taskId)
    {
        if(!hasTask(taskId))
        {
            return false;
        }

        tasks.remove(taskId);
        return true;
    }

    public boolean hasTask(String taskId)
    {
        return tasks.containsKey(taskId);
    }

    public boolean setTaskCompletionStatus(String taskId, boolean status)
    {
        if(tasks.containsKey(taskId))
        {
            tasks.put(taskId, status);
        }

        return false;
    }
    //endregion


}

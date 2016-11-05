package com.test.stream.stream.Objects.Tasks;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a representation of the TaskGroup datatype, which is the encapsulation of
 * all tasks within a project
 *
 * Created by Catherine Lee on 2016-10-01.
 */

public class TaskGroup {
    //region Variables
    private String parentProjectId;
    private int numActiveTasks = 0;
    private int numTasksCompleted = 0;
    private Map<String, Boolean> tasks = new HashMap<String, Boolean>();
    //endregion

    //region Setters and Getters
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
    public void setNumTasksCompleted(int numTasksCompleted) { this.numTasksCompleted = numTasksCompleted; }
    //endregion

    //region Constructors
    public TaskGroup() {
        parentProjectId = "";
    }

    public TaskGroup(String parentId) {
        this.parentProjectId = parentId;
    }

    //endregion

    /**
     * Add a task to the project
     *
     * @param taskId the id of the task to add to this TaskGroup
     * @return true if the task was added. False otherwise
     */
    public boolean addTask(String taskId)
    {
        if(hasTask(taskId)) {
            return false;
        }

        tasks.put(taskId, true);
        return true;
    }

    /**
     * Remove the specified task from the project
     *
     * @param taskId the id of the task to remove
     * @return true if the task has been removed. False otherwise
     */
    public boolean removeTask(String taskId)
    {
        if(!hasTask(taskId)) {
            return false;
        }

        tasks.remove(taskId);
        return true;
    }

    /**
     * Check if the specified task is in this TaskGroup
     *
     * @param taskId the id of the task to check
     * @return true if the task exists in the project, false otherwise
     */
    public boolean hasTask(String taskId)
    {

        return tasks.containsKey(taskId);
    }

    /**
     * Mark a task as complete or active
     *
     * @param taskId the id of the task to modify
     * @param complete true if the task is complete. False otherwise.
     * @return
     */
    public boolean setTaskCompletionStatus(String taskId, boolean complete)
    {
        if(tasks.containsKey(taskId))
        {
            tasks.put(taskId, complete);
        }

        return false;
    }

}

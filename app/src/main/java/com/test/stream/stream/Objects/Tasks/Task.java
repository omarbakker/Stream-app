package com.test.stream.stream.Objects.Tasks;

import com.test.stream.stream.Objects.Users.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by catherine on 2016-10-01.
 */

public class Task {
    //region Variables
    private String taskGroupId;
    private String name;
    private String description;
    private Date dueDate;
    private int progress;

    private boolean complete = false;
    private Map<String, String> assignees = new HashMap<String, String>();

    //endregion

    //region Getters and Setters
    public String getName() {
        return name;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getProgress() {
        return progress;
    }

    public String getDescription() {
        return description;
    }

    public boolean getComplete() {
        return complete;
    }

    public Map<String, String> getAssignees() {
        return assignees;
    }

    public String getTaskGroupId() {
        return taskGroupId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    //endregion

    //region Constructors
    public Task()
    {
        name = "";
        description = "";
        dueDate = new Date(); //TODO: Find something better to record the date.
        progress = 0;
    }

    public Task(String name, String description, Date dueDate, String taskGroupId)
    {
        this.taskGroupId = taskGroupId;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        progress = 0;
    }

    //endregion

    //region Core Functions
    public boolean addAssignee(String userKey, User user)
    {
        if(isAssigned(user))
        {
            return false;
        }

        assignees.put(user.getUid(), userKey);
        return true;
    }

    public boolean removeAssignee(String userKey, User user)
    {
        if(!isAssigned(user))
        {
            return false;
        }

        assignees.remove(user);
        return true;
    }

    public boolean isAssigned(User user)
    {
        return assignees.containsKey(user.getUid());
    }

    //endregion



}

package com.test.stream.stream.Objects.Tasks;

import com.test.stream.stream.Objects.Users.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A representation of the Task datatype.
 *
 * Created by Catherine Lee on 2016-10-01.
 */

public class Task {
    //region Variables
    private String id;

    private String taskGroupId;
    private String name;
    private String description;

    private int dueDay = 0;
    private int dueMonth = 0;
    private int dueYear = 0;

    private boolean complete = false;
    private String assignee;
    private String assigneeUid;

    //endregion

    //region Getters and Setters
    public String getName() {
        return name;
    }
    public String getId ()
    {
        return id;
    }
    public String getAssignee()
    {
        return assignee;
    }
    public String getAssigneeUid() { return assigneeUid; }
    public int getDueDay()
    {
        return dueDay;
    }
    public int getDueMonth()
    {
        return dueMonth;
    }
    public int getDueYear()
    {
        return dueYear;
    }
    public String getDescription() {
        return description;
    }
    public boolean getComplete() {
        return complete;
    }
    public String getTaskGroupId() {
        return taskGroupId;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }
    public void setDueMonth(int dueMonth) {
        this.dueMonth = dueMonth;
    }
    public void setDueYear(int dueYear) {
        this.dueYear = dueYear;
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
    public void setId(String id)
    {
        this.id = id;
    }
    public void setTaskGroupId(String id)
    {
        this.taskGroupId = id;
    }
    //endregion


    public Task()
    {
        name = "";
        description = "";
    }


    /**
     * @return a string representation of the project's deadine
     */
    public String dueDateRepresentation(){
        return "Due " + dueDay + "/" + dueMonth + "/" + dueYear;
    }

    public int getDuePriority()
    {
        return 365*dueYear+30*dueMonth+dueDay;
    }
    /**
     * Set the user as the tasks's assignee
     *
     * @param user the user to set as the assignee of the task
     */
    public void setUser(User user)
    {
        this.assigneeUid = user.getUid();
        this.assignee = user.getUsername();
    }
}
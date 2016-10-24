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
    private int dueDay = 0;
    private int dueMonth = 0;
    private int dueYear = 0;

    private boolean complete = false;
    private String assignee = "";

    //endregion

    //region Getters and Setters
    public String getName() {
        return name;
    }


    public String getAssignee()
    {
        return assignee;
    }

    public int[] getTaskDueDate() {
        int[] due_date = new int[3];
        due_date[0] = dueDay;
        due_date[1] = dueMonth;
        due_date[2] = dueYear;

        return due_date;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setTaskDueDate(int[] dueDate)
    {
        dueDay = dueDate[0];
        dueMonth = dueDate[1];
        dueYear = dueDate[2];
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignee(String userName)
    {
        this.assignee = userName;
    }

    public void setTaskGroupId(String id)
    {
        this.taskGroupId = id;
    }

    //endregion

    //region Constructors
    public Task()
    {
        name = "";
        description = "";
    }

    //endregion

}

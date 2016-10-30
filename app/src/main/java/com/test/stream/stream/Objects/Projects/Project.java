package com.test.stream.stream.Objects.Projects;
import com.test.stream.stream.Objects.Tasks.TaskGroup;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by omarbakker on 2016-10-28.
 * The Project object.
 */

public class Project {
    //region Variables
    private String name;

    // Map<String,Boolean> for project members, String: uid, Boolean: isAdministrator
    private Map<String, Boolean> members = new HashMap<>();

    private String boardId;
    private String chatId;
    private String calendarId;
    private String taskGroupId;
    private String id;
    private int numberOfActiveTasks;
    private int dueDay;
    private int dueMonth;
    private int dueYear;

    public int getDueDay() { return dueDay; }

    public void setDueDay(int dueDay) { this.dueDay = dueDay; }

    public int getDueMonth() { return dueMonth; }

    public void setDueMonth(int dueMonth) { this.dueMonth = dueMonth; }

    public int getDueYear() { return dueYear; }

    public void setDueYear(int dueYear) { this.dueYear = dueYear; }

    public int getNumberOfActiveTasks() { return numberOfActiveTasks; }

    public void setNumberOfActiveTasks(int numberOfActiveTasks) { this.numberOfActiveTasks = numberOfActiveTasks; }

    public String getId() {  return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public Map<String, Boolean> getMembers() { return  members; }

    public String getBoardId() { return boardId; }

    public String getChatId() { return chatId; }
    public String getCalendarId() { return calendarId; }
    public String getTaskGroupId() { return taskGroupId; }

    public void setName(String name) {
        this.name = name;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public void setCalendarId(String calendarId) { this.calendarId = calendarId; }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public Project()
    {
        name = "";
        boardId = "";
        chatId = "";
        taskGroupId = "";
        setDueDate(0,0,0);
        numberOfActiveTasks = 0;
    }

    public void setDueDate(int day, int month,int year)
    {
        dueDay = day;
        dueMonth = month;
        dueYear = year;
    }

    public String dueDateRepresentation(){
        return "Due on " + dueDay + "/" + dueMonth + "/" + dueYear;
    }

    public boolean addMember(User user, Boolean isAdministrator)
    {
        if(isMember(user))
            return false;

        members.put(user.getUid(), isAdministrator);
        return true;
    }

    public boolean removeMember(User user)
    {
        if(!isMember(user))
            return false;

        members.remove(user.getUid());
        return true;
    }


    public boolean isAdministrator(User user) {
        return members.get(user.getUid());
    }

    private boolean isMember(User user) {
        return members.containsKey(user.getUid());
    }

    public boolean makeAdministrator(User user, boolean isAdministrator)
    {
        if(!isMember(user))
            return false;

        members.put(user.getUid(),isAdministrator);
        return true;
    }

}

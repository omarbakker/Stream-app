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

    //endregion

    //region Getters and setters
    public int getDueDay() { return dueDay; }
    public int getDueMonth() { return dueMonth; }
    public int getDueYear() { return dueYear; }
    public int getNumberOfActiveTasks() { return numberOfActiveTasks; }
    public String getId() {  return id; }
    public String getName() { return name; }
    public Map<String, Boolean> getMembers() { return  members; }
    public String getBoardId() { return boardId; }
    public String getChatId() { return chatId; }
    public String getCalendarId() { return calendarId; }
    public String getTaskGroupId() { return taskGroupId; }


    public void setDueDay(int dueDay) { this.dueDay = dueDay; }
    public void setDueMonth(int dueMonth) { this.dueMonth = dueMonth; }
    public void setDueYear(int dueYear) { this.dueYear = dueYear; }
    public void setNumberOfActiveTasks(int numberOfActiveTasks) { this.numberOfActiveTasks = numberOfActiveTasks; }
    public void setId(String id) { this.id = id; }
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

    //endregion

    /**
     * Create a blank project object
     */
    public Project()
    {
        name = "";
        boardId = "";
        chatId = "";
        taskGroupId = "";
        setDueDate(0,0,0);
        numberOfActiveTasks = 0;
    }

    /**
     * Specify a due date for the project
     *
     * @param day the integer day in which the project is due
     * @param month the month in which the project is due
     * @param year the year this project is due in
     */
    public void setDueDate(int day, int month, int year)
    {
        dueDay = day;
        dueMonth = month;
        dueYear = year;
    }

    /**
     * @return a string representation of the project's deadine
     */
    public String dueDateRepresentation(){
        return "Due " + dueDay + "/" + dueMonth + "/" + dueYear;
    }

    /**
     * Add a user as a member of the project
     *
     * @param user the user to be added to the project
     * @param isAdministrator true if the user is an adminstrator. False otherwise
     * @return true if the user has been added, false otherwise
     */
    public boolean addMember(User user, Boolean isAdministrator)
    {
        if(isMember(user))
            return false;

        members.put(user.getUid(), isAdministrator);
        return true;
    }

    /**
     * Remove the provided user from the project
     *
     * @param user the member to remove
     * @return true if the user has been removed. False otherwse
     */
    public boolean removeMemberByUid(User user)
    {
        if(!isMember(user))
            return false;

        members.remove(user.getUid());
        return true;
    }


    /**
     * Checks if a user is an administrator.
     *
     * @param user the user to check the status of
     * @return true if the user in an administrator. False otherwise.
     */
    public boolean isAdministrator(User user) {

        return members.get(user.getUid());
    }

    /**
     * Checks if a user is a member
     *
     * @param user the user to check the status of
     * @return true if the user is a member. False otherwise.
     */
    public boolean isMember(User user) {
        return isMember(user.getUid());
    }

    /**
     * Checks if a user is a member
     *
     * @param userId the user to check the status of
     * @return true if the user is a member. False otherwise.
     */
    public boolean isMember(String userId) {
        return members.containsKey(userId);
    }

    /**
     * Make a member an administrator or member in the project
     *
     * @param user user to make an administrator or member
     * @param isAdministrator true to make the user an administrator. False otherwise
     * @return true if the change was made. False otherwise.
     */
    public boolean setMemberStatus(User user, boolean isAdministrator)
    {
        if(!isMember(user))
            return false;

        members.put(user.getUid(),isAdministrator);
        return true;
    }

}

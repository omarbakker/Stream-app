package com.test.stream.stream.Objects.Users;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robyn on 2016-09-27.
 * Modifed by Catherine on 2016-10-01
 */

public class User {

    //region Variables
    private String username;
    private String uid;
    private String email;
    private String name;

    // projects String: id, Boolean: project active
    private Map<String, Boolean> projects =  new HashMap<>();
    private Map<String, String> tasks  =  new HashMap<>(); //Task id - project id
    //endregion

    //region Setters and getters

    /**
     * Sets the username of the user
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the username of the user
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets all of the projects that the user is part of
     * @return Map of all of the projects mapping the Project name to a boolean indicating that the user is in the project
     */
    public Map<String, Boolean> getProjects() {
        return projects;
    }

    /**
     * Gets all of teh tasks that the user is part of
     * @return Map of all of the tasks that the user has been assigned mapped to the project
     */
    public Map<String, String> getTasks() {
        return tasks;
    }

    /**
     * gets the User ID
     * @return UserID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the User ID
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets the user's email
     * @return user email
     */
    public String getEmail() { return email; }

    /**
     * Sets the user's email
     * @param email user email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Get's the name of the user
     * @return name of the user
     */
    public String getName() {return name;}

    /**
     * Sets the naem of the user
     * @param name
     */
    public void setName(String name) {this.name = name;}

    //endregion

    /**
     * Constructor for the User
     */
    public User()
    {
        this.uid = "";
        this.username = "";
        this.email = "";
        this.name = "";

    }

    /**
     * Constructor for the user
     * @param uid UID for the user
     */
    public User(String uid) {
        this.uid = uid;
        this.username = " ";
        this.email = "";
        this.name = "";
    }

    /**
     * Constructor for the user
     * @param uid userID
     * @param username user's username
     * @param email user's email
     * @param name user's name
     */
    public User(String uid, String username, String email, String name) {
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.name = name;
    }

    //endregion


    /**
     * Adds a new project to the user
     * @param projectID
     * @return flag indicating that the project was successfully added in
     */
    public boolean addProject(String projectID) {
        if(isInProject(projectID))
        {
            return false;
        }

        projects.put(projectID, true);
        return true;
    }

    /**
     * Removes a project from the user
     * @param projectId
     * @return flag indicating that that the project was successfully removed
     */
    public boolean removeProject(String projectId) {
        if(!isInProject(projectId))
        {
           return false;
        }

        projects.remove(projectId);
        return true;
    }

    /**
     * Checks to see if the user is a member of a project
     * @param projectId projectID of a proejct the user is being searched for
     * @return flag indicating if the user is part of that project
     */
    public boolean isInProject(String projectId)
    {
        return projects.containsKey(projectId);
    }

    /**
     * Adds a new task to the user
     * @param taskId
     * @param taskName
     * @return flag indicating that the task was added in
     */
    public boolean addTask(String taskId, String taskName) {
        if(hasTask(taskId))
        {
            return false;
        }

        tasks.put(taskId, taskName);
        return true;
    }

    /**
     * Removes a task
     * @param taskId
     * @return flag indicating that the task was removed
     */
    public boolean removeTask(String taskId){
        if(!hasTask(taskId))
        {
            return false;
        }

        tasks.remove(taskId);
        return true;
    }

    /**
     * Checks to see if a task has been assigned for the user
     * @param taskId
     * @return flag indicating that the task is assigned to the user
     */
    public boolean hasTask(String taskId)
    {
        return tasks.containsKey(taskId);
    }

    //endregion

}


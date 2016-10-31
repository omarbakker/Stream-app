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
    private Map<String, String> projects =  new HashMap<>();
    private Map<String, String> tasks  =  new HashMap<>();

    //endregion

    //region Setters and getters
    public void SetUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, String> getProjects() {
        return projects;
    }

    public Map<String, String> getTasks() {
        return tasks;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    //endregion

    //region Constructors
    public User()
    {
        this.uid = "";
        this.username = "";
        this.email = "";
        this.name = "";

    }

    public User(String uid) {
        this.uid = uid;
        this.username = " ";
        this.email = "";
        this.name = "";
    }

    public User(String uid, String username, String email, String name) {
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.name = name;
    }

    //endregion

    //region Core Functions
    public boolean addProject(String projectID, String projectName) {
        if(isInProject(projectID))
        {
            return false;
        }

        projects.put(projectID, projectName);
        return true;
    }

    public boolean removeProject(String projectId) {
        if(!isInProject(projectId))
        {
           return false;
        }

        projects.remove(projectId);
        return true;
    }

    public boolean isInProject(String projectId)
    {
        return projects.containsKey(projectId);
    }

    public boolean addTask(String taskId, String taskName) {
        if(hasTask(taskId))
        {
            return false;
        }

        tasks.put(taskId, taskName);
        return true;
    }

    public boolean removeTask(String taskId){
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

    //endregion

}


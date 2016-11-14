package com.test.stream.stream.Controllers;

import com.google.firebase.database.DataSnapshot;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.Objects.Tasks.Task;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-11-14.
 */

public class TeamManager extends DataManager {
    private DataEventListener listener;

    private Project currentProject;
    private ConcurrentHashMap<String, User> memberList = new ConcurrentHashMap<String, User>(); //Firebase user id - user

    /**
     * Requires: current project in ProjectManager exists and is on the database
     * @param listener
     */
    public void Initialize(DataEventListener listener){
        this.listener = listener;
        super.registerParent(DatabaseFolders.Projects, ProjectManager.sharedInstance().getCurrentProject().getId());
    }

    /**
     * Updates the provided project
     *
     * @param project the project to update with contents updated
     */
    private void UpdateProject(Project project)
    {
        DatabaseManager.getInstance().updateObject(DatabaseFolders.Projects, project.getId(), project);
    }

    /**
    * Add a user to the project
    *
    * @param user the user to add to the project
    * @param isAdminsistrator true if the user is an admin. False if they are a regular member.
    * @return true if the member was added succesfully, false otherwise
    */
    public Boolean AddMemberToCurrentProject(User user, Boolean isAdminsistrator)
    {
        if(currentProject.addMember(user, isAdminsistrator))
        {
            UpdateProject(currentProject);
            return true;
        }

        return false;

    }

    public ArrayList<User> GetUsers()
    {
        ArrayList users = new ArrayList();
        users.addAll(memberList.values());
        return users;
    }

    public Boolean DeleteMemberFromCurrentProject(User user)
    {
        if(currentProject.removeMemberByUid(user))
        {
            UpdateProject(currentProject);
            return true;
        }

        return false;
    }

    /**
     * Update the project if it has been modified in Firebase
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    @Override
    public void parentUpdated(DataSnapshot dataSnapshot) {
        currentProject = dataSnapshot.getValue(Project.class); //Get the updated project
        registerMembers();
    }

    /**
     * Trigered if the parent object (currentProject) has been deleted
     */
    @Override
    public void parentDeleted() {
        //TODO: Handle potential errors relating to this.
    }

    /**
     *
     * @param id the firebase key of the user
     */
    @Override
    public void childDeleted(String id) {
        memberList.remove(id);
        listener.onDataChanged();
    }

    /**
     *
     * @param dataSnapshot The object returned by Firebase containing the read object and its key.
     */
    @Override
    public void childUpdated(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        memberList.put(dataSnapshot.getKey(), user);

        if(memberList.size() == currentProject.getMembers().size())
        {
            listener.onDataChanged();
        }
    }


    /**
     * Registers a listener to each user not already stored in the memberList
     */
    private void registerMembers()
    {
        for(String id : currentProject.getMembers().keySet()) //Ensure that each user only is register once.
        {
            if(!memberList.containsValue(id))
            {
                super.registerChild(id, DatabaseFolders.Users);
            }
        }
    }
}

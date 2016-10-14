package com.test.stream.stream.Objects.Projects;

import com.test.stream.stream.Objects.Tasks.TaskGroup;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by catherine on 2016-10-01.
 */

public class Project {
    //region Variables
    private String name;
    private Map<String, String> administrators = new HashMap<>();
    private Map<String, String> members = new HashMap<>();
    private String boardId;
    private String chatId;
    private String taskGroupId;
    //endregion

    //region Setters and Getters
    public String getName() { return name; }
    public Map<String, String> getAdministrators() { return administrators; }
    public Map<String, String> getMembers() { return  members; }
    public String getBoardId() { return boardId; }
    public String getChatId() { return chatId; }
    public String getTaskGroupId() { return taskGroupId; }

    public void setName(String name) {
        this.name = name;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }
    //endregion

    //region Constructors
    public Project()
    {
        name = "";
        boardId = "";
        chatId = "";
        taskGroupId = "";
    }
    //endregion

    //region Core Functions

    public void CreateProject(String name, String key, User projectOwner, String ownerKey)
    {
        this.name = name;
        administrators.put(projectOwner.getUid(), ownerKey);
    //    boardId = DatabaseManager.getInstance().writeObject("boards", new Board());
    //    chatId = DatabaseManager.getInstance().writeObject("chatGroups", new ChatGroup());
        taskGroupId = DatabaseManager.getInstance().writeObject("taskGroups", new TaskGroup(key, "TG"+key));
    }


    public boolean AddMember(User user, String userKey)
    {
        if(isMember(user)|| isAdministrator(user))
        {
            return false;
        }

        members.put(user.getUid(), userKey);
        return true;
    }

    public boolean RemoveMember(User user)
    {
        if(!isMember(user))
        {
            return false;
        }

        members.remove(user.getUid());
        return true;
    }

    public boolean AddAdministrator(User user, String userKey)
    {
        if(isMember(user)|| isAdministrator(user))
        {
            return false;
        }
        administrators.put(user.getUid(), userKey);
        return true;
    }

    public boolean RemoveAdministrator(User user)
    {
        if(!isAdministrator(user))
        {
            return false;
        }

        administrators.remove(user.getUid());
        return true;
    }


    public boolean isAdministrator(User user)
    {
        return administrators.containsKey(user.getUid());
    }

    public boolean isMember(User user)

    {
        return members.containsKey(user.getUid());
    }

    //endregion

    //TODO: Move these two functions over to the controller
    public boolean memberToAdministrator(User user)
    {
        if(!isMember(user))
        {
            return false;
        }

        String userKey = members.get(user.getUid());
        RemoveMember(user);
        AddAdministrator(user, userKey);
        return true;
    }

    public boolean administratorToMember(User user)
    {
        if(!isAdministrator(user))
        {
            return false;
        }

        String userKey = administrators.get(user.getUid());
        RemoveAdministrator(user);
        AddMember(user, userKey);
        return true;
    }
}

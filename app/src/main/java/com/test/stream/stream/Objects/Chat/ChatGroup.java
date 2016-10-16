package com.test.stream.stream.Objects.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cathe on 2016-10-01.
 */

public class ChatGroup {
    private String parentProjectId;
    private Map<String, String> channels = new HashMap<>();

    public ChatGroup() {
    }

    public ChatGroup(String parentId) { parentProjectId = parentId;}

    public void setChannels (HashMap<String, String> channels){
        this.channels = channels;
    }

    public Map<String, String> getChannels() {
        return channels;
    }

    public String getParentProjectId () { return parentProjectId;}

    public void setParentProjectId(String projectId){ this.parentProjectId = projectId;}

    public void addChannel(String channelName, String channelId)
    {
        if(!containsChannel(channelName))
        {
            channels.put(channelName, channelId);
        }
    }

    public boolean containsChannel(String channelName)
    {
        return channels.containsKey(channelName);
    }

    public void removeChannel(String channelName)
    {
        if(containsChannel(channelName))
        {
            channels.remove(channelName);
        }
    }
}

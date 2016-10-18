package com.test.stream.stream.Controllers;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.test.stream.stream.Objects.Chat.Channel;
import com.test.stream.stream.Objects.Chat.ChatGroup;
import com.test.stream.stream.Objects.Chat.Message;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.ChatScreen;
import com.test.stream.stream.Utilities.DatabaseFolders;
import com.test.stream.stream.Utilities.DatabaseManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cathe on 2016-10-15.
 */

public class ChatManager {
    private static ChatManager instance = new ChatManager();

    private ChatGroup currentChatGroup;
    private String currentChatGroupId;
    private Channel currentChannel;
    private String currentChannelId;

    private ConcurrentHashMap<Query, ChildEventListener> channelListeners = new ConcurrentHashMap<Query, ChildEventListener>();;
    private ConcurrentHashMap<Query, ChildEventListener> chatGroupListeners =  new ConcurrentHashMap<Query, ChildEventListener>();;

    private ChatManager(){};

    public static ChatManager getInstance() { return instance; }

    public void registerChatGroup(Project project, final ChatScreen context)
    {
        registerGroupChatByID(project.getChatId(), context);
    }

    //Remove later or make private. Only for testing.
    public void registerGroupChatByID(String chatId, final AppCompatActivity context)
    {
        DatabaseReference myRef = DatabaseManager.getInstance().getReference(DatabaseFolders.ChatGroups.toString());
        Query query = myRef.orderByKey().equalTo(chatId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentChatGroup = dataSnapshot.getValue(ChatGroup.class);
                    currentChatGroupId = dataSnapshot.getKey();
                    context.invalidateOptionsMenu();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    currentChatGroup = dataSnapshot.getValue(ChatGroup.class);
                    context.invalidateOptionsMenu();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);

        chatGroupListeners.put(query, listener);

    }

    public Map<String, String> getChannels()
    {
        if(currentChatGroup != null)
        {
            return currentChatGroup.getChannels();
        }

        return null;
    }

    public void registerChannel(String channelId, final ChatScreen context)
    {
        currentChannelId = channelId;

        DatabaseReference myRef = DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Channels.toString());

        Query query = myRef.orderByKey().equalTo(channelId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists() && dataSnapshot.getKey().equals(currentChannelId))
                {
                    currentChannel = dataSnapshot.getValue(Channel.class);
                    context.registerContent();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists() && dataSnapshot.getKey().equals(currentChannelId))
                {
                    currentChannel = dataSnapshot.getValue(Channel.class);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addChildEventListener(listener);

        channelListeners.put(query, listener);

    }

    public boolean writeMessage(String username, String content)
    {
        Message message = new Message(content, username);
        DatabaseReference reference = getChannelReference();

        if(reference == null)
        {
            return false;
        }

        DatabaseManager.getInstance().writeObjectByReference(reference, message);

        return true;
    }

    public String getChannelName()
    {
       return currentChannel.getName();
    }

    public DatabaseReference getChannelReference()
    {
        if(currentChannelId.isEmpty())
        {
            return null;
        }

        return DatabaseManager.getInstance()
                .getReference(DatabaseFolders.Channels.toString())
                .child(currentChannelId)
                .child(DatabaseFolders.messages.toString());
    }

    //Precondition: The channel cannot be an invalid name (ie. +channel)
    public boolean createChannel(String name)
    {
        if(currentChatGroup == null || currentChatGroup.containsChannel(name))
        {
            return false; //Cannot create a channel without a chat group,
                         // an existing channel
        }

        Channel newChannel = new Channel(name);
        String id = DatabaseManager.getInstance().writeObject(DatabaseFolders.Channels, newChannel);

        currentChatGroup.addChannel(name, id);
        DatabaseManager.getInstance().updateObject(DatabaseFolders.ChatGroups, currentChatGroupId, currentChatGroup);

        return true;
    }

    public void destroyAllListeners()
    {
        for(Query query: channelListeners.keySet())
        {
            query.removeEventListener(channelListeners.get(query));
        }

        for(Query query: chatGroupListeners.keySet())
        {
            query.removeEventListener(chatGroupListeners.get(query));
        }

        chatGroupListeners.clear();
        channelListeners.clear();
    }

}

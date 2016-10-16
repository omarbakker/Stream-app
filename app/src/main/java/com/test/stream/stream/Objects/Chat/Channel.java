package com.test.stream.stream.Objects.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cathe on 2016-10-01.
 */

public class Channel {
    private String name;
    private HashMap<String, Message> messages = new HashMap<String, Message>();

    public Channel() {
    }

    public Channel(String name) {
        this.name = name;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

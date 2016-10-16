package com.test.stream.stream.Objects.Chat;

/**
 * Created by cathe on 2016-10-14.
 */


public class Message {

    private String text;
    private String name;

    public Message() {
    }

    public Message(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

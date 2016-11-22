package com.test.stream.stream.Objects.Notifications;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Xingyu on 11/18/2016.
 */

public class NotificationObject {

    private String title;
    private String message;
    private String[] users;

    /**
     * prevent calling void constructor
     */
    public NotificationObject(){};

    public NotificationObject(String title, String message, String[] users){
        NotificationObject notification = new NotificationObject();
        notification.title = title;
        notification.message = message;
        notification.users = users;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }



}

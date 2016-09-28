package com.test.stream.streamtest.Objects;

/**
 * Created by robyn on 2016-09-27.
 */

public class User {

    private String username;
    private String email;


    public User(){
        this.username = " ";
        this.email = " ";
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username) {
        this.username = username;
        this.email = " ";
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }

}

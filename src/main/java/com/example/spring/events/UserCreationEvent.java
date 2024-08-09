package com.example.spring.events;

public class UserCreationEvent<User> extends EntityEvent<User> {

    private User user ;

    public UserCreationEvent(User user) {
        super(user);
        this.user = user;
    }

    public User getUser(){
        return  user;
    }
}

package com.steed.top5.singleton;

import com.steed.top5.pojo.User;

public class UserSingleton {

    public User currentUser;

    private static final UserSingleton holder = new UserSingleton();

    public static UserSingleton getInstance() {
        return holder;
    }

}

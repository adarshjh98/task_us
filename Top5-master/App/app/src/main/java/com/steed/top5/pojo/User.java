package com.steed.top5.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    public String uid;
    public String name;
    public String email;
    public String password;
    public String profilePhoto;
//    public String gender;
//    public String bio;
    public ArrayList<String> categories;
    public boolean isAuthenticated;
    public boolean isNew;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public HashMap<String, String> toJson(){
        HashMap<String, String> json = new HashMap<>();
        json.put("name", name);
        json.put("email", email);
        return json;
    }
}

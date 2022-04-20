package com.steed.top5.pojo;

import java.util.HashMap;

public class Comment {

    public String id, userID, comment, userName,userPhoto;
    public long timestamp;
    public Post post;

    public Comment(Post post, String id, String userID, String comment, long timestamp) {
        this.post = post;
        this.id = id;
        this.userID = userID;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public HashMap<String, Object> toJson() {
        HashMap<String, Object> json = new HashMap<>();

        json.put("userID", userID);
        json.put("comment", comment);
        json.put("timestamp", timestamp);

        return json;
    }
}

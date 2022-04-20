package com.steed.top5.pojo;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {

    public String id, type, name, link, text;
    public long likes, comments;
    public Category category;
    public boolean isLiked, isSaved;
    public ArrayList<String> tags;

    public Post() {
    }

    public Post(String id, String type, String name, String link, String text, long likes, long comments, Category category, ArrayList<String> tags) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.link = link;
        this.text = text;
        this.likes = likes;
        this.comments = comments;
        this.category = category;
        this.tags = tags;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Post post = (Post) obj;

        return this.id.equals(post.id);
    }
}

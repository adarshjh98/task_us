package com.steed.top5.pojo;

public class LikeResponse {

    public boolean isSuccess;
    public Post post;

    public LikeResponse(boolean isSuccess, Post post) {
        this.isSuccess = isSuccess;
        this.post = post;
    }
}

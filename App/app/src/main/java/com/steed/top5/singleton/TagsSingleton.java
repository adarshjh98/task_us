package com.steed.top5.singleton;

import java.util.ArrayList;
import java.util.HashMap;

public class TagsSingleton {

    public ArrayList<String> tags, recentlyViewedPosts;
    public ArrayList<HashMap<String, String>> recentlyViewed;

    private static final TagsSingleton holder = new TagsSingleton();

    public static TagsSingleton getInstance() {
        return holder;
    }

    TagsSingleton() {
        tags = new ArrayList<>();
        recentlyViewedPosts = new ArrayList<>();
        recentlyViewed = new ArrayList<>();
    }
}

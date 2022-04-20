package com.steed.top5.pojo;

import java.util.ArrayList;
import java.util.HashMap;

public class TagsResponse {

    public String statusMessage;
    public boolean isError;
    public ArrayList<HashMap<String, String>> tags;

    public TagsResponse() {
    }
}

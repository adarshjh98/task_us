package com.steed.top5.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String extractImageLink(String postText){
        String link="";
        Pattern pattern = Pattern.compile("<img\\ssrc=\"([^\"]+)");
        Matcher matcher = pattern.matcher(postText);
        if (matcher.find()) {
            link = matcher.group(1);
        }

        return link;
    }
}

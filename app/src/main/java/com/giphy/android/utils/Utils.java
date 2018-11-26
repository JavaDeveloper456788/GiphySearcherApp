package com.giphy.android.utils;

import java.util.regex.Pattern;

/**
 *  Util methods
 */

public class Utils {
    /**
     * Sometimes Giphy API changes the hostname of image URL.
     * Use the remaining path of URL as a unique identifier of the image instead of the full URL
     */
    public static  String getUniqueUrl(String url){
        String[] paths = url.split(Pattern.quote("/"));
        return url.replace(paths[2],"");
    }
}

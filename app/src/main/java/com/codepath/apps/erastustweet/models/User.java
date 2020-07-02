package com.codepath.apps.erastustweet.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    public String name;
    public String screenName;
    public String profilePictureUrl;
    public boolean isVerified;

    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name  = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profilePictureUrl = getBiggerUrl(jsonObject.getString("profile_image_url_https"));
        user.isVerified = jsonObject.getBoolean("verified");
        return user;
    }

    private static String getBiggerUrl(String imageUrl){
        int i;
        for (i = imageUrl.length() - 1; imageUrl.charAt(i) != '.' && i >= 0; i--);
        String extension = imageUrl.substring(i);
        return imageUrl.substring(0, i - 7) + extension;
    }
}

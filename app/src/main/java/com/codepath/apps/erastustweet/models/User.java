package com.codepath.apps.erastustweet.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String name;
    public String screenName;
    public String profilePictureUrl;
    public boolean isVerified;

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name  = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profilePictureUrl = jsonObject.getString("profile_image_url_https");
        user.isVerified = jsonObject.getBoolean("verified");
        return user;
    }
}

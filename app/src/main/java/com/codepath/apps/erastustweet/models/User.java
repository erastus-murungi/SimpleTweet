package com.codepath.apps.erastustweet.models;

import android.util.Log;

import com.codepath.apps.erastustweet.utilities.FormatNumbers;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public static final String TAG = "User";
    public String name;
    public String screenName;
    public String profileImageUrl;
    public boolean isVerified;
    public String bannerUrl;
    public String createdAt;
    public String description;
    public String followersCount;
    public String followingCount;
    public ScreenNameId screenNameId;

    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        assert jsonObject != null;
        user.name  = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = getBiggerUrl(jsonObject.getString("profile_image_url_https"));
        user.isVerified = jsonObject.getBoolean("verified");
        user.bannerUrl = getBannerUrl(jsonObject);
        user.createdAt = Tweet.getRelativeTimeAgo(jsonObject.getString("created_at"));
        user.description = getDescription(jsonObject);
        user.followersCount = FormatNumbers.format(jsonObject.getInt("followers_count"));
        user.followingCount = FormatNumbers.format(jsonObject.getInt("friends_count"));
        user.screenNameId = new ScreenNameId(jsonObject.getLong("id"), user.screenName);
        return user;
    }

    private static String getDescription(JSONObject jsonObject) {
        try {
            return jsonObject.getString("description");
        } catch (JSONException e) {
            return null;
        }
    }

    private static String getBannerUrl(JSONObject jsonObject) {
        try {
            return getBiggerUrl(jsonObject.getString("profile_banner_url"));
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
            return null;
        }
    }

    private static String getBiggerUrl(String imageUrl){
        int i;
        for (i = imageUrl.length() - 1; imageUrl.charAt(i) != '.' && i >= 0; i--);
        String extension = imageUrl.substring(i);
        return imageUrl.substring(0, i - 7) + extension;
    }
}

package com.codepath.apps.erastustweet.models;

import android.util.Log;

import com.codepath.apps.erastustweet.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import okhttp3.Headers;

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

    public static void fromIdAndScreenName(final Long id, final String name, TwitterClient client, final Tweet tweet, final boolean toReply) {
        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    if (toReply) {
                        tweet.replyToUser = ScreenNameId.fromJson(json.jsonObject);
                    } else {
                        tweet.user = User.fromJson(json.jsonObject);
                    }
                } catch (JSONException e) {
                    Log.e("User", "JSONException", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("User", "Error", throwable);
            }
        }, id, name);
    }
}

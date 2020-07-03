package com.codepath.apps.erastustweet.models;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.apps.erastustweet.TwitterApp;
import com.codepath.apps.erastustweet.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.parceler.Parcel;


@Parcel
public class Tweet {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final String TAG = "Tweet";

    public String body;
    public String createdAt;
    public User user;
    public long id;
    public TweetEntity entity;
    public boolean reTweeted;
    public ScreenNameId replyToUser;

    public Tweet() {
    }


    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        tweet.entity = TweetEntity.fromJsonArray(jsonObject);
        tweet.reTweeted = getRetweet(jsonObject);
        tweet.replyToUser = ScreenNameId.fromJson(jsonObject);
        return tweet;
    }

//    private User getReplyToUser(Context context, JSONObject jsonObject, Tweet tweet) {
//        String name;
//        Long id;
//        try {
//            name = jsonObject.getString("in_reply_to_screen_name");
//            id = jsonObject.getLong("in_reply_to_user_id");
//        } catch (JSONException e) {
//            name = null;
//            id = null;
//        }
//        return User.fromIdAndScreenName(TwitterApp.getRestClient(context), id, name, tweet, toReply);
//    }
    private static boolean getRetweet(JSONObject jsonObject) {
        try {
            jsonObject.getJSONObject("retweeted_status");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }
        return "";
    }
}

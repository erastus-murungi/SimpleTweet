package com.codepath.apps.erastustweet.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class TweetEntity {
    public static final String TAG = "TweetEntities";
    public String[] hashTags;
    public String[][] urls;
    public String[][] userMentions;
    public String[] symbols;
    public MediaType mediaType;
    public String[] entities;

    public TweetEntity() {
    }

    public static TweetEntity fromJsonArray(JSONObject jsonObject) throws JSONException {
        TweetEntity tweetEntity = new TweetEntity();
        tweetEntity.entities = getMediaUrls(jsonObject);
        tweetEntity.mediaType = getMediaType(jsonObject);
        tweetEntity.hashTags = getHashTags(jsonObject);
        tweetEntity.userMentions = getUserMentions(jsonObject);
        tweetEntity.urls = getUrls(jsonObject);
        tweetEntity.symbols = getSymbols(jsonObject);
        return tweetEntity;
    }

    private static String[][] getUrls(JSONObject jsonObject) {
        try {
            JSONArray urlsArray = jsonObject.getJSONArray("urls");
            String[][] urls = new String[urlsArray.length()][2];
            JSONObject urlObject;
            for (int i = 0; i < urlsArray.length(); i++) {
                urlObject = urlsArray.getJSONObject(i);
                urls[i][0] = urlObject.getString("display_url");
                urls[i][1] = urlObject.getString("expanded_url");
            }
            return urls;
        } catch (JSONException e) {
            return null;
        }
    }

    private static String[] getSymbols(JSONObject jsonObject) {
        try {
            JSONArray symbolsArray = jsonObject.getJSONArray("symbols");
            String[] symbols = new String[symbolsArray.length()];
            for (int i = 0; i < symbolsArray.length(); i++) {
                symbols[i] = symbolsArray.getJSONObject(i).getString("text");
            }
            return symbols;
        } catch (JSONException e) {
            return null;
        }
    }

    private static String[][] getUserMentions(JSONObject jsonObject) {
        try {
            JSONArray userMentionsArray = jsonObject.getJSONArray("user_mentions");
            String[][] userMentions = new String[userMentionsArray.length()][2];
            JSONObject userMentionObject;
            for (int i = 0; i < userMentionsArray.length(); i++) {
                userMentionObject = userMentionsArray.getJSONObject(i);
                userMentions[i][0] = userMentionObject.getString("text");
                userMentions[i][1] = userMentionObject.getString("id");
            }
            return userMentions;
        } catch (JSONException e) {
            return null;
        }
    }

    private static String[] getHashTags(JSONObject jsonObject) {
        try {
            JSONArray hashTagArray = jsonObject.getJSONArray("hashtags");
            String[] hashTags = new String[hashTagArray.length()];
            for (int i = 0; i < hashTagArray.length(); i++) {
                hashTags[i] = hashTagArray.getJSONObject(i).getString("text");
            }
            return hashTags;
        } catch (JSONException e) {
            return null;
        }
    }

    private static MediaType getMediaType(JSONObject jsonObject) {
        try {
            JSONArray mediaArray = jsonObject.getJSONArray("extended_entities");
            JSONObject mediaObject = mediaArray.getJSONObject(0);
            if (mediaObject == null) {
                return null;
            } else {
                String type = mediaObject.getString("type");
                switch (type) {
                    case "photo":
                        return MediaType.IMAGE;
                    case "video":
                        return MediaType.VIDEO;
                    case "animated_gif":
                        return MediaType.ANIMATED_GIF;
                    default:
                        return null;
                }
            }
        } catch (JSONException e) {
            return null;
        }
    }

    private static String[] getMediaUrls(JSONObject jsonObject) {
        try {
            JSONArray mediaArray = jsonObject.getJSONArray("extended_entities");
            String[] entities = new String[jsonObject.length()];
            for (int i = 0; i < mediaArray.length(); i++) {
                JSONObject mediaObject = mediaArray.getJSONObject(i);
                entities[i] = mediaObject.getString("media_url_https");
            }
            return entities;

        } catch (JSONException e) {
            Log.i(TAG, "Image has no video!", e);
            return null;
        }
    }

}

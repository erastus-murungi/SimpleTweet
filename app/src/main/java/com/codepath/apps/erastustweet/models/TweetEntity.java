package com.codepath.apps.erastustweet.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class TweetEntity {
    public static final String TAG = "TweetEntity";
    public int[][] hashTagIndices;
    public String[][] urls;
    public UserMention[] userMentions;
    public String[] symbols;
    public MediaType mediaType;
    public String[] entities;

    public TweetEntity() {
    }

    public static TweetEntity fromJsonArray(JSONObject jsonObject) throws JSONException {
        TweetEntity tweetEntity = new TweetEntity();
        tweetEntity.entities = getMediaUrls(jsonObject);
        tweetEntity.mediaType = getMediaType(jsonObject);
        tweetEntity.hashTagIndices = getHashTags(jsonObject);
        tweetEntity.userMentions = getUserMentions(jsonObject);
        tweetEntity.urls = getUrls(jsonObject);
        tweetEntity.symbols = getSymbols(jsonObject);
        return tweetEntity;
    }

    private static String[][] getUrls(JSONObject jsonObject) {
        try {
            JSONArray urlsArray = jsonObject.getJSONObject("entities").getJSONArray("urls");
            if (urlsArray.length() == 0) {
                return null;
            }
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

            JSONArray symbolsArray = jsonObject.getJSONObject("symbols").getJSONArray("symbols");
            if (symbolsArray.length() == 0) {
                return null;
            }
            String[] symbols = new String[symbolsArray.length()];
            for (int i = 0; i < symbolsArray.length(); i++) {
                symbols[i] = symbolsArray.getJSONObject(i).getString("text");
            }
            return symbols;
        } catch (JSONException e) {
            return null;
        }
    }

    private static UserMention[] getUserMentions(JSONObject jsonObject) {
        try {
            UserMention[] mentions = UserMention.fromJsonArray(jsonObject);
            if (mentions == null) {
                Log.i(TAG, "No mentions");
                return null;
            } else  {
                return mentions;
            }
        } catch (JSONException e) {
            Log.i(TAG, "Json Exception", e);
            return null;
        }
    }

    private static int[][] getHashTags(JSONObject jsonObject) {
        try {
            JSONObject entitiesObject = jsonObject.getJSONObject("entities");
            JSONArray hashTagArray = entitiesObject.getJSONArray("hashtags");
            if (hashTagArray.length() == 0) {
                return null;
            }
            int[][] hashTags = new int[hashTagArray.length()][];
            for (int i = 0; i < hashTagArray.length(); i++) {
                JSONArray indices = hashTagArray.getJSONObject(i).getJSONArray("indices");
                hashTags[i] = new int[indices.length()];
                for (int j = 0; j < indices.length(); j++) {
                    hashTags[i][j] = indices.getInt(j);
                }
            }
            return hashTags;
        } catch (JSONException e) {
            return null;
        }
    }

    private static MediaType getMediaType(JSONObject jsonObject) {
        try {
            JSONObject extendedEntities = jsonObject.getJSONObject("extended_entities");
            JSONArray mediaArray = extendedEntities.getJSONArray("media");
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
            JSONObject extendedEntities = jsonObject.getJSONObject("extended_entities");
            JSONArray mediaArray = extendedEntities.getJSONArray("media");
            if (mediaArray.length() == 0) {
                return null;
            }
            String[] entities = new String[mediaArray.length()];
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

package com.codepath.apps.erastustweet.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class UserMention {
    /**
     * {
     *   "user_mentions": [
     *     {
     *       "name": "Twitter API",
     *       "indices": [
     *         4,
     *         15
     *       ],
     *       "screen_name": "twitterapi",
     *       "id": 6253282,
     *       "id_str": "6253282"
     *     }
     *   ]
     * }
     */

    public String screenName;
    public String name;
    public long id;
    public int[] indices;

    public UserMention() {}

    public static UserMention[] fromJsonArray(JSONObject jsonObject) throws JSONException {
        JSONArray userMentionsArray = jsonObject.getJSONObject("entities").getJSONArray("user_mentions");
        if (userMentionsArray.length() == 0) {
            return null;
        }
        UserMention[] userMentions = new UserMention[userMentionsArray.length()];
        UserMention temp;
        JSONObject userMention;
        for (int i = 0; i < userMentionsArray.length(); i++) {
            userMention = userMentionsArray.getJSONObject(i);
            temp = new UserMention();
            temp.name = userMention.getString("name");
            temp.screenName = userMention.getString("screen_name");
            temp.id = userMention.getLong("id");
            temp.indices = toIntArray(userMention.getJSONArray("indices"));
            userMentions[i] = temp;
        }
        return userMentions;
    }

    private static int[] toIntArray(JSONArray jsonArray) throws JSONException {
        int[] arr = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            arr[i] = jsonArray.getInt(i);
        }
        return arr;

    }
}

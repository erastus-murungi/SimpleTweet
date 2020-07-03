package com.codepath.apps.erastustweet.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class ScreenNameId {
    public long id;
    public String screenName;

    public ScreenNameId() {}

    public static ScreenNameId fromJson(JSONObject jsonObject) {
        try {
            ScreenNameId sid = new ScreenNameId();
            sid.id = jsonObject.getLong("in_reply_to_user_id");
            sid.screenName = jsonObject.getString(jsonObject.getString("in_reply_to_screen_name"));
            return sid;
        } catch (JSONException e) {
            Log.e("ScreenNameId", "JSON Exception", e);
            return null;
        }
    }
}

package com.codepath.apps.erastustweet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.erastustweet.adapters.TweetsAdapter;
import com.codepath.apps.erastustweet.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";
    TwitterClient twitterClient;
    RecyclerView timelineRecyclerView;
    TweetsAdapter tweetsAdapter;
    List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Timeline");

        twitterClient = TwitterApp.getRestClient(this);
        timelineRecyclerView = findViewById(R.id.timelineRecyclerView);
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);
        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timelineRecyclerView.setAdapter(tweetsAdapter);
        populateHomeTimeline();

    }

    private void populateHomeTimeline() {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Json Success!");
                JSONArray jsonArray = json.jsonArray;
                try {
                    Log.i(TAG, "Content" + json.toString());
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    tweetsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Json Failure!" + response, throwable);
            }
        });
    }
}
package com.codepath.apps.erastustweet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    SwipeRefreshLayout timelineSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Timeline");

        timelineRecyclerView = findViewById(R.id.timelineRecyclerView);
        timelineSwipeRefresh = findViewById(R.id.timelineSwipeRefresh);

        twitterClient = TwitterApp.getRestClient(this);
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);

        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timelineRecyclerView.setAdapter(tweetsAdapter);

        // add a horizontal separator between rows
        timelineRecyclerView.addItemDecoration(
                new DividerItemDecoration(timelineRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL));



        // set the loading indicator to cycle between four colors
        timelineSwipeRefresh.setColorSchemeResources(R.color.holo_red_dark,
                R.color.holo_blue_dark, R.color.DarkSalmon, R.color.android_green);

        timelineSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching new data.");
                populateHomeTimeline();
            }
        });

        // send get tweets request and populate timeline recycle view
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
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    // refreshing is finished and so we no longer need to show `loading` indicator
                    timelineSwipeRefresh.setRefreshing(false);
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
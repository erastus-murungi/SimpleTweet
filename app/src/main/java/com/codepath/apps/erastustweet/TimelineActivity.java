package com.codepath.apps.erastustweet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.erastustweet.adapters.TweetsAdapter;
import com.codepath.apps.erastustweet.fragments.ReplyTweetFragment;
import com.codepath.apps.erastustweet.models.ScreenNameId;
import com.codepath.apps.erastustweet.models.Tweet;
import com.codepath.apps.erastustweet.models.User;
import com.codepath.apps.erastustweet.models.UserMention;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ReplyTweetFragment.OnInputListener {
    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE = 20;
    public static final long TIME_INTERVAL = 2000L;
    public Map<String, ScreenNameId> tagToUserMap;

    private long mBackPressed = 0L;

    private TwitterClient mTwitterClient;
    private RecyclerView mTimelineRecyclerViewer;
    private TweetsAdapter mTweetsAdapter;
    private List<Tweet> mTweets;
    private SwipeRefreshLayout mTimelineSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Objects.requireNonNull(getSupportActionBar()).
                setBackgroundDrawable(new ColorDrawable(getColor(R.color.twitter_blue)));

        mTimelineRecyclerViewer = findViewById(R.id.recycler_view_timeline);
        mTimelineSwipeRefresh = findViewById(R.id.swipe_refresh_timeline);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "On Load more getting more tweets!");
                getMoreTweets();
            }
        };

        mTwitterClient = TwitterApp.getRestClient(this);
        mTweets = new ArrayList<>();
        tagToUserMap = new Hashtable<>();
        mTweetsAdapter = new TweetsAdapter(this, mTweets,
                scrollListener, getTweetClickListener(), getOnUserTagClickedListener());

        mTimelineRecyclerViewer.setLayoutManager(layoutManager);
        mTimelineRecyclerViewer.setAdapter(mTweetsAdapter);
        mTimelineRecyclerViewer.addOnScrollListener(scrollListener);


        // add a horizontal separator between rows
        mTimelineRecyclerViewer.addItemDecoration(
                new DividerItemDecoration(mTimelineRecyclerViewer.getContext(),
                        DividerItemDecoration.VERTICAL));

        // set the loading indicator to cycle between four colors
        mTimelineSwipeRefresh.setColorSchemeResources(R.color.holo_red_dark,
                R.color.holo_blue_dark, R.color.DarkSalmon, R.color.android_green);

        mTimelineSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching new data.");
                populateHomeTimeline();
            }
        });

        // send get tweets request and populate timeline recycle view
        populateHomeTimeline();
    }

    private TweetsAdapter.OnUserTagClickedListener getOnUserTagClickedListener() {
        return new TweetsAdapter.OnUserTagClickedListener() {
            @Override
            public void onItemClicked(String tag) {
                ScreenNameId sid = tagToUserMap.get(tag);
                if (sid == null) {
                    final String msg = "Cannot access user page";
                    Log.e(TAG, msg);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
                else {
                    mTwitterClient.getUser(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            // start a new intent
                            try {
                                Intent userIntent = new Intent(getApplicationContext(), UserActivity.class);
                                if (json.jsonObject != null) {
                                    userIntent.putExtra(UserActivity.class.getSimpleName(), Parcels.wrap(User.fromJson(json.jsonObject)));
                                    Toast.makeText(getApplicationContext(), "Activity Starting", Toast.LENGTH_SHORT).show();
                                    startActivity(userIntent);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                final String msg = "Failed to get User object response";
                                Log.e(TAG, msg, e);
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            String msg = "Failed to get User object response";
                            Log.e(TAG, response, throwable);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }, sid.id, sid.screenName);
                }
            }
        };
    }

    private TweetsAdapter.OnClickListener getTweetClickListener() {
        return new TweetsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                showReplyTweetDialog(position);
            }
        };
    }

    private void showReplyTweetDialog(int pos) {
        FragmentManager fm = getSupportFragmentManager();
        ReplyTweetFragment editNameDialogFragment = ReplyTweetFragment.newInstance(pos);
        editNameDialogFragment.show(fm, "fragment_reply_tweet");
    }


    private void getMoreTweets() {
        mTwitterClient.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.i(TAG, "More tweets loaded");
                    List<Tweet> tweets = Tweet.fromJsonArray(json.jsonArray);
                    mTweetsAdapter.addAll(tweets);
                    addUsersToMap(tweets);
                } catch (JSONException e) {
                    Log.e(TAG, "Json Exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Error retrieving posts" + response, throwable);
            }
        }, mTweets.get(mTweets.size() - 1).id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.composeMenuItem) {
            Intent composeIntent = new Intent(this, ComposeActivity.class);

            // we want the compose activity to return a tweet object
            startActivityForResult(composeIntent, REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // get the tweet and update the recycler view with this tweet
            if (data == null) {
                Log.e(TAG, "Intent returned is null");
            } else {
                Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
                mTweets.add(0, tweet);
                mTweetsAdapter.notifyItemInserted(0);
                mTimelineRecyclerViewer.smoothScrollToPosition(0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addUsersToMap(List<Tweet> tweets) {
        for (Tweet tweet: tweets) {
            tagToUserMap.put("@" + tweet.user.screenName, tweet.user.screenNameId);
            if (tweet.entity.userMentions != null) {
                for (UserMention mention : tweet.entity.userMentions) {
                    tagToUserMap.put("@" + mention.screenName,
                            new ScreenNameId(mention.id, mention.screenName));
                }
            }
        }
    }

    private void populateHomeTimeline() {
        mTwitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Json Success!");
                try {
                    Log.i(TAG, "Content" + json.toString());
                    mTweetsAdapter.clear();
                    List<Tweet> tweets = Tweet.fromJsonArray(json.jsonArray);
                    mTweetsAdapter.addAll(tweets);
                    addUsersToMap(tweets);

                    // refreshing is finished and so we no longer need to show `loading` indicator
                    mTimelineSwipeRefresh.setRefreshing(false);
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

    private void quit() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            quit();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.exit_press_back_twice_message), Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void sendInput(String status, int pos) {
        Log.d(TAG, "sendInput: got the input");
        Log.i(TAG, status);
        Tweet tweet = mTweets.get(pos);
        mTwitterClient.publishReplyTweet(status,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Success" + json);
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Couldn't publish reply tweet", throwable);
                    }
                }, tweet.id, "@" + tweet.user.screenName);
    }
}
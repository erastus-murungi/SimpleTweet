package com.codepath.apps.erastustweet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.erastustweet.models.Tweet;
import com.codepath.apps.erastustweet.utilities.TweetEditTextBehavior;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Objects;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_CHARS = 140;

    private EditText mComposeTweetEditText;
    private Button mComposeTweetButton;
    private TwitterClient mTwitterClient;
    private TextView mCharCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Objects.requireNonNull(getSupportActionBar()).
                setBackgroundDrawable(new ColorDrawable(getColor(R.color.twitter_blue)));

        mTwitterClient = TwitterApp.getRestClient(this);
        mCharCountTextView = (TextView) findViewById(R.id.text_view_char_count);
        mComposeTweetButton = (Button) findViewById(R.id.button_compose_tweet);
        mComposeTweetEditText = (EditText) findViewById(R.id.edit_text_compose_tweet);

        TweetEditTextBehavior.setComposeTweetEditTextBehaviour(this,
                mComposeTweetEditText, mCharCountTextView, mComposeTweetButton);
        setComposeTweetButtonBehavior();

    }

    private void setComposeTweetButtonBehavior() {
        mComposeTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mComposeTweetEditText.getText().toString();
                mTwitterClient.publishTweet(content, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Tweet published successfully");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Exception", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        String msg = "Couldn't post tweet";
                        Log.e(TAG, msg, throwable);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
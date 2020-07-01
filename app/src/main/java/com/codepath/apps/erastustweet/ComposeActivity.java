package com.codepath.apps.erastustweet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";

    public static final int MAX_TWEET_CHARS = 140;
    EditText composeTweetEditText;
    Button composeTweetButton;
    TwitterClient twitterClient;
    TextView charCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        twitterClient = TwitterApp.getRestClient(this);
        composeTweetButton = (Button) findViewById(R.id.button_compose_tweet);
        composeTweetEditText = (EditText) findViewById(R.id.edit_text_compose_tweet);
        charCountTextView = (TextView) findViewById(R.id.text_view_char_count);

        setComposeTweetEditTextBehaviour();
        setComposeTweetButtonBehavior();

    }

    private void setComposeTweetEditTextBehaviour() {
        composeTweetEditText.addTextChangedListener(new TextWatcher() {
            // to prevent an infinite loop because TextWatcher is called
            // again inside the afterTextChanged method
            private boolean mChangedByTextWatcher = false;

            // This method is called to notify you that, within s,
            // the count characters beginning at start are about to be replaced
            // by new text with length after.
            // It is an error to attempt to make changes to s from this callback.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (start + after > MAX_TWEET_CHARS) {
                    composeTweetButton.setClickable(false);
                }
            }

            // This method is called to notify you that, within s, the count
            // characters beginning at start have just replaced old text that had length before.
            // It is an error to attempt to make changes to s from this callback.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tweetBody = composeTweetEditText.getText().toString().trim();
                if (tweetBody.length() > MAX_TWEET_CHARS) {
                    composeTweetButton.setEnabled(false);
                    charCountTextView.setTextColor(getColor(R.color.red));
                    String length = "-" + (s.length() - MAX_TWEET_CHARS);
                    charCountTextView.setText(length);

                } else if (tweetBody.length() == 0) {
                    composeTweetButton.setEnabled(false);
                    charCountTextView.setText(0);
                } else {
                    composeTweetButton.setEnabled(true);
                    charCountTextView.setTextColor(getColor(R.color.green));
                    charCountTextView.setText(String.valueOf(s.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > MAX_TWEET_CHARS) {
                    if (mChangedByTextWatcher) {
                        return;
                    }
                    mChangedByTextWatcher = true;

                    // cursor position will be reset to 0, so save it
                    int cursorPosition = composeTweetEditText.getSelectionStart();

                    Spannable tweetColored = new SpannableString(s);
                    tweetColored.setSpan(new BackgroundColorSpan(getColor(R.color.RedHighlight)),
                            140, s.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    composeTweetEditText.setText(tweetColored);

                    composeTweetEditText.setSelection(cursorPosition);

                    // release, so the TextWatcher can start to listen again.
                    mChangedByTextWatcher = false;

                }
            }
        });
    }

    private void setComposeTweetButtonBehavior() {
        composeTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = composeTweetEditText.getText().toString();
                twitterClient.publishTweet(content, new JsonHttpResponseHandler() {
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
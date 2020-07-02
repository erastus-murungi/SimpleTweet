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
        mComposeTweetButton = (Button) findViewById(R.id.button_compose_tweet);
        mComposeTweetEditText = (EditText) findViewById(R.id.edit_text_compose_tweet);
        mCharCountTextView = (TextView) findViewById(R.id.text_view_char_count);

        setComposeTweetEditTextBehaviour();
        setComposeTweetButtonBehavior();

    }

    private void setComposeTweetEditTextBehaviour() {
        mComposeTweetEditText.addTextChangedListener(new TextWatcher() {
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
                    mComposeTweetButton.setClickable(false);
                }
            }

            // This method is called to notify you that, within s, the count
            // characters beginning at start have just replaced old text that had length before.
            // It is an error to attempt to make changes to s from this callback.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tweetBody = mComposeTweetEditText.getText().toString().trim();
                if (tweetBody.length() > MAX_TWEET_CHARS) {
                    mComposeTweetButton.setEnabled(false);
                    mCharCountTextView.setTextColor(getColor(R.color.red));
                    String length = "-" + (s.length() - MAX_TWEET_CHARS);
                    mCharCountTextView.setText(length);

                } else if (tweetBody.length() == 0) {
                    mComposeTweetButton.setEnabled(false);
                    mCharCountTextView.setText(0);
                } else {
                    mComposeTweetButton.setEnabled(true);
                    mCharCountTextView.setTextColor(getColor(R.color.green));
                    mCharCountTextView.setText(String.valueOf(s.length()));
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
                    int cursorPosition = mComposeTweetEditText.getSelectionStart();

                    Spannable tweetColored = new SpannableString(s);
                    tweetColored.setSpan(new BackgroundColorSpan(getColor(R.color.RedHighlight)),
                            140, s.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    mComposeTweetEditText.setText(tweetColored);

                    mComposeTweetEditText.setSelection(cursorPosition);

                    // release, so the TextWatcher can start to listen again.
                    mChangedByTextWatcher = false;

                }
            }
        });
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
package com.codepath.apps.erastustweet.utilities;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.erastustweet.R;

public class TweetEditTextBehavior {
    public static final int MAX_TWEET_CHARS = 280;

    public static void setComposeTweetEditTextBehaviour(final Context context,
                                                         final EditText textBody, final TextView charCount,
                                                         final Button submitText) {
        textBody.addTextChangedListener(new TextWatcher() {
            // to prevent an infinite loop because TextWatcher is called
            // again inside the afterTextChanged method
            private boolean mChangedByTextWatcher = false;

            // This method is called to notify you that, within s,
            // the count characters beginning at start are about to be replaced
            // by new text with length after.
            // It is an error to attempt to make changes to s from this callback.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            // This method is called to notify you that, within s, the count
            // characters beginning at start have just replaced old text that had length before.
            // It is an error to attempt to make changes to s from this callback.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tweetBody = textBody.getText().toString().trim();
                if (tweetBody.length() > MAX_TWEET_CHARS) {
                    submitText.setEnabled(false);
                    charCount.setTextColor(context.getColor(R.color.red));
                    String length = "-" + (s.length() - MAX_TWEET_CHARS);
                    charCount.setText(length);

                } else if (tweetBody.length() == 0) {
                    submitText.setEnabled(false);
                    charCount.setTextColor(context.getColor(R.color.green));
                    charCount.setText(context.getString(R.string.zero));
                } else {
                    submitText.setEnabled(true);
                    charCount.setTextColor(context.getColor(R.color.green));
                    charCount.setText(String.valueOf(s.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (s.toString().trim().length() > MAX_TWEET_CHARS) {
                        if (mChangedByTextWatcher) {
                            return;
                        }
                        mChangedByTextWatcher = true;

                        // cursor position will be reset to 0, so save it
                        int cursorPosition = textBody.getSelectionStart();

                        Spannable tweetColored = new SpannableString(s);
                        tweetColored.setSpan(new BackgroundColorSpan(context.getColor(R.color.RedHighlight)),
                                MAX_TWEET_CHARS, s.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                        textBody.setText(tweetColored);

                        textBody.setSelection(cursorPosition);

                        // release, so the TextWatcher can start to listen again.
                        mChangedByTextWatcher = false;
                    }
                }
            }
        });
    }
}

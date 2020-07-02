package com.codepath.apps.erastustweet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.erastustweet.R;

import org.jetbrains.annotations.NotNull;


public class ReplyTweetFragment extends DialogFragment {


    public static final String TAG = "ReplyTweetFragment";
    private EditText mReplyEditText;
    private TextView mCancelTextView;
    private Button mReplyButton;
    private TextView mCharCountTextView;
    private OnInputListener mOnInputListener;

    public interface OnInputListener {
        void sendInput(String string);
    }

    public ReplyTweetFragment() {
    }

    public static ReplyTweetFragment newInstance(String title) {
        ReplyTweetFragment frag = new ReplyTweetFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_reply_tweet, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mReplyEditText = (EditText) view.findViewById(R.id.edit_text_reply_tweet);
        // Fetch arguments from bundle and set title
        // Show soft keyboard automatically and request focus to field


        mReplyButton = view.findViewById(R.id.btn_reply);
        mCancelTextView = view.findViewById(R.id.tv_cancel);
        mCharCountTextView = view.findViewById(R.id.text_view_char_count);
        mReplyEditText.requestFocus();

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setReplyButtonBehavior();
    }


    private void setReplyButtonBehavior() {
        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mReplyEditText.getText().toString();
                mOnInputListener.sendInput(content);
                dismiss();
            }
        });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach", e);
        }
    }
}
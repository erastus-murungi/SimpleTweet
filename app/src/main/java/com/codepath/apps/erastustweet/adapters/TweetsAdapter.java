package com.codepath.apps.erastustweet.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.erastustweet.EndlessRecyclerViewScrollListener;
import com.codepath.apps.erastustweet.PatternEditableBuilder;
import com.codepath.apps.erastustweet.R;
import com.codepath.apps.erastustweet.models.MediaType;
import com.codepath.apps.erastustweet.models.Tweet;
import com.codepath.apps.erastustweet.models.UserMention;


import java.util.List;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.TweetsViewHolder> {
    private Context context;
    private List<Tweet> tweets;
    private EndlessRecyclerViewScrollListener mOnScrollListener;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    public TweetsAdapter(Context context, List<Tweet> tweets, EndlessRecyclerViewScrollListener onScrollListener) {
        this.context = context;
        this.tweets = tweets;
        this.mOnScrollListener = onScrollListener;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public TweetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new TweetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetsViewHolder holder, int position) {
        // get the tweet at index `position` and bind it to a (view holder) row
        holder.bind(tweets.get(position));
    }

    // clears all the items in in the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
        mOnScrollListener.resetState();
    }

    //
    public void addAll(List<Tweet> newTweets) {
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    public class TweetsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView screenNameTextView, tweetBodyTextView, nameTextView;
        ImageView profilePictureImageView, verifiedBadgeImageView;
        VideoView twitterVideoView;
        ImageView twitterImageView;
        Spannable tweetBody;

        public TweetsViewHolder(@NonNull View itemView) {
            super(itemView);
            screenNameTextView = (TextView) itemView.findViewById(R.id.screenNameTextView);
            tweetBodyTextView = (TextView) itemView.findViewById(R.id.tweetBodyTextView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            profilePictureImageView = (ImageView) itemView.findViewById(R.id.profilePictureImageView);
            verifiedBadgeImageView = (ImageView) itemView.findViewById(R.id.verifiedBadgeTextView);
            twitterImageView = (ImageView) itemView.findViewById(R.id.image_view_twitter);
            twitterVideoView = (VideoView) itemView.findViewById(R.id.video_view_twitter);
        }

        public void bind(Tweet tweet) {
            screenNameTextView.setText(String.format("@%s \u00B7 %s", tweet.user.screenName, tweet.createdAt));
            nameTextView.setText(tweet.user.name);
            loadCircularImage(context, tweet.user.profilePictureUrl, profilePictureImageView);
            if (!tweet.user.isVerified) {
                verifiedBadgeImageView.setVisibility(View.GONE);
            }
            displayMedia(tweet, twitterImageView, twitterVideoView);
            tweetBody = new SpannableStringBuilder(tweet.body);
            colorHashTags(tweetBody, tweet);
            colorUserMentions(tweetBodyTextView);
            tweetBodyTextView.setText(tweetBody);
            tweetBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        @Override
        public void onClick(View v) {
        }
    }


    private void displayMedia(@NonNull Tweet tweet, ImageView targetImageView, VideoView videoView) {
        MediaType mediaType = tweet.entity.mediaType;
        if (mediaType != null) {
            switch (mediaType) {
                case IMAGE:
                    videoView.setVisibility(View.GONE);
                    targetImageView.setVisibility(View.VISIBLE);
                    loadRoundImage(context, tweet.entity.entities[0], targetImageView);
                    break;
                case ANIMATED_GIF:
                    videoView.setVisibility(View.GONE);
                    targetImageView.setVisibility(View.VISIBLE);
                    displayGif(context, tweet.entity.entities[0], targetImageView);
                    break;
                case VIDEO:
                    break;
                default:
                    videoView.setVisibility(View.GONE);
                    targetImageView.setVisibility(View.GONE);
            }
        } else {
            videoView.setVisibility(View.GONE);
            targetImageView.setVisibility(View.GONE);
        }

    }

    // Display a circular image in an imageView using the Glide Library
    public static void loadCircularImage(Context context, String imageUrl, ImageView targetImageView) {
        Glide.with(context)
                .load(imageUrl)
                .circleCrop()
                .into(targetImageView);
    }

    // display image with rounded corners
    public static void loadRoundImage(Context context, String imageUrl, ImageView targetImageView) {
        Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .apply(new RequestOptions()
                        .transform(new RoundedCornersTransformation(30, 0)))
                .into(targetImageView);
    }

    // get the index of the last character of a word starting index `start`
    private int getWordLastCharIndex(CharSequence sequence, int start) {
        int end = start;
        Log.i("Adapter", "Character: " + sequence.charAt(start));
        while (end < sequence.length() && !Character.isWhitespace(sequence.charAt(end++))) ;
        return end;
    }

    private void colorHashTags(@NonNull Spannable string, Tweet tweet) {
        if (tweet.entity.hashTagIndices != null) {
            for (int[] arr : tweet.entity.hashTagIndices) {
                for (int index : arr) {
                    // can also make the text clickable
                    string.setSpan(new ForegroundColorSpan(context.getColor(R.color.twitter_blue)),
                            index, getWordLastCharIndex(string, index),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private void colorUserMentions(@NonNull TextView textView) {
        new PatternEditableBuilder().
                addPattern(Pattern.compile("@(\\w+)"), context.getColor(R.color.twitter_blue),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(context, "Clicked username: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(textView);
    }

    private void displayGif(Context context, String imageUrl, ImageView targetImageView) {
        Glide.with(context)
                .asGif()
                .load(imageUrl)
                .fitCenter()
                .apply(new RequestOptions()
                        .transform(new RoundedCornersTransformation(30, 0)))
                .into(targetImageView);
    }
}

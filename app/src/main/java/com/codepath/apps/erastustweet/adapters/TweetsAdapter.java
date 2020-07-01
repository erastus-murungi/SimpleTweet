package com.codepath.apps.erastustweet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.erastustweet.R;
import com.codepath.apps.erastustweet.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.TweetsViewHolder> {
    private Context context;
    private List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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
    }

    //
    public void addAll(List<Tweet> newTweets) {
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return tweets.size(); }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    public class TweetsViewHolder extends RecyclerView.ViewHolder {
        TextView screenNameTextView, tweetBodyTextView, timestampTextView, nameTextView;
        ImageView profilePictureImageView, verifiedBadgeImageView;

        public TweetsViewHolder(@NonNull View itemView) {
            super(itemView);
            screenNameTextView = (TextView) itemView.findViewById(R.id.screenNameTextView);
            tweetBodyTextView = (TextView) itemView.findViewById(R.id.tweetBodyTextView);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestampTextView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            profilePictureImageView = (ImageView) itemView.findViewById(R.id.profilePictureImageView);
            verifiedBadgeImageView = (ImageView) itemView.findViewById(R.id.verifiedBadgeTextView);
        }

        public void bind(Tweet tweet) {
            screenNameTextView.setText(String.format("@%s", tweet.user.screenName));
            tweetBodyTextView.setText(tweet.body);
            timestampTextView.setText(tweet.createdAt);
            nameTextView.setText(tweet.user.name);
            loadRoundImage(context, tweet.user.profilePictureUrl, profilePictureImageView);
        }
    }

    /** Display a circular image in an imageView using the Glide Library */
    public static void loadRoundImage(Context context, String imageUrl, ImageView targetImageView) {
        Glide.with(context)
                .load(imageUrl)
                .circleCrop()
                .into(targetImageView);
    }
}

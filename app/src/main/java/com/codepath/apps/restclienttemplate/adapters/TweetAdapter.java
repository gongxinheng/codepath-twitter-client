package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.fragments.TweetDetailFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.Constants;

import org.parceler.Parcels;

import java.util.List;

public class TweetAdapter extends  RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;
    private TweetAdapterListener mListener;
    private FragmentManager fragmentManager;

    // define an interface required by the ViewHolder
    public interface TweetAdapterListener {
        void onItemSeleted(View view, int position);
    }

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener, @NonNull FragmentManager fragmentManager) {
        mTweets = tweets;
        mListener = listener;
        this.fragmentManager = fragmentManager;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemTweetBinding itemTweetBinding = ItemTweetBinding.inflate(inflater, parent, false);

        return new ViewHolder(itemTweetBinding, context);
    }

    // bind the values vased on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemTweetBinding binding;
        public final Context context;

        public ViewHolder(@NonNull ItemTweetBinding binding,
                          @NonNull final Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;

            // handle row click event
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        // get the position of row element
                        int position = getAdapterPosition();
                        // fire the listener callback
                        mListener.onItemSeleted(view, position);
                    }
                }
            });
        }

        public void bind(@NonNull final Tweet tweet) {
            binding.setTweet(tweet);
            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewProfile(tweet);
                }
            });

            if (tweet.mediaUrl == null) {
                binding.ivMedia.setVisibility(View.GONE);
            } else {
                binding.ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.mediaUrl).into(binding.ivMedia);
            }

            binding.ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TweetDetailFragment tweetDetailFragment = TweetDetailFragment.newInstance(tweet, true);
                    tweetDetailFragment.show(fragmentManager, Constants.FLAG_TWEET_DETAIL_FRAGMENT);
                }
            });

            binding.executePendingBindings();
        }
    }

    public void onViewProfile(@NonNull Tweet tweet) {
        // launch the profile view
        Intent i = new Intent(context, ProfileActivity.class);
        i.putExtra("user", Parcels.wrap(tweet.user));
        context.startActivity(i);
    }
}

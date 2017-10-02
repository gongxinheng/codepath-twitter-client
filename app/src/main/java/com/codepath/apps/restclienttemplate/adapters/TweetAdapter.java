package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.fragments.TweetDetailFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.Constants;

import java.util.List;

public class TweetAdapter extends  RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemTweetBinding binding;
        public final Context context;

        public ViewHolder(@NonNull ItemTweetBinding binding,
                          @NonNull final Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
        }

        public void bind(@NonNull final Tweet tweet) {
            binding.setTweet(tweet);
            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            binding.executePendingBindings();
            binding.getRoot().setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                            TweetDetailFragment tweetDetailFragment = TweetDetailFragment.newInstance(tweet);
                            tweetDetailFragment.show(fm, Constants.FLAG_COMPOSE_FRAGMENT);
                        }
                    }
            );
        }
    }
}

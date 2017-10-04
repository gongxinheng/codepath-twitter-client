package com.codepath.apps.restclienttemplate.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentTweetsListBinding;
import com.codepath.apps.restclienttemplate.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment {

    protected FragmentTweetsListBinding binding;
    protected ArrayList<Tweet> tweets;
    protected TweetAdapter tweetAdapter;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected boolean offlineMode;

    // inflation happends inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweets_list, container, false);

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // find the RecyclerView
        // init the arrayList (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.scrollToPosition(0);
        // RecyclerView setup (layout manager, use adapter)
        binding.rvTweets.setLayoutManager(layoutManager);
        // set the adapter
        binding.rvTweets.setAdapter(tweetAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(tweets.size());
            }
        };
        // Adds the scroll listener to RecyclerView
        binding.rvTweets.addOnScrollListener(scrollListener);
        return binding.getRoot();
    }

    public void addItems(@NonNull JSONArray response) {
        // iterate through the JSON array
        // for each entry, deserialize the JSON object
        for (int i = 0; i < response.length(); i++) {
            // convert each object to a Tweet model
            // add that Tweet model to our data source
            // notify the adapter that we've added an item
            Tweet tweet = null;
            try {
                tweet = Tweet.fronJSON(response.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tweets.add(tweet);
            tweet.save();
            tweetAdapter.notifyItemInserted(tweets.size() - 1);
        }
    }


    public void fetchTimelineAsync(int page) {
    }

    // Reset all views and clear items
    protected void reset() {
        scrollListener.resetState();
        tweets.clear();
        tweetAdapter.notifyDataSetChanged();
    }

    // Offline mode
    protected void populateTimeLine(@NonNull List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        tweetAdapter.notifyDataSetChanged();
    }

    // Append the next page of data into the adapter
    public void loadNextDataFromApi(int offset) {
    }
}

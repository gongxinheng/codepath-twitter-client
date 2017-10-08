package com.codepath.apps.restclienttemplate.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentTweetsListBinding;
import com.codepath.apps.restclienttemplate.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.Config;
import com.codepath.apps.restclienttemplate.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public abstract class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {

    protected TwitterClient client;
    protected FragmentTweetsListBinding binding;
    protected ArrayList<Tweet> tweets;
    protected TweetAdapter tweetAdapter;
    protected EndlessRecyclerViewScrollListener scrollListener;

    protected final JsonHttpResponseHandler defaultJsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("TwitterClient", response.toString());
            onReceiveResponce();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("TwitterClient", response.toString());
            onReceiveResponce();
            addItems(response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("TwitterClient", errorResponse.toString());
            onReceiveResponce();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            Log.d("TwitterClient", errorResponse.toString());
            onReceiveResponce();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d("TwitterClient", responseString);
            onReceiveResponce();
        }
    };

    public interface TweetSelectedListener {
        // handle tweet selection
        void onTweetSelected(Tweet tweet);
    }

    // inflation happends inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        client = TwitterApp.getRestClient(getContext());
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
        tweetAdapter = new TweetAdapter(tweets, this, getFragmentManager());
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

        // Add divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.rvTweets.getContext(),
                layoutManager.getOrientation());
        binding.rvTweets.addItemDecoration(dividerItemDecoration);
        return binding.getRoot();
    }

    @Override
    public void onItemSeleted(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet);
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
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getNewHomeTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Clear out old items before appending in the new ones
                reset();
                // ...the data has come back, add new items to your adapter...
                addItems(response);
                // Now we call setRefreshing(false) to signal refresh has finished
                binding.swipeContainer.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        }, 1, Constants.TWEETS_COUNT_PER_PAGE);
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

    // for inherited fragment to override
    protected abstract void getMoreTimeline(long maxId);

    // Append the next page of data into the adapter
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        if (Config.isOnline()) {
            if (tweets.isEmpty()) return;
            long maxId = tweets.get(tweets.size() - 1).uid - 1;
            getMoreTimeline(maxId);
        } else {
            populateTimeLine(Tweet.getTopOfflineTweets(offset, Constants.TWEETS_COUNT_PER_PAGE));
        }
    }

    private void onReceiveResponce() {
        if (getContext() instanceof TwitterClient.NetworkRequestListener) {
            ((TwitterClient.NetworkRequestListener) getContext()).onReceiveResponse();
        }
    }
}

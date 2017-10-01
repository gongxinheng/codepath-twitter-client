package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetFragment;
import com.codepath.apps.restclienttemplate.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.PostTweetListener {

    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    private EndlessRecyclerViewScrollListener scrollListener;

    private final JsonHttpResponseHandler defaultJsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("TwitterClient", response.toString());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("TwitterClient", response.toString());
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
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("TwitterClient", errorResponse.toString());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            Log.d("TwitterClient", errorResponse.toString());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d("TwitterClient", responseString);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient();

        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // init the arrayList (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.scrollToPosition(0);
        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(layoutManager);
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(tweets.size());
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
        populateTimeLine();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compose:
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance();
                composeTweetFragment.show(fm, Constants.FLAG_COMPOSE_FRAGMENT);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateTimeLine() {
        client.getNewHomeTimeline(defaultJsonHttpResponseHandler, 1, Constants.TWEETS_COUNT_PER_PAGE);
    }

    // Append the next page of data into the adapter
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        if (tweets.isEmpty()) return;
        long maxId = tweets.get(tweets.size() - 1).uid;
        client.getHomeTimeline(defaultJsonHttpResponseHandler, maxId, Constants.TWEETS_COUNT_PER_PAGE);
    }

    @Override
    public void onPostReturn(boolean success, @Nullable Tweet newTweet) {
        if (success && newTweet != null) {
            tweets.add(0, newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }
}

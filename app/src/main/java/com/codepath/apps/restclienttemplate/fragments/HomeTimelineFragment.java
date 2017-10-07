package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment implements ComposeTweetFragment.PostTweetListener {

    private final JsonHttpResponseHandler defaultJsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("TwitterClient", response.toString());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("TwitterClient", response.toString());
            addItems(response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            //Log.d("TwitterClient", errorResponse.toString());
            offlineMode = true;
            Toast.makeText(getContext(), "Offline mode on", Toast.LENGTH_LONG).show();
            // Load offline
            populateTimeLine(Tweet.getTopOfflineTweets(0, Constants.TWEETS_COUNT_PER_PAGE));
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
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        populateTimeLine();

        return root;
    }

    @Override
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

    @Override
    protected void getMoreTimeline(long maxId) {
        client.getHomeTimeline(defaultJsonHttpResponseHandler, maxId, Constants.TWEETS_COUNT_PER_PAGE);
    }

    private void populateTimeLine() {
        client.getNewHomeTimeline(defaultJsonHttpResponseHandler, 1, Constants.TWEETS_COUNT_PER_PAGE);
    }

    @Override
    public void onPostReturn(boolean success, @Nullable Tweet newTweet) {
        if (success && newTweet != null) {
            tweets.add(0, newTweet);
            newTweet.save();
            tweetAdapter.notifyItemInserted(0);
            binding.rvTweets.scrollToPosition(0);
        }
    }
}

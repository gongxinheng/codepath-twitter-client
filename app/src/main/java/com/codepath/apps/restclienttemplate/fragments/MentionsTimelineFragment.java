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

public class MentionsTimelineFragment extends TweetsListFragment {

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

    private void populateTimeLine() {
        client.getMentionsTimeline(defaultJsonHttpResponseHandler, 0, Constants.TWEETS_COUNT_PER_PAGE);
    }

    @Override
    protected void getMoreTimeline(long maxId) {
        client.getMentionsTimeline(defaultJsonHttpResponseHandler, maxId, Constants.TWEETS_COUNT_PER_PAGE);
    }
}

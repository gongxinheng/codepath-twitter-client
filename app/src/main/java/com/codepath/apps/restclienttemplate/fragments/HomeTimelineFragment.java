package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.utils.Constants;

public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        populateTimeLine();

        return root;
    }

    @Override
    protected void getMoreTimeline(long maxId) {
        client.getHomeTimeline(defaultJsonHttpResponseHandler, maxId, Constants.TWEETS_COUNT_PER_PAGE);
    }

    private void populateTimeLine() {
        client.getNewHomeTimeline(defaultJsonHttpResponseHandler, 1, Constants.TWEETS_COUNT_PER_PAGE);
    }
}

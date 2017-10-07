package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.utils.Constants;

public class UserTimelineFragment extends TweetsListFragment {

    private String screenName;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);

        return userTimelineFragment;
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        screenName = getArguments().getString("screen_name");
        populateTimeLine();

        return root;
    }

    private void populateTimeLine() {
        client.getUserTimeline(defaultJsonHttpResponseHandler, screenName, 0, Constants.TWEETS_COUNT_PER_PAGE);
    }

    @Override
    protected void getMoreTimeline(long maxId) {
        client.getUserTimeline(defaultJsonHttpResponseHandler, screenName, maxId, Constants.TWEETS_COUNT_PER_PAGE);
    }
}

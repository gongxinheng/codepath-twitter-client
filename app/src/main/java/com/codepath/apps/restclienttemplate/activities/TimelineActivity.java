package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetDetailFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.Constants;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener,
        TwitterClient.NetworkRequestListener {

    // Instance of the progress action-view
    private MenuItem miActionProgressItem;
    private ActivityTimelineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        binding.fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance();
                composeTweetFragment.show(fm, Constants.FLAG_COMPOSE_FRAGMENT);
            }
        });

        // set the adapter for the pager
        binding.viewpager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(), this));
        // setup the TabLayout to use the view pager
        binding.slidingTabs.setupWithViewPager(binding.viewpager);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        // ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        if (miActionProgressItem != null) {
            // Show progress item
            miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        if (miActionProgressItem != null) {
            // Hide progress item
            miActionProgressItem.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCompose:
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

    @Override
    public void onTweetSelected(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        TweetDetailFragment tweetDetailFragment = TweetDetailFragment.newInstance(tweet);
        tweetDetailFragment.show(fm, Constants.FLAG_TWEET_DETAIL_FRAGMENT);
    }

    public void onProfileView(MenuItem item) {
        // launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    public void onSendRequest() {
        showProgressBar();
    }

    @Override
    public void onReceiveResponse() {
        hideProgressBar();
    }
}

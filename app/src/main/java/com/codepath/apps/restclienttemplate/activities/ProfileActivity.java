package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private TwitterClient client;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        client = TwitterApp.getRestClient();

        // Current user
        if (user == null) {
            client.getUserProfile(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        user = User.fromJSON(response);
                        binding.setUser(user);
                        loadUserTimeline(user.screenName);
                    /*
                    binding.setUser(user);
                    Glide.with(getContext()).load(user.profileImageUrl).into(binding.ivProfileImage);
                    binding.executePendingBindings();
                    dataReady = true;*/
                        populateUserHeadline(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("TwitterClient", errorResponse.toString());
                };
            });
        } else {
            binding.setUser(user);
            loadUserTimeline(user.screenName);
            populateUserHeadline(user);
        }
    }

    public void populateUserHeadline(@NonNull User user) {
        // set the title of the ActionBar based on the user info
        getSupportActionBar().setTitle(user.screenName);
        binding.tvName.setText(user.name);

        // load profile image with Glide
        Glide.with(this).load(user.profileImageUrl).into(binding.ivProfileImage);
    }

    public void loadUserTimeline(String screenName) {
        // create the user fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

        // display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // make change
        ft.replace(R.id.flContainer, userTimelineFragment);
        // commit
        ft.commit();
    }
}

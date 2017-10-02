package com.codepath.apps.restclienttemplate.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.FragmentTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link TweetDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TweetDetailFragment extends DialogFragment {
    private static final String ARG_TWEET = "tweet";

    private FragmentTweetDetailBinding binding;
    private Tweet tweet;

    public TweetDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tweet Tweet info.
     * @return A new instance of fragment TweetDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TweetDetailFragment newInstance(@NonNull final Tweet tweet) {
        TweetDetailFragment fragment = new TweetDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TWEET, tweet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tweet = (Tweet) getArguments().getSerializable(ARG_TWEET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweet_detail, container, false);
        binding.setTweet(tweet);
        Glide.with(getContext()).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
        if (tweet.mediaUrl == null) {
            binding.ivMedia.setVisibility(View.GONE);
        } else {
            binding.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(tweet.mediaUrl).into(binding.ivMedia);
        }
        return binding.getRoot();
    }
}

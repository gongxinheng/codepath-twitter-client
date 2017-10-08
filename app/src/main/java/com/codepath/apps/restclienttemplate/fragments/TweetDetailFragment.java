package com.codepath.apps.restclienttemplate.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.databinding.FragmentTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link TweetDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TweetDetailFragment extends DialogFragment {
    private static final String ARG_TWEET = "tweet";
    private static final String ARG_ISREPLY = "isReply";

    private FragmentTweetDetailBinding binding;
    private Tweet tweet;
    private boolean isReply;
    private TwitterClient client;

    public TweetDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tweet Tweet info.
     * @param isReply True to initial focus reply editor
     * @return A new instance of fragment TweetDetailFragment.
     */
    public static TweetDetailFragment newInstance(@NonNull final Tweet tweet, boolean isReply) {
        TweetDetailFragment fragment = new TweetDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TWEET, Parcels.wrap(tweet));
        args.putBoolean(ARG_ISREPLY, isReply);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tweet = Parcels.unwrap(getArguments().getParcelable(ARG_TWEET));
            isReply = getArguments().getBoolean(ARG_ISREPLY);
        }
        client = TwitterApp.getRestClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweet_detail, container, true);
        if (tweet != null) {
            binding.setTweet(tweet);
            Glide.with(getContext()).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            if (tweet.mediaUrl == null) {
                binding.ivMedia.setVisibility(View.GONE);
            } else {
                binding.ivMedia.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(tweet.mediaUrl).into(binding.ivMedia);
            }
            if (isReply) {
                binding.tvReplyToName.setText("@" + tweet.user.screenName);
                binding.etReply.requestFocus();
            }

            binding.btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client.replyTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getActivity(), "Reply posted.", Toast.LENGTH_LONG).show();
                            onReceiveResponce();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("TwitterClient", errorResponse.toString());
                            onReceiveResponce();
                        }
                    }, binding.etReply.getText().toString(), tweet.uid);
                }
            });
        }
        return binding.getRoot();
    }

    private void onReceiveResponce() {
        if (getContext() instanceof TwitterClient.NetworkRequestListener) {
            ((TwitterClient.NetworkRequestListener) getContext()).onReceiveResponse();
        }
        dismiss();
    }
}

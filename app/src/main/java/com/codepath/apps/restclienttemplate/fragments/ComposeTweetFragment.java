package com.codepath.apps.restclienttemplate.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ComposeTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeTweetFragment extends DialogFragment {

    private static final String ARG_USER = "user";

    private FragmentComposeTweetBinding binding;
    private User user;
    private boolean dataReady;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ComposeTweetFragment.
     */
    public static ComposeTweetFragment newInstance() {
        return new ComposeTweetFragment();
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final TwitterClient twitterClient = TwitterApp.getRestClient(getContext());

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_tweet, container, false);
        binding.tvCharCounter.setText(String.valueOf(Constants.MAX_TWEET_CHARS_NUM));
        binding.etTweet.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsRemain = Constants.MAX_TWEET_CHARS_NUM - s.length();
                binding.tvCharCounter.setText(String.valueOf(charsRemain));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.btnTweet.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tweetText = binding.etTweet.getText().toString();
                if (!StringUtils.isNullOrEmpty(tweetText)) {
                    twitterClient.createTweet(new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                ((PostTweetListener) ComposeTweetFragment.this.getActivity()).onPostReturn(true, Tweet.fronJSON(response));
                                Toast.makeText(ComposeTweetFragment.this.getContext(), "Your tweet is posted!", Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(ComposeTweetFragment.this.getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            } finally {
                                ComposeTweetFragment.this.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            ((PostTweetListener) ComposeTweetFragment.this.getActivity()).onPostReturn(false, null);
                            Toast.makeText(ComposeTweetFragment.this.getContext(), errorResponse.toString(), Toast.LENGTH_LONG).show();
                            ComposeTweetFragment.this.dismiss();
                        }


                    }, tweetText);
                }
            }
        });
        twitterClient.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        user = User.fromJSON(response);
                        binding.setUser(user);
                        Glide.with(getContext()).load(user.profileImageUrl).into(binding.ivProfileImage);
                        binding.executePendingBindings();
                        dataReady = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
            }
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public interface PostTweetListener {
        void onPostReturn(boolean success, @Nullable Tweet newTweet);
    }
}

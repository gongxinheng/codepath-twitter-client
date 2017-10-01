package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {

    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;

    // deserialize the JSON
    public static Tweet fronJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values fron JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = Utils.getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        return tweet;
    }
}

package com.codepath.apps.restclienttemplate.models;

import android.support.annotation.NonNull;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.codepath.apps.restclienttemplate.utils.Utils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(database = MyDatabase.class)
public class Tweet extends BaseModel implements Serializable {

    @PrimaryKey
    @Column
    public long uid; // database ID for the tweet

    @Column
    public String body;

    @ForeignKey(saveForeignKeyModel = true,
            references = {@ForeignKeyReference(
            columnName = "user", foreignKeyColumnName = "uid")})
    public User user;

    @Column
    public String createdAt;

    @Column
    public String mediaUrl;

    // Record Finders
    public static Tweet byId(long uid) {
        return new Select().from(Tweet.class).where(Tweet_Table.uid.eq(uid)).querySingle();
    }

    // deserialize the JSON
    public static Tweet fronJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values fron JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = Utils.getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities != null && entities.has("media")) {
            JSONArray medias = entities.getJSONArray("media");
            if (medias.length() > 0) {
                JSONObject media = (JSONObject) medias.get(0);
                if (media.has("media_url")) {
                    tweet.mediaUrl = media.getString("media_url");
                }
            }
        }

        return tweet;
    }

    @NonNull
    public static List<Tweet> getTopOfflineTweets(int startPos, int count) {
        List<Tweet> tweets = SQLite.select()
                    .from(Tweet.class)
                    .orderBy(Tweet_Table.uid, false)
                    .queryList();

        try {
            tweets = tweets.subList(startPos, startPos + count);
        } catch (IndexOutOfBoundsException e) {
            return new ArrayList<>();
        }

        return tweets;
    }
}

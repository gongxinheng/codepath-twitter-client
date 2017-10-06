package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Table(database = MyDatabase.class)
@Parcel
public class User extends BaseModel {

    @PrimaryKey
    @Column
    public long uid = 0;

    @Column
    public String name = "";

    @Column
    public String screenName = "";

    @Column
    public String profileImageUrl = "";
    public String tagLine;
    public int followersCount;
    public int followingCount;

    // deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        // extract and fill the values
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");

        user.tagLine = json.getString("description");
        user.followersCount = json.getInt("followers_count");
        user.followingCount = json.getInt("friends_count");

        return user;
    }

    // Record Finders
    public static User byId(long uid) {
        return new Select().from(User.class).where(User_Table.uid.eq(uid)).querySingle();
    }
}

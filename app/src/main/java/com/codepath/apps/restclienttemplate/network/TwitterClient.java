package com.codepath.apps.restclienttemplate.network;

import android.content.Context;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    private static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    private static final String REST_URL = "https://api.twitter.com/1.1";
    private static final String REST_CONSUMER_KEY = "HCJoKjjpxbTBxMRgoUZKOMbsq";
    private static final String REST_CONSUMER_SECRET = "YyWB5SthnKCxVuPdfmSjo0fzH4cRBU7PuhyWZoCV03C443kKub";
    private static final String REST_HOME_TIMELINE_URL = "statuses/home_timeline.json";
    private static final String REST_MENTIONS_TIMELINE_URL = "statuses/mentions_timeline.json";
    private static final String REST_VERIFY_CREDENTIAL_URL = "account/verify_credentials.json";
    private static final String REST_USER_TIMELINE_URL = "statuses/user_timeline.json";
    private static final String REST_STATUSES_UPDATE_URL =  "statuses/update.json";

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    private static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
    private static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getNewHomeTimeline(AsyncHttpResponseHandler handler, long sinceId, int count) {
		String apiUrl = getApiUrl(REST_HOME_TIMELINE_URL);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", count);
		params.put("since_id", sinceId);
		client.get(apiUrl, params, handler);
	}

    public void getHomeTimeline(AsyncHttpResponseHandler handler, long maxId, int count) {
        String apiUrl = getApiUrl(REST_HOME_TIMELINE_URL);
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", count);
        params.put("max_id", maxId);
        client.get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler, long maxId, int count) {
        String apiUrl = getApiUrl(REST_MENTIONS_TIMELINE_URL);
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", count);
        params.put("max_id", maxId);
        client.get(apiUrl, params, handler);
    }

    public void getUserProfile(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(REST_VERIFY_CREDENTIAL_URL);
        client.get(apiUrl, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler, int count) {
        String apiUrl = getApiUrl(REST_USER_TIMELINE_URL);
        RequestParams params = new RequestParams();
        params.put("count", count);
        params.put("screen_name", screenName);
        client.get(apiUrl, params, handler);
    }

    public void createTweet(AsyncHttpResponseHandler handler, String tweetText) {
        String apiUrl = getApiUrl(REST_STATUSES_UPDATE_URL);
        RequestParams params = new RequestParams();
        params.put("status", tweetText);
        client.post(apiUrl, params, handler);
    }
}

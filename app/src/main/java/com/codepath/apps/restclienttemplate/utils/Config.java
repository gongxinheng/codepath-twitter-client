package com.codepath.apps.restclienttemplate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class Config {
    public static boolean isOnline() {
        return isOnline;
    }

    public static void setOnline(@NonNull final Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        isOnline = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isOnline) {
            Toast.makeText(context, "Offline mode on", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isOnline;
}

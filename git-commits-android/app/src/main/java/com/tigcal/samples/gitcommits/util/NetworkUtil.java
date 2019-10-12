package com.tigcal.samples.gitcommits.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkUtil {
    private static final String BASE_URL = "https://api.github.com/repos/%s/%s/commits";

    public static String getCommitsUrl(final String username, final String repository) {
        return String.format(BASE_URL, username, repository);
    }

    private static RequestQueue sRequestQueue;

    private static RequestQueue getRequestQueue(Context context) {
        if(sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return sRequestQueue;
    }

    public static void addToRequestQueue(Context context, Request request) {
        getRequestQueue(context).add(request);
    }

    public static void cancelRequests(Context context, String requestTag) {
        getRequestQueue(context).cancelAll(requestTag);
    }
}

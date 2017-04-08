package com.example.toto.aiapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by toto on 17/4/8.
 */

public class VolleyUtils {
    private RequestQueue requestQueue;
    private static VolleyUtils mInstance;
    private Context mContext;

    public static VolleyUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtils(context);
        }
        return mInstance;
    }

    public VolleyUtils(Context context) {
        mContext = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void sendRequest(Request request) {
        requestQueue.add(request);
    }
}

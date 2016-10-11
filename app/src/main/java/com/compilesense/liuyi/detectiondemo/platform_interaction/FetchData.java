package com.compilesense.liuyi.detectiondemo.platform_interaction;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.compilesense.liuyi.detectiondemo.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenjingyuan002 on 16/9/6.
 */
public class FetchData {
    private final String TAG = "FetchData";
    private FetchData(){};
    private static FetchData instance = new FetchData();
    public static FetchData getInstance() {
        return instance;
    }

    public void fetch(final Context context, String url, final Map<String,String> extraParams , final ResponseListener listener){
        PostRequest.getInstance().post(context, url, extraParams, listener);
    }

    public void fetch(final Context context, String url, final ResponseListener listener){
        PostRequest.getInstance().post(context, url, listener);
    }

}

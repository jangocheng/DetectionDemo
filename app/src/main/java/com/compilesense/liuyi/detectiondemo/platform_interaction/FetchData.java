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
        RequestQueue queue = Volley.newRequestQueue(context);

        MultipartRequest request = new MultipartRequest(
                Request.Method.POST,
                url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String parsed;
                        try {
                            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            listener.success(parsed);

                        } catch (UnsupportedEncodingException e) {
                            parsed = new String(response.data);
                        }
                        Log.d(TAG,"onResponse:"+parsed);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.failed();
                        Log.e(TAG,error.toString());
                    }
                }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_id", context.getString(R.string.api_id));
                params.put("api_secret", context.getString(R.string.api_secret));

                if (extraParams != null && !extraParams.isEmpty()){
                    params.putAll(extraParams);
                }
                return params;
            }

//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                try {
//                    byte[] b = Util.uri2ByteArray(bitmap,context);
//                    params.put("image", new DataPart("jiang.jpg", b, "image/jpeg"));
//                }catch (Exception e){
//                    Log.e(TAG,e.toString());
//                }
//
//                return params;
//            }
        };
        queue.add(request);
    }

    public void fetch(final Context context, String url, final ResponseListener listener){
        fetch(context,url,null,listener);
    }

}

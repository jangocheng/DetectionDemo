package com.compilesense.liuyi.detectiondemo.platform_interaction.apis;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.PostRequest;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.utils.CacheUtils;
import com.compilesense.liuyi.detectiondemo.utils.Constans;

/**
 * Created by shenjingyuan002 on 16/9/5.
 */
public class DetectAge {
    private static final String TAG = "DetectAge";
    public static final String ACTION = "/Detection/Age";
    private DetectAge(){};
    private static class singleton{
        private static DetectAge instance = new DetectAge();
    }
    public static DetectAge getInstance() {
        return singleton.instance;
    }

    public void detect(final Context context, final Uri bitmap, final ResponseListener listener){
        String baseUrl= CacheUtils.getString(context, Constans.API_URL,"");
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = context.getString(R.string.api_url);
        }
        String url = baseUrl  + ACTION;
        PostRequest.getInstance().post(context, url, bitmap, listener);
    }

    public void detect(final Context context, final Bitmap bitmap, final ResponseListener listener){
        String baseUrl= CacheUtils.getString(context, Constans.API_URL,"");
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = context.getString(R.string.api_url);
        }
        String url = baseUrl + ACTION;
        PostRequest.getInstance().post(context, url, bitmap, listener);
    }

//    public void post(final Context context, final Uri bitmap, final ResponseListener listener){
//
//        String url = context.getString(R.string.api_url) + action;
//
//        RequestQueue queue = Volley.newRequestQueue(context);
//        MultipartRequest request = new MultipartRequest(
//                Request.Method.POST,
//                url,
//                new Response.Listener<NetworkResponse>() {
//                    @Override
//                    public void onResponse(NetworkResponse response) {
//                        String parsed;
//                        try {
//                            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                            listener.success(parsed);
//
//                        } catch (UnsupportedEncodingException e) {
//                            parsed = new String(response.data);
//                        }
//                        Log.d(TAG,"onResponse:"+parsed);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        listener.failed();
//                        Log.e(TAG,error.toString());
//                    }
//                }){
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("api_id", context.getString(R.string.api_id));
//                params.put("api_secret", context.getString(R.string.api_secret));
//                return params;
//            }
//
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
//        };
//
//        queue.add(request);
//    }
}

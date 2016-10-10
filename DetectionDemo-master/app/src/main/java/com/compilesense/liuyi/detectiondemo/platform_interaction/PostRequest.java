package com.compilesense.liuyi.detectiondemo.platform_interaction;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.utils.Util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装MultipartRequest的调用
 * Created by shenjingyuan002 on 16/9/5.
 */
public class PostRequest {
    private static final String TAG = "PostRequest";
    private PostRequest(){};
    private static class singleton{
        static PostRequest instance = new PostRequest();
    }
    public static PostRequest getInstance() {
        return singleton.instance;
    }

    public void post(final Context context, String url, final Uri[] bitmapUris, final Bitmap[] bitmaps, final Map<String, String> extraParams, final ResponseListener listener){

        Map<String, String> headers = new HashMap<>();
        headers.put("Charset","UTF-8");

        RequestQueue queue = Volley.newRequestQueue(context);
        MultipartRequest request = new MultipartRequest(
                url,
                headers,
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

            @Override
            protected Map<String, DataPart> getByteData() {

                boolean noneUri = (bitmapUris == null || bitmapUris.length == 0);
                boolean noneBitmap = (bitmaps == null || bitmaps.length == 0);

                if (noneUri && noneBitmap){
                    return null;
                }

                Map<String, DataPart> params = new HashMap<>();

                if (!noneUri){
                    for (int i = 0; i < bitmapUris.length; i++){
                        Uri imageUri = bitmapUris[i];
                        try {
                            byte[] b = Util.uri2ByteArray(imageUri,context);
                            params.put("image"+i, new DataPart("image" + i + ".jpg", b, "image/jpeg"));
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
                        }
                    }

                }

                if (!noneBitmap){
                    for (int i = 0; i < bitmaps.length; i++){
                        Bitmap bitmap = bitmaps[i];
                        byte[] b = Util.bitmap2ByteArray(bitmap);
                        params.put("image" + i + 1,new DataPart("j"+i+".jpg", b, "image/jpeg"));
                    }
                }

                return params;
            }
        };

        queue.add(request);
    }

    /**
     *  Uri
     * @param context
     * @param url
     * @param bitmapUris
     * @param extraParams
     * @param listener
     */
    public void post(Context context,String url, Uri[] bitmapUris, Map<String, String> extraParams, ResponseListener listener){
        Bitmap[] bitmaps = null;
        post(context,url,bitmapUris,bitmaps,extraParams,listener);
    }

    public void post(Context context,String url, Uri bitmapUri, Map<String, String> extraParams, ResponseListener listener){
        Uri[] bitmaps = {bitmapUri};
        post(context,url,bitmaps,extraParams,listener);
    }

    public void post(Context context, String url, Uri bitmapUri, ResponseListener listener){
        post(context,url,bitmapUri,null,listener);
    }


    /**
     * bitmap
     * @param context
     * @param url
     * @param bitmaps
     * @param extraParams
     * @param listener
     */
    public void post(Context context,String url, Bitmap[] bitmaps, Map<String, String> extraParams, ResponseListener listener){
        Uri[] bitmapUris = null;
        post(context,url,bitmapUris,bitmaps,extraParams,listener);
    }

    public void post(Context context,String url, Bitmap bitmap, Map<String, String> extraParams, ResponseListener listener){
        Bitmap[] bitmaps = {bitmap};
        post(context,url,bitmaps,extraParams,listener);
    }

    public void post(Context context, String url, Bitmap bitmap, ResponseListener listener){
        post(context,url,bitmap,null,listener);
    }


    public void post(Context context, String url, Map<String, String> extraParams, ResponseListener listener){
        Uri[] bitmaps = null;
        post(context,url,bitmaps,extraParams,listener);
    }

    public void post(Context context, String url, ResponseListener listener){
        Uri[] bitmaps = null;
        post(context,url,bitmaps,null,listener);
    }

}

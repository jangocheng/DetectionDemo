package com.compilesense.liuyi.detectiondemo.platform_interaction.apis;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.PostRequest;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;

/**
 * Created by shenjingyuan002 on 16/9/8.
 */
public class RecognizeTwoFace {
    private static final String ACTION = "/Recognition/Recognition";

    public void recognize(Context context, Uri face1, Uri face2, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION;
        Uri[] uris = {face1,face2};
        PostRequest.getInstance().post(context, url, uris, null, listener);
    }
    public void recognize(Context context, Uri face1, Bitmap face2, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION;
        Uri[] uris = {face1};
        Bitmap[] bitmaps = {face2};
        PostRequest.getInstance().post(context, url, uris, bitmaps, null, listener);
    }
    public void recognize(Context context, Bitmap face1, Uri face2, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION;
        Bitmap[] uris = {face1};
        Uri[] bitmaps = {face2};
        PostRequest.getInstance().post(context, url, bitmaps, uris, null, listener);
    }
    public void recognize(Context context, Bitmap face1, Bitmap face2, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION;
        Bitmap[] bitmaps = {face1, face2};
        PostRequest.getInstance().post(context, url, bitmaps, null, listener);
    }

}

package com.compilesense.liuyi.detectiondemo.platform_interaction.apis;

import android.content.Context;
import android.net.Uri;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.PostImage;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;

/**
 * Created by shenjingyuan002 on 16/9/5.
 */
public class DetectImgProperties {
    private static final String ACTION = "/Detection/ImgProperties";
    private DetectImgProperties(){};
    private static DetectImgProperties instance = new DetectImgProperties();
    public static DetectImgProperties getInstance() {
        return instance;
    }

    public void detect(final Context context, final Uri bitmap, final ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION;
        PostImage.getInstance().post(context, url, bitmap, listener);
    }
}

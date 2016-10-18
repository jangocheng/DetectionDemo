package com.compilesense.liuyi.detectiondemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.compilesense.liuyi.detectiondemo.view.XDialog;

/**
 * 因为较多的activity都会有图片获取的操作,这里封装一下
 * Created by shenjingyuan002 on 16/9/12.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private final String TAG = "BaseActivity";
    private final int  REQUEST_IMAGE_ALBUM = 1, REQUEST_IMAGE_CAPTURE = 2;

    private GetImageListener listener;
    private boolean busy = false;
    abstract void onDialogClick(int which);

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            busy = false;
            return;
        }

        switch (requestCode){
            case REQUEST_IMAGE_ALBUM:
                if (data != null) {
                    Uri imageUri = data.getData();
                    listener.getImage(imageUri,null);
                }
                busy = false;
                break;
            case REQUEST_IMAGE_CAPTURE:

                Bitmap bitmap;
                try {
                    bitmap = data.getExtras().getParcelable("data");
                    listener.getImage(null,bitmap);
                } catch (ClassCastException e){
                    e.printStackTrace();
                }
                busy = false;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     *
     *
     *
     * 创建对话框获取图片
     */
    public void getImage(GetImageListener listener){

        //不想出现listener被覆盖的情况,所以只可以在一次图片获取成功或失败后,才能进行下一次
        if (busy){
            Log.e(TAG,"busy, can't get image.");
            return;
        }else {
            busy = true;
        }

        this.listener = listener;
        Util.buildImgGetDialog(this, new Util.DialogOnClickListener() {
            @Override
            public void onClick(int which) {
                onDialogClick(which);

                if (which == 0){
                    getPicFromAlbum();
                }else if (which == 1){
                    getPicFromCamera();
                }
            }

            @Override
            public void onPosiButtonClick(int which, String text1, String text2) {

            }
        });
    }

    private void getPicFromAlbum(){
        // 来自相册
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, REQUEST_IMAGE_ALBUM);
    }

    private void getPicFromCamera(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public interface GetImageListener{
        void getImage(Uri imageUri, Bitmap bitmap);
    }

    /**
     * 显示dialog
     *
     * @param mContext
     *            上下文
     * @param msg
     *            提示语
     */
    protected XDialog loadingDialog;

    public void showDialog(Context mContext, String msg) {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
        loadingDialog = new XDialog(mContext, R.style.Dialog_image, msg);
        loadingDialog.show();
    }

    /**
     * 让dialog消失
     */
    public void dismissDialog() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

}

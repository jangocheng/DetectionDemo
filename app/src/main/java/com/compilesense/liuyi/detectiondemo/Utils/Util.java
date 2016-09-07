package com.compilesense.liuyi.detectiondemo.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.compilesense.liuyi.detectiondemo.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shenjingyuan002 on 16/9/5.
 */
public class Util {
    public interface DialogOnClickListener{
        void onClick(int which);
    }
    public static void buildDialog(Context context, final DialogOnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String[] a = {"相册"};
        builder.setTitle(context.getString(R.string.dialog_title))
                .setItems(a, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(which);
                    }
                })
                .create().show();
    }



    public static String string2jsonString(String s){

        s = s.replace("\\","");
        int length = s.length();
        s = s.substring(1,length -1);
        return s;
    }

    public static Bitmap getBitmapFromAssets(Context context){
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("jiang.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] bitmap2ByteArray(Bitmap bitmap){
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bStream);
        return bStream.toByteArray();
    }

    public static byte[] uri2ByteArray(Uri uri,Context context)throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static void compressPicture(String srcPath, String desPath) {
        FileOutputStream fos = null;
        BitmapFactory.Options op = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
        op.inJustDecodeBounds = false;

        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        float hh = 1024f;//
        float ww = 1024f;//
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > ww) {
            be = (float) (w / ww);
        } else if (w < h && h > hh) {
            be = (float) (h / hh);
        }
        if (be <= 0) {
            be = 1.0f;
        }
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, op);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        try {
            fos = new FileOutputStream(desPath);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

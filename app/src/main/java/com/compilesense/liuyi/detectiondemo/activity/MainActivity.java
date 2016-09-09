package com.compilesense.liuyi.detectiondemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.Utils.Util;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectAge;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectGender;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectImgProperties;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectKeyPoint;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    // 拍照成功，读取相册成功，裁减成功
    private final int  REQUEST_IMAGE_ALBUM = 1, REQUEST_IMAGE_CAPTURE = 2,CUT_OK = 3;
    private final int ACTION_DETECT_AGE = 11,ACTION_DETECT_GENDER = 12, ACTION_DETECT_IMAGE_POR = 13,
        ACTION_KEY_POINT = 14;
    private int action;

    private File tempFile;
    private final String tempFileName = "tempFile.jpg";
    private Uri uriTempFile;

    private TextView info;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFresco();
//        Bitmap bmp = Util.getBitmapFromAssets(this);
//        if (bmp == null){
//            return;
//        }
//
//        byte[] bitmapByte = Util.bitmap2ByteArray(bmp);
//        if (bmp != null) bmp.recycle();

//        testPost(bitmapByte);


        // 定义拍照后存放图片的文件位置和名称，使用完毕后可以方便删除
        tempFile = new File(getCacheDir(), tempFileName);
        initView();
    }

    private void initFresco(){
        Fresco.initialize(this);
    }

    private void initView(){
        info = (TextView) findViewById(R.id.info);
        image = (ImageView) findViewById(R.id.image_view);

        Button ageDetect = (Button) findViewById(R.id.age_detection);
        ageDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_DETECT_AGE;
                buildDialog();
            }
        });

        Button genderDetect = (Button) findViewById(R.id.gender_detection);
        genderDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_DETECT_GENDER;
                buildDialog();
            }
        });

        Button imagePor = (Button) findViewById(R.id.image_por_detection);
        imagePor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_DETECT_IMAGE_POR;
                buildDialog();
            }
        });

        Button keyPoint = (Button) findViewById(R.id.key_point_detection);
        keyPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_KEY_POINT;
                buildDialog();
            }
        });

        Button recognition = (Button) findViewById(R.id.face_recognition);
        recognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RecognitionActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case REQUEST_IMAGE_ALBUM:
                //从相册中获取到图片了，才执行裁剪动作
                if (data != null) {
                    Uri imageUri = data.getData();
                    image.setImageURI(imageUri);
                    detect(imageUri);
                    info.setText("上传图片中...");

//                    clipPhoto(data.getData(),ALBUM_OK);
                    //setPicToView(data);
                }
                break;
            case REQUEST_IMAGE_CAPTURE:

                Bitmap bitmap;
                try {
                    bitmap = data.getExtras().getParcelable("data");
                    image.setImageBitmap(bitmap);
                    detect(bitmap);
                } catch (ClassCastException e){
                    e.printStackTrace();
                }

                break;

            case CUT_OK:
                if (data != null) {
                    setPicToView(data);
                }else
                {
                    Log.e("Main","data为空");
                }
                break;
        }
    }

    private void buildDialog(){
        Util.buildImgGetDialog(MainActivity.this, new Util.DialogOnClickListener() {
            @Override
            public void onClick(int which) {
                if (which == 0){
                    getPicFromAlbum();
                }else if (which == 1){
                    getPicFromCamera();
                }
            }
        });
    }

    /**
     * 保存裁剪之后的图片数据 将图片设置到imageview中
     *
     * @param picdata　　　　　　　　　　资源
     */
    private void setPicToView(Intent picdata) {

        try
        {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriTempFile));
            ImageView v = (ImageView) findViewById(R.id.image_view);
            v.setImageBitmap(bitmap);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri          图片uri
     * @param type         类别：相机，相册
     */
    public void clipPhoto(Uri uri,int type) {


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);

        /**
         * 此处做一个判断
         * １，相机取到的照片，我们把它做放到了定义的目录下。就是file
         * ２，相册取到的照片，这里注意了，因为相册照片本身有一个位置，我们进行了裁剪后，要给一个裁剪后的位置，
         * 　　不然onActivityResult方法中，data一直是null
         */
        if(type == REQUEST_IMAGE_CAPTURE)
        {
            uriTempFile = Uri.parse("file://" + "/" +  this.getCacheDir().getPath() + "/" + "small.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTempFile);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        }else {
            uriTempFile = Uri.parse("file://" + "/" + this.getCacheDir().getPath() + "/" + "small.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTempFile);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        }
        startActivityForResult(intent, CUT_OK);
    }

    //    private void getPicjFromCarmen(){
//
//        //这里被注掉的，是在6.0中进行权限判断的，大家可以根据情况，自行加上
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    123);
//            Log.e("Album","我没有权限啊");
//        }else {
//
//            Log.e("Album","我有权限啊");
//        }
//
//        // 来自相机
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 下面这句指定调用相机拍照后的照片存储的路径
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//        startActivityForResult(cameraIntent, CAMERA_OK);// CAMERA_OK是用作判断返回
//    }

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

    void detect(Bitmap bitmap){
        switch (action){
            case ACTION_DETECT_AGE:
                DetectAge.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed(){
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_DETECT_GENDER:
                DetectGender.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_DETECT_IMAGE_POR:
                DetectImgProperties.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_KEY_POINT:
                DetectKeyPoint.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;
        }

    }

    void detect(Uri imageUri){
        switch (action){
            case ACTION_DETECT_AGE:
                DetectAge.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed(){
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_DETECT_GENDER:
                DetectGender.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_DETECT_IMAGE_POR:
                DetectImgProperties.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_KEY_POINT:
                DetectKeyPoint.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;
        }


    }

    void detectGender(Uri imageUri){

    }
//    private Bitmap getBitmapFromAssets(){
//        try {
//            AssetManager assetManager = getAssets();
//            InputStream is = assetManager.open("jiang.jpg");
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//            return bitmap;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

}

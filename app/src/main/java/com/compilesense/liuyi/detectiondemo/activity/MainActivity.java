package com.compilesense.liuyi.detectiondemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.compilesense.liuyi.detectiondemo.view.FaceRectangleView;
import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectAge;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectGender;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectImgProperties;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.DetectKeyPoint;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private final int ACTION_DETECT_AGE = 11,ACTION_DETECT_GENDER = 12, ACTION_DETECT_IMAGE_POR = 13,
        ACTION_KEY_POINT = 14;
    private int action;
    private TextView info;
    private FaceRectangleView image;
    private Rect imageSourceRect;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFresco();
        initView();
    }

    private void initFresco(){
        Fresco.initialize(this);
    }

    private void initView(){
        info = (TextView) findViewById(R.id.info);
        image = (FaceRectangleView) findViewById(R.id.image_view);

        final GetImageListener listener = new GetImageListener() {
            @Override
            public void getImage(Uri imageUri, Bitmap bitmap) {
                if (imageUri != null){

                    Bitmap b = null;
                    try {
                        b = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), imageUri);
                        imageSourceRect = new Rect(0, 0, b.getWidth(),  b.getHeight());
                        Log.d("imageSourceRect",imageSourceRect.toString());
                        b.recycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.setImageURI(imageUri);
                    detect(imageUri);
                    info.setText("上传图片中...");

                }else if (bitmap != null){

                    imageSourceRect = new Rect(0, 0, bitmap.getWidth(),  bitmap.getHeight());
                    Log.d("imageSourceRect",imageSourceRect.toString());
                    image.setImageBitmap(bitmap);
                    detect(bitmap);

                }else {
                    Toast.makeText(MainActivity.this,"请选择图片",Toast.LENGTH_SHORT).show();
                }
            }
        };

        Button ageDetect = (Button) findViewById(R.id.age_detection);
        ageDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_DETECT_AGE;
                getImage(listener);
            }
        });

        Button genderDetect = (Button) findViewById(R.id.gender_detection);
        genderDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_DETECT_GENDER;
                getImage(listener);
            }
        });

        Button imagePor = (Button) findViewById(R.id.image_por_detection);
        imagePor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_DETECT_IMAGE_POR;
                getImage(listener);
            }
        });

        Button keyPoint = (Button) findViewById(R.id.key_point_detection);
        keyPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = ACTION_KEY_POINT;
                getImage(listener);
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
    void onDialogClick(int which) {

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
                        response = Util.string2jsonString(response);

                        Gson gson = new Gson();
                        try{
                            ResponseImagePro responseImagePro = gson.fromJson(response,ResponseImagePro.class);
                            Rectangle rectangle = responseImagePro.attribute;
                            int left = Integer.parseInt(rectangle.X);
                            int top = Integer.parseInt(rectangle.Y);
                            int right = left + Integer.parseInt(rectangle.width);
                            int bottom = top + Integer.parseInt(rectangle.height);
                            Rect rect = new Rect(left,top,right,bottom);
                            Log.d("imageSourceRect,face",rect.toString());
                            image.setRect(imageSourceRect, rect);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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
                        response = Util.string2jsonString(response);
                        Gson gson = new Gson();
                        try{
                            ResponseImagePro responseImagePro = gson.fromJson(response,ResponseImagePro.class);
                            Rectangle rectangle = responseImagePro.attribute;
                            int left = Integer.parseInt(rectangle.X);
                            int top = Integer.parseInt(rectangle.Y);
                            int right = left + Integer.parseInt(rectangle.width);
                            int bottom = top + Integer.parseInt(rectangle.height);
                            Rect rect = new Rect(left,top,right,bottom);
                            Log.d("imageSourceRect,face",rect.toString());
                            image.setRect(imageSourceRect, rect);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

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

    class Rectangle{
        public String X;
        public String Y;
        public String width;
        public String height;
    }

    class ResponseImagePro{
        public String status;
        public Rectangle attribute;
    }
}

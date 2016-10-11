package com.compilesense.liuyi.detectiondemo.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.RecognizeTwoFace;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.google.gson.Gson;

public class TwoFaceRecognizeActivity extends BaseActivity {
    private final String TAG = "TwoFaceRecActivity";

    ImageView face1;
    ImageView face2;

    Uri face1ImageUri = null;
    Bitmap face1Bitmap = null;
    Uri face2ImageUri = null;
    Bitmap face2Bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_face_recognize);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    void initView(){
        face1 = (ImageView) findViewById(R.id.img_face1);
        face2 = (ImageView) findViewById(R.id.img_face2);

        Button chooseFace1 = (Button) findViewById(R.id.bt_face1);
        Button chooseFace2 = (Button) findViewById(R.id.bt_face2);
        Button recognize = (Button) findViewById(R.id.bt_recognition_two_face);

        chooseFace1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage(new GetImageListener() {
                    @Override
                    public void getImage(Uri imageUri, Bitmap bitmap) {
                        if (imageUri != null){
                            face1ImageUri = imageUri;
                            face1Bitmap = null;
                            face1.setImageURI(imageUri);
                        }else if (bitmap != null){
                            face1Bitmap = bitmap;
                            face1ImageUri = null;
                            face1.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        });

        chooseFace2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage(new GetImageListener() {
                    @Override
                    public void getImage(Uri imageUri, Bitmap bitmap) {
                        if (imageUri != null){
                            face2ImageUri = imageUri;
                            face2Bitmap = null;
                            face2.setImageURI(imageUri);
                        }else if (bitmap != null){
                            face2Bitmap = bitmap;
                            face2ImageUri = null;
                            face2.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        });

        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ResponseListener listener = new ResponseListener() {
                    @Override
                    public void success(String response) {

                        if (face1Bitmap != null){
                            face1Bitmap.recycle();
                        }
                        if (face2Bitmap != null){
                            face2Bitmap.recycle();
                        }

                        response = Util.string2jsonString(response);
                        Gson gson = new Gson();
                        ResponseRecognize responseRecognize = gson.fromJson(response, ResponseRecognize.class);
                        if (responseRecognize.status.equals("OK")){
                            Toast.makeText(TwoFaceRecognizeActivity.this,"是同一人",Toast.LENGTH_SHORT).show();
                        }else if (responseRecognize.status.equals("NO")){
                            Toast.makeText(TwoFaceRecognizeActivity.this,"不是同一人",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void failed() {

                    }
                };

                if (face1ImageUri != null && face2ImageUri != null){
                    new RecognizeTwoFace().recognize(
                            TwoFaceRecognizeActivity.this,
                            face1ImageUri,
                            face2ImageUri,
                            listener
                    );
                }else if (face1Bitmap != null && face2ImageUri != null){
                    new RecognizeTwoFace().recognize(
                            TwoFaceRecognizeActivity.this,
                            face1Bitmap,
                            face2ImageUri,
                            listener
                    );
                }else if (face1ImageUri != null && face2Bitmap != null){
                    new RecognizeTwoFace().recognize(
                            TwoFaceRecognizeActivity.this,
                            face1ImageUri,
                            face2Bitmap,
                            listener
                    );
                }else if (face1Bitmap != null && face2Bitmap != null){
                    new RecognizeTwoFace().recognize(
                            TwoFaceRecognizeActivity.this,
                            face1Bitmap,
                            face2Bitmap,
                            listener
                    );
                }
                else {
                    Toast.makeText(TwoFaceRecognizeActivity.this,"请选择图片",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    void onDialogClick(int which) {

    }

    class ResponseRecognize{
        public String status;
    }
}

package com.compilesense.liuyi.detectiondemo.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.compilesense.liuyi.detectiondemo.model.bean.FaceBean;
import com.compilesense.liuyi.detectiondemo.model.bean.KeyPointBean;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.Train;
import com.compilesense.liuyi.detectiondemo.utils.CacheUtils;
import com.compilesense.liuyi.detectiondemo.utils.Constans;
import com.compilesense.liuyi.detectiondemo.utils.Util.DialogOnClickListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

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
        initToolbar();
        initFresco();
        initView();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.base_toolbar_menu);//设置右上角的填充菜单
        toolbar.setTitle(getResources().getString(R.string.app_name));//设置主标题
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorwrite));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_item1) {
                    Util.buildEditDialog(MainActivity.this,"更改账号","账号","密码",new DialogOnClickListener(){

                        @Override
                        public void onClick(int which) {}

                        @Override
                        public void onPosiButtonClick(int which, String text1, String text2) {
                            // 将账号和密码设置到SP中
                            //CacheUtils.setString(MainActivity.this,Constans.API_ID,text1);
                            //  CacheUtils.setString(MainActivity.this,Constans.API_SECRET,text2);
                        }
                    });
                } else if (menuItemId == R.id.action_item2) {
                    Util.buildEditDialog(MainActivity.this,"更改IP","IP","端口号",new DialogOnClickListener(){

                        @Override
                        public void onClick(int which) {}

                        @Override
                        public void onPosiButtonClick(int which, String text1, String text2) {
                            // 将账号和密码设置到SP中
                            CacheUtils.setString(MainActivity.this, Constans.API_URL,text1);
                             CacheUtils.setString(MainActivity.this,Constans.API_PORT,text2);
                        }
                    });
                }

                return true;
            }
        });

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
            case ACTION_DETECT_AGE://年龄检测

               DetectAge.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                       System.out.print("======================年龄检测返回值=======================>"+response);
                        //处理数据
                        detectionFace(response,"age");
                    }

                    @Override
                    public void failed(){
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_DETECT_GENDER://性别检测
                DetectGender.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        System.out.print("======================性别检测返回值=======================>"+response);
                        //处理数据
                        detectionFace(response,"gender");

                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_DETECT_IMAGE_POR://人脸检测
                DetectImgProperties.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                        Gson gson = new Gson();
                        try{
                            FaceBean facebean = gson.fromJson(response,FaceBean.class);
                            //处理数据
                            detectionFace(facebean);
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

            case ACTION_KEY_POINT://关键点检测
                DetectKeyPoint.getInstance().detect(MainActivity.this, bitmap, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                        Gson gson = new Gson();
                        try{
                            KeyPointBean keyPointBean = gson.fromJson(response,KeyPointBean.class);
                            //处理数据
                            detectionFace(keyPointBean);
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
        }

    }

    void detect(Uri imageUri){
        switch (action){
            case ACTION_DETECT_AGE://年龄检测
                DetectAge.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        System.out.print("======================年龄检测返回值=======================>"+response);
                        //处理数据
                        detectionFace(response,"age");
                    }

                    @Override
                    public void failed(){
                        info.setText("上传超时");
                    }
                });


                break;

            case ACTION_DETECT_GENDER://性别检测
                DetectGender.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        System.out.print("======================性别检测返回值=======================>"+response);
                        //处理数据
                        detectionFace(response,"gender");
                    }

                    @Override
                    public void failed() {
                        info.setText("上传超时");
                    }
                });
                break;

            case ACTION_DETECT_IMAGE_POR://人脸检测
                DetectImgProperties.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);
                        response = Util.string2jsonString(response);
                        Gson gson = new Gson();
                        try{
                            FaceBean facebean = gson.fromJson(response,FaceBean.class);
                            //处理数据
                            detectionFace(facebean);
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

            case ACTION_KEY_POINT://关键点检测
                DetectKeyPoint.getInstance().detect(MainActivity.this, imageUri, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        info.setText(response);

                        response = Util.string2jsonString(response);
                        Gson gson = new Gson();
                        try{
                            KeyPointBean keyPointBean = gson.fromJson(response,KeyPointBean.class);
                            //处理数据
                            detectionFace(keyPointBean);
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
        }


    }




    //关键点检测
    private void detectionFace(KeyPointBean keypointBean) {
        //info.setText("检测成功");
        if ( !keypointBean.getStatus().equals("OK")){
            info.setText("图片中未检测到人脸");
            return;
        }
        List<KeyPointBean.FacesBean> faces = keypointBean.getFaces();
        if ( faces.size()<0){
            return;
        }
        for (KeyPointBean.FacesBean face : faces) {
            int left = Integer.parseInt(face.getX());
            int top = Integer.parseInt(face.getY());
            int right = left + Integer.parseInt(face.getWidth());
            int bottom = top + Integer.parseInt(face.getHeight());
            Rect rect = new Rect(left,top,right,bottom);
            image.setRect(imageSourceRect, rect);
            image.setPoints(face.getPoints());
        }

    }
    //人脸检测
    private void detectionFace(FaceBean facebean) {
       // info.setText("检测成功");
        FaceBean.AttributeBean rectangle = facebean.getAttribute();
        if(TextUtils.isEmpty(rectangle.getX()) || TextUtils.isEmpty(rectangle.getY())
                || TextUtils.isEmpty(rectangle.getWidth()) || TextUtils.isEmpty(rectangle.getHeight())
                || (!facebean.getStatus().equals("OK"))){
            info.setText("图片中未检测到人脸");
            return;
        }
        int left = Integer.parseInt(rectangle.getX());
        int top = Integer.parseInt(rectangle.getY());
        int right = left + Integer.parseInt(rectangle.getWidth());
        int bottom = top + Integer.parseInt(rectangle.getHeight());
        Rect rect = new Rect(left,top,right,bottom);
        Log.d("imageSourceRect,face",rect.toString());
        image.setRect(imageSourceRect, rect);
    }

    private void detectionFace(String data,String jsonKey) {
        String response = Util.string2jsonString(data);
        //解析json数据并显示
        try {
            JSONObject jo = new JSONObject(response);
            String status= jo.getString("status");
            if (status.equals("OK")){
                String result= jo.getString(jsonKey);
                info.setText(result);
            }else {
                info.setText("检测失败");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            info.setText("检测失败");
        }

    }



}

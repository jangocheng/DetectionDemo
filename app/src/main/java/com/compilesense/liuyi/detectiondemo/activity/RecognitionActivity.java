package com.compilesense.liuyi.detectiondemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.model.Group;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.APIManager;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.Train;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.google.gson.Gson;

import java.util.List;

public class RecognitionActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        initPreferences();
        initView();
    }

    void initPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    void initView(){
        findViewById(R.id.person_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecognitionActivity.this,PersonManageActivity.class));
            }
        });

        Button trainPerson = (Button) findViewById(R.id.training_person);
        trainPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Train.getInstance().trainPerson(RecognitionActivity.this, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        response = Util.string2jsonString(response);
                        ResponseTrain responseTrain;
                        try{
                            Gson gson1 = new Gson();
                            responseTrain = gson1.fromJson(response,ResponseTrain.class);
                            if (!responseTrain.status.equals("OK")){
                                return;
                            }
                            preferences.edit()
                                    .putString("task_id",responseTrain.task_id)
                                    .apply();
                            Toast.makeText(RecognitionActivity.this,
                                    "正在训练请稍等一会",
                                    Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed() {

                    }
                });
            }
        });

        Button trainSate = (Button) findViewById(R.id.train_state);
        trainSate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String task_id = preferences.getString("task_id","");
                task_id = "1526";
                if (task_id.equals("")){
                    return;
                }

                Train.getInstance().trainState(RecognitionActivity.this, task_id, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        response = Util.string2jsonString(response);
                        Gson gson = new Gson();
                        try{
                            ResponseTrainState responseTrainState = gson.fromJson(response, ResponseTrainState.class);
                            String status = responseTrainState.status;
                            if (status.equals(Train.STATUS_SUCCESS)){
                                Toast.makeText(RecognitionActivity.this,
                                        "训练成功",
                                        Toast.LENGTH_SHORT).show();
                            }else if (status.equals(Train.STATUS_FAILED)){
                                Toast.makeText(RecognitionActivity.this,
                                        "训练失败",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(RecognitionActivity.this,
                                        "正在训练请等待",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void failed() {

                    }
                });
            }
        });

        final Button recognitionTwoFace = (Button) findViewById(R.id.recognition_two_face);
        recognitionTwoFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecognitionActivity.this,TwoFaceRecognizeActivity.class));
            }
        });

        Button manageGroup = (Button) findViewById(R.id.group_manage);
        manageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecognitionActivity.this, GroupManageActivity.class));
            }
        });

        Button trainGroup = (Button) findViewById(R.id.training_group);
        trainGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIManager.getInstance().fetchGroup(RecognitionActivity.this, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        response = Util.string2jsonString(response);
                        Gson gson = new Gson();
                        try {
                            ResponseGroupFetch responseGroupFetch = gson.fromJson(response, ResponseGroupFetch.class);
                            final List<Group> groups = responseGroupFetch.group;

                            if ( groups == null || groups.isEmpty()){
                                Toast.makeText(RecognitionActivity.this,"本账号下没有人群",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Util.buildChooseGroupDialog(RecognitionActivity.this, groups,
                                    new Util.DialogOnClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            Train.getInstance().trainGroup(RecognitionActivity.this,
                                                    groups.get(which).group_id,
                                                    new ResponseListener() {
                                                        @Override
                                                        public void success(String response) {
                                                            response = Util.string2jsonString(response);
                                                            ResponseTrain responseTrain;
                                                            try{
                                                                Gson gson1 = new Gson();
                                                                responseTrain = gson1.fromJson(response,ResponseTrain.class);
                                                                if (!responseTrain.status.equals("OK")){
                                                                    return;
                                                                }
                                                                preferences.edit()
                                                                        .putString("task_id",responseTrain.task_id)
                                                                        .apply();
                                                                Toast.makeText(RecognitionActivity.this,
                                                                        "正在训练请稍等一会",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }catch (Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void failed() {

                                                        }
                                                    });
                                        }
                                    });


                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failed() {

                    }
                });
            }
        });
    }

    class ResponseGroupFetch{
        public String status;
        public List<Group> group;
    }

    class ResponseTrain{
        public String status;
        public String task_id;
    }

    class ResponseTrainState{
        public String status;
    }
}

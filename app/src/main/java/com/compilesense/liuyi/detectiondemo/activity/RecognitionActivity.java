package com.compilesense.liuyi.detectiondemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.Train;

public class RecognitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        initView();
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
                        Log.d("trainPerson",response);
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

                Train.getInstance().trainState(RecognitionActivity.this, Train.task_id, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        Log.d("trainState",response);
                    }

                    @Override
                    public void failed() {

                    }
                });
            }
        });

        Button recognitionTwoFace = (Button) findViewById(R.id.recognition_two_face);
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

    }
}

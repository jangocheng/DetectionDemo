package com.compilesense.liuyi.detectiondemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.Train;

public class FaceRecognitionActivity extends AppCompatActivity {

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
                startActivity(new Intent(FaceRecognitionActivity.this,PersonManageActivity.class));
            }
        });

        Button trainPerson = (Button) findViewById(R.id.training_person);
        trainPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("onclick","tp");
                Train.getInstance().trainPerson(FaceRecognitionActivity.this, new ResponseListener() {
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

                Log.d("onclick","ts");
                Train.getInstance().trainState(FaceRecognitionActivity.this, Train.task_id, new ResponseListener() {
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

    }
}

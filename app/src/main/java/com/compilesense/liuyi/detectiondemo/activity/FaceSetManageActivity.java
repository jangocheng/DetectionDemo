package com.compilesense.liuyi.detectiondemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.model.FaceSet;
import com.compilesense.liuyi.detectiondemo.model.Group;
import com.compilesense.liuyi.detectiondemo.model.Person;
import com.compilesense.liuyi.detectiondemo.platform_interaction.RecognitionResponse;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.APIManager;
import com.compilesense.liuyi.detectiondemo.utils.SpaceItemDecoration;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FaceSetManageActivity extends BaseActivity {
    private final String TAG = "FaceSetManageActivity";

    ProgressBar progressBar;
    FaceSetRecViewAdapter adapter;
    TextView name,tag;
    String group_id = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faceset_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        fetchFaceSet();
    }

    void initView(){
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        name = (TextView) findViewById(R.id.add_faceset_name);
        tag = (TextView) findViewById(R.id.add_faceset_tag);
        findViewById(R.id.create_faceset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFaceSet();
            }
        });
        initRecycleView();
    }

    void initRecycleView(){
        adapter = new FaceSetRecViewAdapter();
        adapter.listener = new ItemClickListener() {
            @Override
            public void onDeleteFaceSet(int position) {
                deleteFaceSet(adapter.faceSetList.get(position).faceset_id);
            }

            @Override
            public void onManageFaceSet(int position) {

                FaceSet faceSet = adapter.faceSetList.get(position);
                FaceManageActivity.startFaceManageActivity(FaceSetManageActivity.this,null,null,null,
                        faceSet.faceset_id);

            }

            @Override
            public void onRecognizeGroup(final int position) {

            }
        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_faceset);
        recyclerView.setLayoutManager(new LinearLayoutManager(FaceSetManageActivity.this));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));
        recyclerView.setAdapter(adapter);
    }

    void createFaceSet(){
        String nameString = name.getText().toString();
        String tagString = tag.getText().toString();
        if (nameString == null || nameString.equals("")){
            Toast.makeText(this,"name不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        APIManager.getInstance().createFaceSet(this,
                nameString,
                tagString,
                new ResponseListener() {
                    @Override
                    public void success(String response) {
                        progressBar.setVisibility(View.GONE);
                        fetchFaceSet();

                    }

                    @Override
                    public void failed() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(FaceSetManageActivity.this,
                                "添加失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void fetchFaceSet(){
        APIManager.getInstance().fetchFaceSet(this, new ResponseListener() {
            @Override
            public void success(String response) {
                response = Util.string2jsonString(response);
                Gson gson = new Gson();
                try {
                    ResponseFaceSetFetch responseGroupFetch = gson.fromJson(response, ResponseFaceSetFetch.class);
                    if (responseGroupFetch.faceset == null){
                        responseGroupFetch.faceset = Collections.EMPTY_LIST;
                        Toast.makeText(FaceSetManageActivity.this,
                                "该账号下没有人脸集，请添加",
                                Toast.LENGTH_SHORT).show();
                    }

                    adapter.setFaceSetList( responseGroupFetch.faceset );

                }catch (Exception e){
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void failed() {
                Toast.makeText(FaceSetManageActivity.this,
                        "获取人脸集失败",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void deleteFaceSet(String id){
        APIManager.getInstance().deleteFaceSet(this, id, new ResponseListener() {
            @Override
            public void success(String response) {
               fetchFaceSet();
            }

            @Override
            public void failed() {
                Toast.makeText(FaceSetManageActivity.this,
                        "删除失败",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void handRecognitionResponse(String response){
        response = Util.string2jsonString(response);
        Gson gson = new Gson();
        try{
            RecognitionResponse recognitionResponse = gson.fromJson(response,RecognitionResponse.class);


            if (recognitionResponse.Persons.get(0).Passed){
                Toast.makeText(this,"识别通过",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"识别未通过",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    void onDialogClick(int which) {

    }



    class ResponseFaceSetFetch{
        public String status;
        public List<FaceSet> faceset;
    }

    interface ItemClickListener{
        void onDeleteFaceSet(int position);
        void onManageFaceSet(int position);
        void onRecognizeGroup(int position);
    }

    class FaceSetViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView itemName;
        View itemControl;
        Button deleteFaceSet, manageFaceSet, recognizeGroup;

        public FaceSetViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemControl = itemView.findViewById(R.id.item_control);
            deleteFaceSet = (Button) itemView.findViewById(R.id.item_delete_faceset);
            manageFaceSet = (Button) itemView.findViewById(R.id.item_manage_faceset);
            recognizeGroup = (Button) itemView.findViewById(R.id.item_recognize_group);

            itemControl.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (View.GONE == itemControl.getVisibility()){
                        itemControl.setVisibility(View.VISIBLE);
                    }else if (View.VISIBLE == itemControl.getVisibility()){
                        itemControl.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    class FaceSetRecViewAdapter extends RecyclerView.Adapter<FaceSetViewHolder>{
        public ItemClickListener listener;
        public List<FaceSet> faceSetList = new ArrayList<>();

        public void setFaceSetList(List<FaceSet> faceSetList) {
            this.faceSetList = faceSetList;
            Log.d(TAG,"groupList.size:"+faceSetList.size());
            notifyDataSetChanged();
        }

        @Override
        public FaceSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faceset_list,parent,false);
            return new FaceSetViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final FaceSetViewHolder holder, int position) {
            if (listener == null){
                return;
            }
            FaceSet faceSet = faceSetList.get(position);
            holder.itemName.setText(faceSet.faceset_name);
            holder.deleteFaceSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteFaceSet(holder.getAdapterPosition());
                }
            });
            holder.manageFaceSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onManageFaceSet(holder.getAdapterPosition());
                }
            });
            holder.recognizeGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecognizeGroup(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return faceSetList.size();
        }
    }

}

package com.compilesense.liuyi.detectiondemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.model.Group;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.APIManager;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.Train;
import com.compilesense.liuyi.detectiondemo.utils.SpaceItemDecoration;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.compilesense.liuyi.detectiondemo.model.Face;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FaceManageActivity extends BaseActivity {
    private final static String TAG = "FaceManageActivity";
    FaceRecycleViewAdapter adapter;
    String person_id;
    String group_id;
    String person_name;
    String faceset_id;
    public static void startFaceManageActivity(Context context, String person_id, String person_name, String group_id,String  faceset_id){
        //由FaceSetManageActivity跳转过来
        if (!TextUtils.isEmpty(faceset_id) ){
            Intent intent = new Intent(context, FaceManageActivity.class);
            intent.putExtra("faceset_id",faceset_id);
            context.startActivity(intent);
            return;
        }

        if (person_id == null || person_id.equals("")){
            Log.e(TAG,"缺少person_id");
            return;
        }

        Intent intent = new Intent(context, FaceManageActivity.class);
        intent.putExtra("person_id",person_id);
        intent.putExtra("person_name",person_name);
        if (group_id != null){
            intent.putExtra("group_id",group_id);
        }
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_manage);
        parseIntent();
        initView();
        fetchFace();
    }

    @Override
    void onDialogClick(int which) {

    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (person_name != null){
            toolbar.setTitle(person_name);
        }
        setSupportActionBar(toolbar);
    }

    private void parseIntent(){
        person_id = getIntent().getStringExtra("person_id");
        group_id = getIntent().getStringExtra("group_id");
        faceset_id = getIntent().getStringExtra("faceset_id");
        person_name = getIntent().getStringExtra("person_name");

    }

    private void initView(){
        initToolbar();
        final Button addFace = (Button) findViewById(R.id.add_face);
        addFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addFace();
            }


        });

        initRecycleView();
    }
    //添加人脸到人员
    private void addFace() {
        getImage(new GetImageListener() {
            @Override
            public void getImage(Uri imageUri, Bitmap bitmap) {
                if (imageUri != null){
                    APIManager.getInstance().addFace(FaceManageActivity.this,
                            imageUri,
                            group_id,
                            person_id,
                            faceset_id,
                            new ResponseListener() {
                                @Override
                                public void success(String response) {
                                    fetchFace();
                                }

                                @Override
                                public void failed() {
                                    Toast.makeText(FaceManageActivity.this,
                                            "添加失败",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }else if (bitmap != null){
                    APIManager.getInstance().addFace(FaceManageActivity.this,
                            bitmap,
                            group_id,
                            person_id,
                            faceset_id,
                            new ResponseListener() {
                                @Override
                                public void success(String response) {
                                    fetchFace();
                                }
                                @Override
                                public void failed() {
                                    Toast.makeText(FaceManageActivity.this,
                                            "添加失败",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(FaceManageActivity.this,"请选择图片",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fetchFace(){
        if (!TextUtils.isEmpty(faceset_id)){
            person_id=faceset_id;
        }
        APIManager.getInstance().fetchFace(this, person_id, new ResponseListener() {
            @Override
            public void success(String response) {
                Gson gson = new Gson();
                try{
                    response = Util.string2jsonString(response);
                    Log.d(TAG,"res:"+response);
                    ResponseFaceFetch r = gson.fromJson(response, ResponseFaceFetch.class);
                    if (r.face == null){
                        r.face = Collections.EMPTY_LIST;
                    }

                    adapter.setFaceList(r.face);
                }catch (Exception e){
                    Log.e(TAG,e.toString());
                }
            }

            @Override
            public void failed() {
                Toast.makeText(FaceManageActivity.this,
                        "获取人脸失败",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initRecycleView(){
        adapter = new FaceRecycleViewAdapter();
        adapter.listener = new FaceViewButtonClickListener() {
            @Override
            public void onDeleteFace(int position) {
                APIManager.getInstance().deleteFace(FaceManageActivity.this,
                        group_id,
                        person_id,
                        adapter.faceList.get(position).face_id,
                        faceset_id,
                        new ResponseListener() {
                            @Override
                            public void success(String response) {
                                fetchFace();
                            }

                            @Override
                            public void failed() {
                                Toast.makeText(FaceManageActivity.this,
                                        "删除失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onRecognizeWithPerson(final int position) {

                APIManager.getInstance().fetchPerson(FaceManageActivity.this, group_id, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        response = Util.string2jsonString(response);

                        Gson gson = new Gson();
                        final PersonManageActivity.ResponseFetchPerson r = gson.fromJson(response, PersonManageActivity.ResponseFetchPerson.class);
                        if (r.person == null || r.person.isEmpty()){
                            return;
                        }

                        Util.buildChosePersonDialog(FaceManageActivity.this, r.person, new Util.DialogOnClickListener() {
                            @Override
                            public void onClick(int which) {
                                String person_id = r.person.get(which).person_id;

                                APIManager.getInstance()
                                        .recognizeFacePerson(FaceManageActivity.this,
                                                person_id,
                                                adapter.faceList.get(position).face_id,
                                                new ResponseListener() {
                                                    @Override
                                                    public void success(String response) {

                                                        response = Util.string2jsonString(response);
                                                        Gson gson1 = new Gson();
                                                        try {
                                                            ResponseRecognition responseRecognition = gson1.fromJson(response,ResponseRecognition.class);
                                                            if (responseRecognition.status.equals("OK")){
                                                                Toast.makeText(FaceManageActivity.this,"是同一人",Toast.LENGTH_SHORT).show();
                                                            }else if (responseRecognition.status.equals("NO")){
                                                                Toast.makeText(FaceManageActivity.this,"不是同一人",Toast.LENGTH_SHORT).show();
                                                            }
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

                    @Override
                    public void failed() {

                    }
                });


            }

            @Override
            public void onRecognizeWithGroup(int position) {
                recognizeWithGroup(position);
            }
        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_face_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));
        recyclerView.setAdapter(adapter);
    }

    private void recognizeWithGroup(final int position){
        APIManager.getInstance().fetchGroup(FaceManageActivity.this, new ResponseListener() {
            @Override
            public void success(String response) {
                response = Util.string2jsonString(response);
                Gson gson = new Gson();
                try {
                    ResponseGroupFetch responseGroupFetch = gson.fromJson(response, ResponseGroupFetch.class);
                    final List<Group> groups = responseGroupFetch.group;

                    if ( groups == null || groups.isEmpty()){
                        Toast.makeText(FaceManageActivity.this,"本账号下没有人群",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Util.buildChooseGroupDialog(FaceManageActivity.this, groups,
                            new Util.DialogOnClickListener() {
                                @Override
                                public void onClick(int which) {
                                    APIManager.getInstance().recognizeFaceGroup(FaceManageActivity.this,
                                            groups.get(which).group_id,
                                            adapter.faceList.get(position).face_id,
                                            new ResponseListener() {
                                                @Override
                                                public void success(String response) {
                                                    Log.d(TAG,response);
                                                }

                                                @Override
                                                public void failed() {

                                                }
                                            }
                                    );
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

    class ResponseGroupFetch{
        public String status;
        public List<Group> group;
    }

    class ResponseRecognition{
        public String status;
        public String person_id;
    }

    class ResponseFaceFetch{
        public String status;
        public List<Face> face;
    }

    interface FaceViewButtonClickListener{
        void onDeleteFace(int position);
        void onRecognizeWithPerson(int position);
        void onRecognizeWithGroup(int position);
    }

    class FaceViewHolder extends RecyclerView.ViewHolder{
        TextView faceId;
        SimpleDraweeView imageView;
        Button deleteFace,recognizeWithPerson, recognizeWithGroup;

        public FaceViewHolder(View itemView) {
            super(itemView);
            faceId = (TextView) itemView.findViewById(R.id.item_tx_face_id);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.item_img_face);
            deleteFace = (Button) itemView.findViewById(R.id.item_bt_delete_face);
            recognizeWithPerson = (Button) itemView.findViewById(R.id.item_bt_recognition_face_person);
            recognizeWithGroup = (Button) itemView.findViewById(R.id.item_bt_recognition_face_group);
        }
    }

    class FaceRecycleViewAdapter extends RecyclerView.Adapter<FaceViewHolder>{
        public List<Face> faceList = new ArrayList<>();
        public FaceViewButtonClickListener listener;

        public void setFaceList(List<Face> faceList) {
            this.faceList = faceList;
            notifyDataSetChanged();
        }

        @Override
        public FaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_face_list,parent,false);
            return new FaceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final FaceViewHolder holder, int position) {
            holder.faceId.setText("ID:" + faceList.get(position).face_id);

            String path = getString(R.string.api_url) + faceList.get(position).face_path;
            Uri uri = Uri.parse(path);
            holder.imageView.setImageURI(uri);

            if (listener == null){
                return;
            }

            final int p = holder.getAdapterPosition();
            holder.deleteFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteFace(p);
                }
            });

//            holder.recognizeWithPerson.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onRecognizeWithPerson(p);
//                }
//            });
//
//            holder.recognizeWithGroup.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onRecognizeWithGroup(p);
//                }
//            });

            holder.recognizeWithGroup.setVisibility(View.GONE);
            holder.recognizeWithPerson.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            return faceList.size();
        }
    }

}

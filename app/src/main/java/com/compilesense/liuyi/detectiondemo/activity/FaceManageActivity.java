package com.compilesense.liuyi.detectiondemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.utils.SpaceItemDecoration;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.compilesense.liuyi.detectiondemo.model.Face;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.PersonManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FaceManageActivity extends AppCompatActivity {
    private final static String TAG = "FaceManageActivity";
    private final int  REQUEST_IMAGE_ALBUM = 1, REQUEST_IMAGE_CAPTURE = 2;
    FaceRecycleViewAdapter adapter;
    String personID;
    public static void startFaceManageActivity(Context context, String person_id){
        if (person_id == null || person_id.equals("")){
            Log.e(TAG,"缺少person_id");
            return;
        }

        Intent intent = new Intent(context, FaceManageActivity.class);
        intent.putExtra("person_id",person_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        fetchFace();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case REQUEST_IMAGE_ALBUM:
                if (data != null) {
                    Uri imageUri = data.getData();
                    PersonManager.getInstance().addFace(FaceManageActivity.this,
                            imageUri,
                            personID,
                            new ResponseListener() {
                                @Override
                                public void success(String response) {
                                    fetchFace();
                                    Log.d("11111111111",response);
                                }

                                @Override
                                public void failed() {

                                }
                            });
                }
                break;

            case REQUEST_IMAGE_CAPTURE:

                Bitmap bitmap;
                try {
                    bitmap = data.getExtras().getParcelable("data");
                    PersonManager.getInstance().addFace(FaceManageActivity.this,
                            bitmap,
                            personID,
                            new ResponseListener() {
                                @Override
                                public void success(String response) {
                                    fetchFace();
                                }

                                @Override
                                public void failed() {

                                }
                            });
                } catch (ClassCastException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void initView(){
        Button addFace = (Button) findViewById(R.id.add_face);
        addFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               buildDialog();
            }
        });


        initRecycleView();
    }

    private void fetchFace(){
        personID = getIntent().getStringExtra("person_id");
        PersonManager.getInstance().fetchFace(this, personID, new ResponseListener() {
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

            }
        });
    }

    private void buildDialog(){
        Util.buildImgGetDialog(FaceManageActivity.this, new Util.DialogOnClickListener() {
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

    private void initRecycleView(){
        adapter = new FaceRecycleViewAdapter();
        adapter.listener = new FaceViewButtonClickListener() {
            @Override
            public void onDeleteFace(int position) {
                PersonManager.getInstance().deleteFace(FaceManageActivity.this,
                        personID,
                        adapter.faceList.get(position).face_id,
                        new ResponseListener() {
                            @Override
                            public void success(String response) {
                                fetchFace();
                            }

                            @Override
                            public void failed() {

                            }
                        });
            }

            @Override
            public void onRecognizeWithPerson(final int position) {

                PersonManager.getInstance().fetchPerson(FaceManageActivity.this, new ResponseListener() {
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

                                PersonManager.getInstance()
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

            }
        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_face_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));
        recyclerView.setAdapter(adapter);
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

            holder.recognizeWithPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecognizeWithPerson(p);
                }
            });

            holder.recognizeWithGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecognizeWithGroup(p);
                }
            });

        }

        @Override
        public int getItemCount() {
            return faceList.size();
        }
    }

}

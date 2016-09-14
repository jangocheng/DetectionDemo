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
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.APIManager;
import com.compilesense.liuyi.detectiondemo.platform_interaction.RecognitionResponse;
import com.compilesense.liuyi.detectiondemo.utils.SpaceItemDecoration;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.compilesense.liuyi.detectiondemo.model.Person;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * PersonManageActivity 有两种可能的启动情况,分别是某一人群中的人和非人群中的人
 */
public class PersonManageActivity extends BaseActivity {
    private final String TAG = "PersonManageActivity";
    PersonListAdapter adapter;
    TextView name;
    TextView tag;
    ProgressBar progressBar ;
    String group_id = null;
    String group_name = null;

    public static void startPersonManageActivity(@NonNull Context context, @Nullable String group_id, @Nullable String group_name){
        Intent intent = new Intent(context, PersonManageActivity.class);
        if (group_id != null){
            intent.putExtra("group_id", group_id);
            intent.putExtra("group_name",group_name);
        }
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_manage);

        parseIntent();



        initView();
        fetchPerson();
    }

    private void parseIntent(){
        group_id = getIntent().getStringExtra("group_id");
        group_name = getIntent().getStringExtra("group_name");
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (group_name != null){
            toolbar.setTitle(group_name);
        }
        setSupportActionBar(toolbar);
    }

    private void initView(){
        initToolbar();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        initRecycleView();
        Button addPerson = (Button) findViewById(R.id.add_person);
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString() == null || name.getText().toString().equals("")){
                    Toast.makeText(PersonManageActivity.this,"name不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }


                addPerson();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        name = (TextView) findViewById(R.id.add_person_name);
        tag = (TextView) findViewById(R.id.add_person_tag);
    }

    private void initRecycleView(){
        adapter = new PersonListAdapter();
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onDeletePerson(int position) {
                progressBar.setVisibility(View.VISIBLE);
                APIManager.getInstance().deletePerson(PersonManageActivity.this,
                        group_id,
                        adapter.personList.get(position).person_id,
                        new ResponseListener() {
                            @Override
                            public void success(String response) {
                                fetchPerson();
                            }

                            @Override
                            public void failed() {

                            }
                        });
            }

            @Override
            public void onManageFace(int position) {
                Person p = adapter.personList.get(position);
                FaceManageActivity.startFaceManageActivity(PersonManageActivity.this,
                        p.person_id, p.person_name, group_id);
            }

            @Override
            public void onRecognizePerson(final int position) {
                getImage(new GetImageListener() {
                    @Override
                    public void getImage(Uri imageUri, Bitmap bitmap) {
                        Person p = adapter.personList.get(position);
                        if (imageUri != null){
                            APIManager.getInstance().recognizeImagePerson(PersonManageActivity.this,
                                    imageUri,
                                    p.person_id,
                                    new ResponseListener() {
                                        @Override
                                        public void success(String response) {
                                            handRecognitionResponse(response);
                                        }

                                        @Override
                                        public void failed() {

                                        }
                                    });

                        }else if (bitmap != null){
                            APIManager.getInstance().recognizeImagePerson(PersonManageActivity.this,
                                    bitmap,
                                    p.person_id,
                                    new ResponseListener() {
                                        @Override
                                        public void success(String response) {
                                            handRecognitionResponse(response);
                                        }

                                        @Override
                                        public void failed() {

                                        }
                                    });
                        }else {
                            Toast.makeText(PersonManageActivity.this,
                                    "请选择图片",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_persons);
        recyclerView.setLayoutManager(new LinearLayoutManager(PersonManageActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));
        recyclerView.setAdapter(adapter);
    }

    private void handRecognitionResponse(String response){
        response = Util.string2jsonString(response);

        Log.d(TAG,"11111111111:"+response);
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

    private void addPerson(){
        String nameString = name.getText().toString();
        String tagString = tag.getText().toString();

        APIManager.getInstance().addPerson(this, group_id, nameString,
                tagString, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        fetchPerson();
                    }

                    @Override
                    public void failed() {

                    }
                });
    }

    private void fetchPerson(){

        APIManager.getInstance().fetchPerson(this, group_id, new ResponseListener() {
            @Override
            public void success(String response) {
                progressBar.setVisibility(View.INVISIBLE);
                Gson gson = new Gson();
                try {
                    response = Util.string2jsonString(response);
                    ResponseFetchPerson responseFetchPerson = gson.fromJson(response,ResponseFetchPerson.class);
                    if (responseFetchPerson.person == null){
                        return;
                    }
                    adapter.setPersons(responseFetchPerson.person);
                }catch (Exception e ){
                    Log.e(TAG,e.toString());
                }
            }

            @Override
            public void failed() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    void onDialogClick(int which) {

    }

    public class ResponseFetchPerson{
        public String status;
        public List<Person> person;
    }

    interface OnItemClickListener{
        void onDeletePerson(int position);
        void onManageFace(int position);
        void onRecognizePerson(int position);
    }

    class PersonListAdapter extends RecyclerView.Adapter<PersonListViewHolder>{

        public List<Person> personList = new ArrayList<>();
        private OnItemClickListener itemClickListener;

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setPersons(List<Person> persons){
            personList = persons;
            notifyDataSetChanged();
        }

        public void addPersons(List<Person> persons){
            int count = personList.size();
            personList.addAll(persons);
            notifyItemInserted(count);
        }

        @Override
        public PersonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person_list,parent,false);
            return new PersonListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final PersonListViewHolder holder, int position) {
            if (itemClickListener != null){

                final int p = holder.getAdapterPosition();

                holder.deletePerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onDeletePerson(p);
                    }
                });

                holder.manageFace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onManageFace(p);
                    }
                });

                holder.recognizePerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onRecognizePerson(p);
                    }
                });
            }
            String name = personList.get(position).person_name;
            holder.name.setText(name);

        }

        @Override
        public int getItemCount() {
            return personList.size();
        }
    }

    class PersonListViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView name;
        View control;
        Button deletePerson,manageFace,recognizePerson;

        public PersonListViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.item_name);
            control =itemView.findViewById(R.id.item_control);
            manageFace = (Button) control.findViewById(R.id.item_manege_face);
            deletePerson = (Button) control.findViewById(R.id.item_delete_person);
            recognizePerson = (Button) control.findViewById(R.id.item_recognize_person);
            control.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (View.GONE == control.getVisibility()){
                        control.setVisibility(View.VISIBLE);

                    }else if (View.VISIBLE == control.getVisibility()){
                        control.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

}

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
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.APIManager;
import com.compilesense.liuyi.detectiondemo.platform_interaction.RecognitionResponse;
import com.compilesense.liuyi.detectiondemo.utils.SpaceItemDecoration;
import com.compilesense.liuyi.detectiondemo.utils.Util;
import com.compilesense.liuyi.detectiondemo.model.Person;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PersonManageActivity 有两种可能的启动情况,分别是某一人群中的人和非人群中的人
 */
public class PersonManageActivity extends BaseActivity {
    private final String TAG = "PersonManageActivity";
    PersonListAdapter adapter;
    TextView name;
    TextView tag;
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
        showDialog(this,"");
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
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initView(){
        initToolbar();
        initRecycleView();
        //添加人员
        Button addPerson = (Button) findViewById(R.id.add_person);
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString() == null || name.getText().toString().equals("")){
                    Toast.makeText(PersonManageActivity.this,"name不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                showDialog(PersonManageActivity.this,"");
                addPerson();

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
                showDialog(PersonManageActivity.this,"");
                APIManager.getInstance().deletePerson(PersonManageActivity.this,
                        group_id,
                        adapter.personList.get(position).person_id,
                        new ResponseListener() {
                            @Override
                            public void success(String response) {
                                dismissDialog();
                                fetchPerson();
                            }

                            @Override
                            public void failed() {
                                dismissDialog();
                                Toast.makeText(PersonManageActivity.this,getResources().getString(R.string.network_fail),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onManageFace(int position) {
                Person p = adapter.personList.get(position);
                FaceManageActivity.startFaceManageActivity(PersonManageActivity.this,
                        p.person_id, p.person_name, group_id,null);
            }

            @Override
            public void onRecognizePerson(final int position) {
                getImage(new GetImageListener() {
                    @Override
                    public void getImage(Uri imageUri, Bitmap bitmap) {
                        Person p = adapter.personList.get(position);
                        if (imageUri != null){
                            showDialog(PersonManageActivity.this,getResources().getString(R.string.recognize_face_ing));
                            APIManager.getInstance().recognizeImagePerson(PersonManageActivity.this,
                                    imageUri,
                                    p.person_id,
                                    new ResponseListener() {
                                        @Override
                                        public void success(String response) {
                                            dismissDialog();
                                            handRecognitionResponse(response);
                                        }

                                        @Override
                                        public void failed() {
                                         dismissDialog();
                                            Toast.makeText(PersonManageActivity.this,getResources().getString(R.string.network_fail),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }else if (bitmap != null){
                            showDialog(PersonManageActivity.this,getResources().getString(R.string.recognize_face_ing));
                            APIManager.getInstance().recognizeImagePerson(PersonManageActivity.this,
                                    bitmap,
                                    p.person_id,
                                    new ResponseListener() {
                                        @Override
                                        public void success(String response) {
                                            dismissDialog();
                                            handRecognitionResponse(response);
                                        }

                                        @Override
                                        public void failed() {
                                            dismissDialog();
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

            @Override
            public void onChageInfo(int position) {
                final Person person = adapter.personList.get(position);

                //获取一个Person的信息
                APIManager.getInstance().getPersonInfo(PersonManageActivity.this,person.person_id  ,  new ResponseListener() {
                    @Override
                    public void success(String response) {

                        response = Util.string2jsonString(response);
                        Gson gson = new Gson();
                        try {
                            PersonInfoBean personInfo= gson.fromJson(response, PersonInfoBean.class);
                            if (personInfo.status.equals("OK")) {
                                String title=personInfo.person_name+"     "+personInfo.tag;
                                Util.buildEditDialog(PersonManageActivity.this,title,"名字","标签",new Util.DialogOnClickListener(){

                                    @Override
                                    public void onClick(int which) {}

                                    @Override
                                    public void onPosiButtonClick(int which, String text1, String text2) {
                                        showDialog(PersonManageActivity.this, "");
                                        APIManager.getInstance().upDataPerson(PersonManageActivity.this,person.person_id  , text1, text2, new ResponseListener() {
                                            @Override
                                            public void success(String response) {
                                                dismissDialog();
                                                Log.d(TAG,"修改人员名称返回值====:"+response);
                                                fetchPerson();
                                            }

                                            @Override
                                            public void failed() {
                                                dismissDialog();
                                                Toast.makeText(PersonManageActivity.this,
                                                        getResources().getString(R.string.network_fail),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(PersonManageActivity.this,getResources().getString(R.string.network_fail),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failed() {

                        Toast.makeText(PersonManageActivity.this,
                                getResources().getString(R.string.network_fail),
                                Toast.LENGTH_SHORT).show();
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
        Gson gson = new Gson();
        try{
            RecognitionResponse recognitionResponse = gson.fromJson(response,RecognitionResponse.class);

            if (recognitionResponse.Persons.size()<=0){
                Toast.makeText(this, "检测异常："+recognitionResponse.Exception,Toast.LENGTH_SHORT).show();
                return;
            }

            if (recognitionResponse.Persons.get(0).Passed){
                Toast.makeText(this,"识别通过",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"识别未通过",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,getResources().getString(R.string.network_fail),Toast.LENGTH_SHORT).show();
        }

    }

    private void addPerson(){
        String nameString = name.getText().toString();
        String tagString = tag.getText().toString();

        APIManager.getInstance().addPerson(this, group_id, nameString,
                tagString, new ResponseListener() {
                    @Override
                    public void success(String response) {
                        name.setText("");
                        tag.setText("");
                        dismissDialog();
                        fetchPerson();
                    }

                    @Override
                    public void failed() {
                        dismissDialog();
                        Toast.makeText(PersonManageActivity.this,getResources().getString(R.string.network_fail),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchPerson(){

        APIManager.getInstance().fetchPerson(this, group_id, new ResponseListener() {
            @Override
            public void success(String response) {

                dismissDialog();
                Gson gson = new Gson();
                try {
                    response = Util.string2jsonString(response);
                    Log.e(TAG,"人员数据:"+response);
                    ResponseFetchPerson responseFetchPerson = gson.fromJson(response,ResponseFetchPerson.class);
                    if (responseFetchPerson.status.equals("NO")){
                        responseFetchPerson.person = Collections.EMPTY_LIST;
                        Toast.makeText(PersonManageActivity.this,
                                "该账号下没有人员数据，请添加",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    adapter.setPersons(responseFetchPerson.person);
                }catch (Exception e ){

                    e.printStackTrace();

                }
            }

            @Override
            public void failed() {
                Toast.makeText(PersonManageActivity.this,getResources().getString(R.string.network_fail),
                        Toast.LENGTH_SHORT).show();
               dismissDialog();
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

    public class PersonInfoBean{
        public String status;
        public String person_id;
        public String person_name;
        public String tag;
    }

    interface OnItemClickListener{
        void onDeletePerson(int position);
        void onManageFace(int position);
        void onRecognizePerson(int position);
        void onChageInfo(int position);
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

                holder.changeInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onChageInfo(p);
                    }
                });
            }
            String name = personList.get(position).person_name;
            holder.name.setText(name);

        }

        @Override
        public int getItemCount() {
            if(personList!=null){
                return personList.size();
            }
            return 0;
        }
    }

    class PersonListViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView name;
        View control;
        Button deletePerson,manageFace,recognizePerson,changeInfo;

        public PersonListViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.item_name);
            control =itemView.findViewById(R.id.item_control);
            manageFace = (Button) control.findViewById(R.id.item_manege_face);
            deletePerson = (Button) control.findViewById(R.id.item_delete_person);
            recognizePerson = (Button) control.findViewById(R.id.item_recognize_person);
            changeInfo = (Button) control.findViewById(R.id.item_chage_person_info);
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

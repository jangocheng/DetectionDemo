package com.compilesense.liuyi.detectiondemo;

import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.compilesense.liuyi.detectiondemo.Utils.Util;
import com.compilesense.liuyi.detectiondemo.model.Person;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.platform_interaction.apis.PersonManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PersonManageActivity extends AppCompatActivity {
    private final String TAG = "PersonManageActivity";
    private final int  ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK = 3;
    PersonListAdapter adapter;
    TextView name;
    TextView tag;

    ProgressBar progressBar ;

    int addFacePostion = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        fetchPerson();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ALBUM_OK:
                if (data != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    Uri imageUri = data.getData();
                    PersonManager.getInstance().addFace(PersonManageActivity.this,
                            imageUri,
                            adapter.personList.get(addFacePostion).person_id,
                            new ResponseListener() {
                                @Override
                                public void success(String response) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Log.d("11111111111",response);
                                }

                                @Override
                                public void failed() {

                                }
                            });
                }
                break;
        }
    }

    private void initView(){

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        initRecycleView();
        Button addPerson = (Button) findViewById(R.id.add_person);
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            public void onAddFace(int position) {
                addFacePostion = position;

                Util.buildDialog(PersonManageActivity.this, new Util.DialogOnClickListener() {
                    @Override
                    public void onClick(int which) {
                        getPicFromAlbum();
                    }
                });
            }

            @Override
            public void onDeleteFace(int position) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDeletePerson(int position) {
                progressBar.setVisibility(View.VISIBLE);
                PersonManager.getInstance().deletePerson(PersonManageActivity.this,
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
            public void onUpdataPerson(int Position) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_persons);
        recyclerView.setLayoutManager(new LinearLayoutManager(PersonManageActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));
        recyclerView.setAdapter(adapter);
    }

    private void getPicFromAlbum(){
        // 来自相册
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_OK);
    }

    private void addPerson(){
        String nameString = name.getText().toString();
        String tagString = tag.getText().toString();

        PersonManager.getInstance().addPerson(this, nameString,
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

        PersonManager.getInstance().fetchPerson(this, new ResponseListener() {
            @Override
            public void success(String response) {
                progressBar.setVisibility(View.INVISIBLE);
                Gson gson = new Gson();
                try {
                    response = Util.string2jsonString(response);
                    ResponseFetchPerson responseFetchPerson = gson.fromJson(response,ResponseFetchPerson.class);

                    if (responseFetchPerson.person.isEmpty()){
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

    class ResponseFetchPerson{
        public String status;
        public List<Person> person;
    }

    interface OnItemClickListener{
        void onAddFace(int position);
        void onDeleteFace(int position);
        void onDeletePerson(int position);
        void onUpdataPerson(int Position);
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

                holder.addFace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onAddFace(holder.getAdapterPosition());
                    }
                });

                holder.deleteFace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onDeletePerson(holder.getAdapterPosition());
                    }
                });

                holder.deletePerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onDeletePerson(holder.getAdapterPosition());
                    }
                });

                holder.updataPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onUpdataPerson(holder.getAdapterPosition());
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

        Button deletePerson,updataPerson,addFace,deleteFace;

        public PersonListViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.item_name);
            control =itemView.findViewById(R.id.item_control);
            deletePerson = (Button) control.findViewById(R.id.item_delete_person);
            updataPerson = (Button) control.findViewById(R.id.item_updata_person);
            addFace = (Button) control.findViewById(R.id.item_add_face);
            deleteFace = (Button) control.findViewById(R.id.item_delete_face);

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

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildAdapterPosition(view) != 0)
                outRect.top = space;
        }
    }
}

package com.compilesense.liuyi.detectiondemo.platform_interaction.apis;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.FetchData;
import com.compilesense.liuyi.detectiondemo.platform_interaction.PostRequest;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理API调用
 * Created by shenjingyuan002 on 16/9/7.
 */
public class APIManager {

    private final String ACTION_ADD_PERSON = "/Person/CreatePerson";
    private final String ACTION_FETCH_PERSON = "/Info/GetPerson";
    private final String ACTION_DELETE_PERSON = "/Person/DeletePerson";
    private final String ACTION_UPDATA_PERSON = "/Person/UpdatePerson";
    private final String ACTION_ADD_FACE = "/Person/AddFaceByPerson";
    private final String ACTION_FETCH_FACE = "/Info/GetFace";
    private final String ACTION_DELETE_PERSON_FACE = "/Person/DeleteFaceByPerson";
    private final String ACTION_RECOGNIZE_FACE_PERSON = "/Recognition/RecognitionVerify";
    private final String ACTION_RECOGNIZE_FACE_GROUP = "/Recognition/RecognitionIdentify";

    private final String ACTION_RECOGNIZE_IMAGE_PERSON = "/Recognition/RecognitionVerifyByArray";
    private final String ACTION_RECOGNIZE_IMAGE_GROUP = "/Recognition/RecognitionIdentifyByArray";


    private final String ACTION_CREATE_GROUP = "/Group/CreateGroup";
    private final String ACTION_DELETE_GROUP = "/Group/DeleteGroup";
    private final String ACTION_ADD_PERSON_IN_GROUP = "/Group/CreatePersonByGroup";
    private final String ACTION_DELETE_PERSON_IN_GROUP = "/Group/DeletePersonByGroup";
    private final String ACTION_GROUP_UPDATA = "/Group/UpdateGroup";
    private final String ACTION_FETCH_GROUP = "/Info/GetGroup";
    private final String ACTION_FETCH_PERSON_IN_GROUP = "/Info/GetPersonByGroup";
    private final String ACTION_ADD_FACE_IN_GROUP = "/Group/AddFaceByPersonGroup";
    private final String ACTION_DELETE_FACE_IN_GROUP = "/Group/DeleteFaceByGroup";


    private APIManager(){};
    private static APIManager instance = new APIManager();
    public static APIManager getInstance() {
        return instance;
    }

    public void createGroup(Context context, String group_name, String group_tag, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_CREATE_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_name",group_name);
        map.put("group_tag",group_tag);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void deleteGroup(Context context, String group_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_DELETE_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void fetchGroup(Context context, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_FETCH_GROUP;
        PostRequest.getInstance().post(context, url ,listener);
    }

    public void fetchPerson(Context context, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_FETCH_PERSON;
        FetchData.getInstance().fetch(context,url,listener);
    }

    public void fetchPerson(Context context, String group_id, ResponseListener listener){
        if (group_id == null){
            fetchPerson(context,listener);
            return;
        }
        String url = context.getString(R.string.api_url) + ACTION_FETCH_PERSON_IN_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void addPerson(Context context, String name, String tag, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_ADD_PERSON;
        Map<String,String> map = new HashMap<>();
        map.put("person_name",name);
        map.put("person_tag",tag);
        FetchData.getInstance().fetch(context, url, map, listener);
    }

    public void addPerson(Context context, String group_id, String person_name, String person_tag, ResponseListener listener){
        if (group_id == null){
            addPerson(context, person_name, person_tag, listener);
            return;
        }

        String url = context.getString(R.string.api_url) + ACTION_ADD_PERSON_IN_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        map.put("person_name", person_name);
        map.put("tag", person_tag);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void deletePerson(Context context,String person_id,ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_DELETE_PERSON;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        FetchData.getInstance().fetch(context,url,map,listener);
    }

    public void deletePerson(Context context, String group_id, String person_id, ResponseListener listener){
        if (group_id == null){
            deletePerson(context, person_id, listener);
            return;
        }

        String url = context.getString(R.string.api_url) + ACTION_DELETE_PERSON_IN_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        map.put("person_id",person_id);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void upDataPerson(Context context, String person_id, String name, String tag, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_UPDATA_PERSON;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        map.put("person_name",name);
        map.put("person_tag",tag);
        FetchData.getInstance().fetch(context,url,map,listener);
    }

    public void addFace(Context context, Uri bitmap, String person_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_ADD_FACE;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        PostRequest.getInstance().post(context, url, bitmap, map, listener);
    }

    public void addFace(Context context, Bitmap bitmap, String person_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_ADD_FACE;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        PostRequest.getInstance().post(context, url, bitmap, map, listener);
    }

    public void addFace(Context context, Uri imageUri,  String group_id, String person_id,ResponseListener listener){
        if (group_id == null){
            addFace(context, imageUri, person_id, listener);
            return;
        }

        String url = context.getString(R.string.api_url) + ACTION_ADD_FACE_IN_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        map.put("group_id",group_id);
        PostRequest.getInstance().post(context,
                url,
                imageUri,
                map,
                listener);
    }

    public void addFace(Context context, Bitmap bitmap, String group_id, String person_id, ResponseListener listener){
        if (group_id == null){
            addFace(context, bitmap, person_id, listener);
            return;
        }

        String url = context.getString(R.string.api_url) + ACTION_ADD_FACE_IN_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        map.put("group_id",group_id);
        PostRequest.getInstance().post(context,
                url,
                bitmap,
                map,
                listener);
    }

    public void fetchFace(Context context, String person_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_FETCH_FACE;
        Map<String, String> map = new HashMap<>();
        map.put("id",person_id);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void deleteFace(Context context, String person_id, String face_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_DELETE_PERSON_FACE;
        Map<String, String> map = new HashMap<>();
        map.put("person_id", person_id);
        map.put("face_id", face_id);

        Log.d("DELETE_FACE","person_id:"+ person_id+",face_id:"+face_id);

        FetchData.getInstance().fetch(context, url, map, listener);
    }

    public void deleteFace(Context context, String group_id, String person_id, String face_id, ResponseListener listener ){
        if (group_id == null){
            deleteFace(context, person_id, face_id, listener);
            return;
        }

        String url = context.getString(R.string.api_url) + ACTION_DELETE_FACE_IN_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        map.put("group_id",group_id);
        map.put("face_id",face_id);
        PostRequest.getInstance().post(context,
                url,
                map,
                listener);
    }

    public void recognizeFacePerson(Context context, String person_id, String face_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_RECOGNIZE_FACE_PERSON;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        map.put("face_id",face_id);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void recognizeFaceGroup(Context context, String group_id, String face_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_RECOGNIZE_FACE_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        map.put("face_id",face_id);
        PostRequest.getInstance().post(context, url, map, listener);
    }

    public void recognizeImagePerson(Context context, Uri imageUri, String person_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_RECOGNIZE_IMAGE_PERSON;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        PostRequest.getInstance().post(context, url, imageUri, map, listener);
    }

    public void recognizeImagePerson(Context context, Bitmap bitmap, String person_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_RECOGNIZE_IMAGE_PERSON;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        PostRequest.getInstance().post(context, url, bitmap, map, listener);
    }

    public void recognizeImageGroup(Context context, Uri imageUri, String group_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_RECOGNIZE_IMAGE_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        PostRequest.getInstance().post(context, url, imageUri, map, listener);
    }

    public void recognizeImageGroup(Context context, Bitmap bitmap, String group_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_RECOGNIZE_IMAGE_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        PostRequest.getInstance().post(context, url, bitmap, map, listener);
    }

}

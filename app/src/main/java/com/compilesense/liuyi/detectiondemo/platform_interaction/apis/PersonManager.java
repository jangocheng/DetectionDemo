package com.compilesense.liuyi.detectiondemo.platform_interaction.apis;

import android.content.Context;
import android.net.Uri;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.FetchData;
import com.compilesense.liuyi.detectiondemo.platform_interaction.PostImage;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenjingyuan002 on 16/9/7.
 */
public class PersonManager {

    private final String ACTION_ADD_PERSON = "/Person/CreatePerson";
    private final String ACTION_FETCH_PERSON = "/Info/GetPerson";
    private final String ACTION_DELETE_PERSON = "/Person/DeletePerson";
    private final String ACTION_UPDATA_PERSON = "/Person/UpdatePerson";
    private final String ACTION_ADD_FACE = "/Person/AddFaceByPerson";
    private final String ACTION_DELETE_FACE = "/Person/DeleteFaceByPerson";

    private PersonManager(){};
    private static PersonManager instance = new PersonManager();
    public static PersonManager getInstance() {
        return instance;
    }

    public void fetchPerson(Context context, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_FETCH_PERSON;
        FetchData.getInstance().fetch(context,url,listener);
    }

    public void addPerson(Context context, String name, String tag, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_ADD_PERSON;
        Map<String,String> map = new HashMap<>();
        map.put("person_name",name);
        map.put("person_tag",tag);
        FetchData.getInstance().fetch(context, url, map, listener);
    }

    public void deletePerson(Context context,String person_id,ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_DELETE_PERSON;
        Map<String, String> map = new HashMap<>();
        map.put("person_id",person_id);
        FetchData.getInstance().fetch(context,url,map,listener);
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
        PostImage.getInstance().post(context, url, bitmap, map, listener);
    }

    public void deleteFace(Context context, String person_id, String face_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_DELETE_PERSON;
        Map<String, String> map = new HashMap<>();
        map.put("person_id", person_id);
        map.put("face_id", face_id);
        FetchData.getInstance().fetch(context, url, map, listener);
    }
}

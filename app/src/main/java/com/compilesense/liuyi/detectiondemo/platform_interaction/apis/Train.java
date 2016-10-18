package com.compilesense.liuyi.detectiondemo.platform_interaction.apis;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.text.TextUtils;
import android.util.Log;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.FetchData;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;
import com.compilesense.liuyi.detectiondemo.utils.CacheUtils;
import com.compilesense.liuyi.detectiondemo.utils.Constans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenjingyuan002 on 16/9/7.
 */
public class Train {
    public static final String STATUS_WAITING1 ="Created";
    public static final String STATUS_WAITING2 ="WaitingForActivation";
    public static final String STATUS_WAITING3 ="WaitingToRun";
    public static final String STATUS_WAITING4 ="Running";
    public static final String STATUS_WAITING5 ="WaitingForChildrenToComplete";
    public static final String STATUS_SUCCESS ="RanToCompletion";
    public static final String STATUS_FAILED ="Faulted";


    private final String ACTION_TRAIN_PERSON = "/Train/TrainPerson";
    private final String ACTION_TRAIN_GROUP = "/Train/TrainGroup";
    private final String ACTION_TRAIN_SATE = "/Info/TrainState/";
    private Train(){}
    private static Train instance = new Train();
    public static Train getInstance() {
        return instance;
    }

    public void trainState(Context context, String task_id, ResponseListener listener){

        String baseUrl= CacheUtils.getString(context, Constans.API_URL,"");
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = context.getString(R.string.api_url);
        }
        String url = baseUrl + ACTION_TRAIN_SATE;
        Map<String, String> map = new HashMap<>();

        Log.d("task_id",task_id);
        map.put("task_id",task_id);
        FetchData.getInstance().fetch(context,url,map,listener);
    }

    public void trainPerson(Context context, ResponseListener listener){
        String baseUrl= CacheUtils.getString(context, Constans.API_URL,"");
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = context.getString(R.string.api_url);
        }
        String url = baseUrl + ACTION_TRAIN_PERSON;
        FetchData.getInstance().fetch(context, url, listener);
    }

    public void trainGroup(Context context, String group_id, ResponseListener listener){
        String baseUrl= CacheUtils.getString(context, Constans.API_URL,"");
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = context.getString(R.string.api_url);
        }
        String url = baseUrl + ACTION_TRAIN_GROUP;
        Map<String, String> map = new HashMap<>();
        map.put("group_id",group_id);
        FetchData.getInstance().fetch(context, url, map, listener);
    }
}

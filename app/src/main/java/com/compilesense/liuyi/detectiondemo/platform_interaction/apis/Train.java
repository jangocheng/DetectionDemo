package com.compilesense.liuyi.detectiondemo.platform_interaction.apis;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.util.Log;

import com.compilesense.liuyi.detectiondemo.R;
import com.compilesense.liuyi.detectiondemo.platform_interaction.FetchData;
import com.compilesense.liuyi.detectiondemo.platform_interaction.ResponseListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenjingyuan002 on 16/9/7.
 */
public class Train {
    private final String ACTION_TRAIN_PERSON = "/Train/TrainPerson";
    private final String ACTION_TRAIN_GROUP = "/Train/TrainGroup";
    private final String ACTION_TRAIN_SATE = "/Info/TrainState/";
    private Train(){}
    private static Train instance = new Train();
    public static Train getInstance() {
        return instance;
    }

    public static String task_id = "1872";

    public void trainState(Context context, String task_id, ResponseListener listener){

        String url = context.getString(R.string.api_url) + ACTION_TRAIN_SATE;
        Map<String, String> map = new HashMap<>();

        Log.d("task_id",task_id);
        map.put("task_id",task_id);
        FetchData.getInstance().fetch(context,url,map,listener);
    }

    public void trainPerson(Context context, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_TRAIN_PERSON;
        FetchData.getInstance().fetch(context, url, listener);
    }

    public void trainGroup(Context context, String group_id, ResponseListener listener){
        String url = context.getString(R.string.api_url) + ACTION_TRAIN_GROUP;
        FetchData.getInstance().fetch(context, url, listener);
    }
}

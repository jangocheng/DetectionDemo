package com.compilesense.liuyi.detectiondemo.platform_interaction;

import java.util.List;

/**
 * Created by shenjingyuan002 on 16/9/14.
 */
public class RecognitionResponse {
    public int Count;
    public String Exception;
    public List<Info> Persons;//奇怪的名字

    public class Info{
        public String ConfidenceLevel;
        public String Label;
        public boolean Passed;
        public String Status;
    }
}

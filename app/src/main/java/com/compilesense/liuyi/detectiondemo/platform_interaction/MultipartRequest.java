package com.compilesense.liuyi.detectiondemo.platform_interaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 上传图片要使用 POST MULTIPART REQUEST.
 * 在API22以上的SDK中 HttpEntity 已经被移除,所以需要自己定义一个 MultipartRequest.
 *
 * Created by liuyi(695183065@qq.com) on 16/8/16.
 */
public class MultipartRequest extends Request<NetworkResponse> {
    private final String TAG = "MultipartRequest";

    private final String twoHyphens = "--";//连字符
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();

    private Response.Listener<NetworkResponse> mListener;
    private Response.ErrorListener mErrorListener;
    private Map<String,String> mHeaders;

    /**
     * 构造器,使用预先定义的请求头以及默认的POST方式.
     * @param url 请求地址
     * @param headers 自定义请求头
     * @param listener 响应监听
     * @param errorListener 错误监听
     */
    public MultipartRequest(String url, Map<String,String> headers, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mErrorListener = errorListener;
        mHeaders = headers;
    }

    /**
     * 构造器 使用传入的请求方式
     * @param method POST or GET
     * @param url 请求地址
     * @param listener 响应监听
     * @param errorListener 错误监听
     */
    public MultipartRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener){
        super(method,url,errorListener);
        mListener = listener;
        mErrorListener = errorListener;
    }

    @Override
    public Map<String,String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders: super.getHeaders();
    }

    @Override
    public String getBodyContentType(){
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            //文本数据
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0){
                textParse(dos,params,getParamsEncoding());
            }
            //byte数据
            Map<String, DataPart> data = getByteData();
            if (data != null && data.size() > 0){
                dataParse(dos, data);
            }
            //close multipart form data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            return bos.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 在具体使用时重写
     * @return
     * @throws AuthFailureError
     */
    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return null;
    }
    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        }catch (Exception e){
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }
    @Override
    public void deliverError(VolleyError error){
        mErrorListener.onErrorResponse(error);
    }

    /**
     * 解析 String map 到 dataOutputStream.
     * @param dataOutputStream handle string parsing
     * @param params inputs collection
     * @param encoding encoding inputs, default UTF-8
     * @throws IOException
     */
    private void textParse(DataOutputStream dataOutputStream,Map<String, String> params, String encoding) throws IOException{
        try {
            for (Map.Entry<String, String> entry : params.entrySet()){
                buildTextPart(dataOutputStream,entry.getKey(),entry.getValue());
            }
        }catch (UnsupportedEncodingException e){
            throw new RuntimeException("Encoding not supported:" + encoding,e);
        }
    }

    /**
     * 解析数据到 dataOutputStream
     * @param dataOutputStream handle file attachment
     * @param data loop through data
     * @throws IOException
     */
    private void dataParse(DataOutputStream dataOutputStream,Map<String, DataPart> data) throws IOException{
        for (Map.Entry<String, DataPart> entry: data.entrySet()){
            buildDataPart (dataOutputStream, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 将文本数据写入 header 和 dataOutputStream
     * @param dataOutputStream handle sting parsing
     * @param parameterName name
     * @param parameterValue value
     * @throws IOException
     */
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException{
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    /**
     * 将文件数据写入 header 和 dataOutputStream
     * @param dataOutputStream handle sting parsing
     * @param inputName name
     * @param dataFile data byte as DaraPart from collection
     * @throws IOException
     */
    private void buildDataPart(DataOutputStream dataOutputStream, String inputName, DataPart dataFile) throws IOException{
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\""
                + dataFile.fileName + "\"" + lineEnd);

        if (dataFile.type != null && !dataFile.type.trim().isEmpty()){
            dataOutputStream.writeBytes("Content-type: " + dataFile.type + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.content);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024*1024;
        int bufferSize = Math.min(bytesAvailable,maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer,0,bufferSize);

        while (bytesRead > 0 ){
            dataOutputStream.write(buffer,0,bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable,maxBufferSize);
            bytesRead = fileInputStream.read(buffer,0,bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }
    /**
     * 传输文件时用的数据容器
     */
    public class DataPart{
        public String fileName;
        public byte[] content;
        public String type;

        public DataPart(){}

        /**
         * 构造器
         * @param name 数据的标签
         * @param data 数据
         */
        public DataPart(String name,byte[] data){
            fileName = name;
            content = data;
        }

        /**
         * 构造器
         * @param name 数据标签
         * @param data 数据
         * @param mimeType mime 类型 eg: "image/jpeg"
         */
        public DataPart(String name ,byte[] data, String mimeType){
            fileName = name;
            content = data;
            type = mimeType;
        }
    }
}

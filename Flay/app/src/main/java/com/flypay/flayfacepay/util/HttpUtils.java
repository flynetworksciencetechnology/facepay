package com.flypay.flayfacepay.util;

import android.text.TextUtils;

import com.tencent.mars.xlog.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * http请求工具类
 */
public class HttpUtils {

    //初始化参数
    private static final Map<String, String> BASE_PARAM = new HashMap<java.lang.String, java.lang.String>(){
        {
            put("uuid",CommonUtil.getDeviceUUid());
            put("ip",CommonUtil.getIpAddress());
        }
    };
    public static void asynGET(String url,Map<String, String> params){
        //处理url
        url = setParams(url, params);
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpUtils","请求失败",e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                Log.i("HttpUtils","onResponse: " + response.body().string());
            }
        });
    }

    private static String setParams(String url, Map<String, String> params) {
        if( params != null && !params.isEmpty()){
            //有参数
            params.putAll(BASE_PARAM);
        }else{
            //无参数
            params = BASE_PARAM;
        }
        //拼接参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue())
                    .append(("&"));
        }
        url = url + "?"
                + sb.toString().subSequence(0, sb.toString().length() - 1);
        return url;
    }

    public static String GET(String url, Map<String, String> params){
        url = setParams(url, params);
        Log.i("HttpUtils","url :" + url);
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Log.d("HttpUtils", "run: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }
    /**
     * 发送Post请求到服务器
     * @param strUrlPath:接口地址
     * @param params:请求体内容
     * @param encode:编码格式
     * @return
     */
    public static String httpPOST(String strUrlPath, Map<String, String> params, String encode) {

        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {
            URL url = new URL(strUrlPath);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(10000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);
            //获得服务器的响应码
            int response = httpURLConnection.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                //处理服务器的响应结果
                return dealResponseResult(inptStream);
            }
        } catch (IOException e) {
            return "";
        }
        return "";
    }

    /**
     * 封装请求体信息
     * @param params:请求体内容
     * @param encode:编码格式
     * @return
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        //存储封装好的请求体信息
        StringBuffer stringBuffer = new StringBuffer();
        if( params != null && !params.isEmpty()){
            try {
                for(Map.Entry<String, String> entry : params.entrySet()) {
                    stringBuffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
                }
                //删除最后的一个"&"
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stringBuffer;
    }

    /**
     * 处理服务器的响应结果（将输入流转化成字符串）
     * @param inputStream:服务器的响应输入流
     * @return
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    public class URI{
        public static final String INIT = "http://192.168.1.117:8762/fly/pay/init";
        public static final String GETAUTHINFO = "http://192.168.1.117:8762/fly/getAuthinfo";
        public static final String AUTHINFO = "http://192.168.1.117:8762/fly/pay/wechat/authinfo";
        public static final String  RAWDATA = "http://192.168.1.117:8762/fly/pay/setRawdata";
    }
}

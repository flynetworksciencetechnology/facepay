package com.flypay.flyfacepay.util.http;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CommonOkhttpClient {

    private static final int TIME_OUT = 30;
    private static OkHttpClient okHttpClient;
    static{
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        okhttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        //允许重定向
        okhttpClientBuilder.followRedirects(true);
        okHttpClient = okhttpClientBuilder.build();
    }

    public static Call sendRequest(Request request, Callback callback){
        Call call = okHttpClient.newCall(request);
        //提交任务到队列,callback回调
        call.enqueue(callback);
        return call;
    }
}

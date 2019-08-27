package com.flypay.flayfacepay.util.http;

import com.flypay.flayfacepay.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;

public class CommonRequest {

    private static final Map<String, String> BASE_PARAM = new HashMap<String, String>(){{
        put("uuid", CommonUtil.getDeviceUUid());
        put("ip",CommonUtil.getIpAddress());
    }};
    /**
     * post请求对象
     * @param url
     * @param params
     * @return
     */
    public static Request initPostRequest(String url,RequestParams params){
        return onPostRequest(url,params,null);
    }

    /**
     * 可以带请求头的post请求对象
     * @param url
     * @param params
     * @param header
     * @return
     */
    public static Request onPostRequest(String url, RequestParams params, RequestParams header){
        FormBody.Builder mFormBody = new FormBody.Builder();
        //参数
        if( params == null){
            params = new RequestParams(BASE_PARAM);
        }else{
            params.urls.putAll(BASE_PARAM);
        }
        for (Map.Entry<String, String> entry:params.urls.entrySet()) {
            mFormBody.add(entry.getKey(),entry.getValue());
        }
        Headers.Builder mHeaders = new Headers.Builder();
        //请求头
        if( header != null){
            for (Map.Entry<String, String> entry:header.urls.entrySet()) {
                mHeaders.add(entry.getKey(),entry.getValue());
            }
        }
        //提交
        FormBody formBody = mFormBody.build();
        Headers headers = mHeaders.build();
        return  new Request.Builder().url(url).post(formBody).headers(headers).build();
    }

    /**
     * post请求对象
     * @param url
     * @param params
     * @return
     */
    public static Request initGetRequest(String url,RequestParams params){
        return onGetRequest(url,params,null);
    }

    /**
     * 可以带请求头的post请求对象
     * @param url
     * @param params
     * @param header
     * @return
     */
    public static Request onGetRequest(String url, RequestParams params, RequestParams header){
        //参数
        StringBuilder sb = new StringBuilder(url).append("?");
        if( params == null){
            params = new RequestParams(BASE_PARAM);
        }else{
            params.urls.putAll(BASE_PARAM);
        }
        for (Map.Entry<String, String> entry:params.urls.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        Headers.Builder mHeaders = new Headers.Builder();
        //请求头
        if( header != null){
            for (Map.Entry<String, String> entry:header.urls.entrySet()) {
                mHeaders.add(entry.getKey(),entry.getValue());
            }
        }
        //提交
        Headers headers = mHeaders.build();
        return  new Request.Builder().url(sb.substring(0,sb.length() -1 )).get().headers(headers).build();
    }
}

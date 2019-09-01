package com.flypay.flyfacepay.util.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求参数的封装
 */
public class RequestParams {

    public ConcurrentHashMap<String, String> urls = new ConcurrentHashMap<>();
    public RequestParams(Map<String, String> map){
        if( map != null){
            for (Map.Entry<String, String> entry:map.entrySet()) {
                put(entry.getKey(),entry.getValue());
            }
        }
    }

    public RequestParams() {
        this((Map<String, String>)null);
    }
    public RequestParams(final String key, final String value) {
        this(new HashMap<String, String>(){
            {
                put(key,value);
            }
        });
    }
    private void put(String key, String value){
        if( key != null && value != null){
            urls.put(key,value);
        }
    }
    public boolean hashParmas(){
        if(urls.size() > 0 ){
            return true;
        }else{
            return false;
        }
    }
}

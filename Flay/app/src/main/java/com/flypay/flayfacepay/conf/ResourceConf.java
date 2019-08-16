package com.flypay.flayfacepay.conf;

import android.content.Context;

import com.flypay.flayfacepay.R;

/**
 * @描述 ： 获取配置文件
 * @作者 ： LIF
 * @修改者 ：
 * @时间 ：2019/8/11 11:10
 * @版本 ： V1.0.0
 * @备注 ：
 *
 */
public class ResourceConf {
    private static Context context;
    public ResourceConf(Context context) {
        this.context = context;
    }

    public String getResource(int key){
        return  context.getResources().getString(key);
    }
}

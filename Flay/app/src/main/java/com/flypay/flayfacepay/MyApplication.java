package com.flypay.flayfacepay;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Environment;

import com.flypay.flayfacepay.util.CommonUtil;
import com.flypay.flayfacepay.util.WxFacePayUtil;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;
/**
 * @描述 : 启动类
 * @版本 : V1.0.0
 * @日期 : 2019/8/7 10:59
 * @作者 : LiF
 * @修改人 :
 * @备注 :
 *
 */
@SuppressLint("Registered")
public class MyApplication extends Application {
    private static final String TAG = CommonUtil.getTag();
    @Override
    public void onCreate() {
        super.onCreate();
        initXlog();
        //初始化微信人脸支付
        Boolean flag = CommonUtil.init(this.getApplicationContext());//初始化,获取商户信息存入本地缓存
        //if( !flag) return;
        WxFacePayUtil.initWxFacePay(this);


    }
    private void initXlog(){
        Log.i(TAG, "initXlog");
        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");
        final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String logPath = SDCARD + "/conf/log";
        // this is necessary, or may cash for SIGBUS
        final String cachePath = this.getFilesDir() + "/xlog";
        //init xlog
        if (BuildConfig.DEBUG) {
            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "ImageFaceSign", "");
            Xlog.setConsoleLogOpen(true);
        } else {
            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "ImageFaceSign", "");
            Xlog.setConsoleLogOpen(false);
        }
        Log.setLogImp(new Xlog());

    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "application terminated");
        super.onTerminate();
        Log.appenderClose();
    }

}

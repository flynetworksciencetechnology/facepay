package com.flypay.flayfacepay;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.RemoteException;

import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.exception.MyException;
import com.flypay.flayfacepay.job.ShowDialogJOB;
import com.flypay.flayfacepay.util.CommonUtil;
import com.flypay.flayfacepay.util.WxFacePayUtil;
import com.flypay.flayfacepay.util.http.CommonOkhttpClient;
import com.flypay.flayfacepay.util.http.CommonRequest;
import com.flypay.flayfacepay.util.http.URI;
import com.google.gson.Gson;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
        //初始化设备
        final Context context = this.getApplicationContext();
        CommonOkhttpClient.sendRequest(CommonRequest.initGetRequest(URI.HOST + URI.INIT , null), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //初始化失败
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //初始设备化成功
                //初始化微信支付
                WxPayFace.getInstance().initWxpayface(context, new IWxPayfaceCallback() {
                    @Override
                    public void response(Map info) throws RemoteException {
                        //inti结果
                        if(!isSuccessInfo(info)){
                            //初始化失败
                            ShowDialogJOB show = new ShowDialogJOB("初始化微信刷脸支付失败,请检查网络",context);
                            Thread t = new Thread(show);
                            t.start();
                            return;
                        }else{
                            Log.i(TAG,"初始化微信人脸支付成功");
                            //初始化调用认证
                            WxFacePayUtil.initAuthinfo(0,null);
                        }
                    }
                });
            }
        });
    }
    private void initXlog(){
        Log.i(TAG, "initXlog");
        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");
        final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String logPath = SDCARD + "/conf/log";
        final String cachePath = this.getFilesDir() + "/xlog";
        if (BuildConfig.DEBUG) {
            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "ImageFaceSign", "");
            Xlog.setConsoleLogOpen(true);
        } else {
            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "ImageFaceSign", "");
            Xlog.setConsoleLogOpen(false);
        }
        Log.setLogImp(new Xlog());

    }
    private static boolean isSuccessInfo(Map info) {
        //BaseActivity ba = new BaseActivity();
        if (info == null) {
            //showToast("调用返回为空, 请查看日志");
            //ba.showToast(StaticConf.ERROR_MSG.微信返回异常_调用返回为空.name());
            new MyException(StaticConf.RESPONSE_CODE.FAILD,StaticConf.ERROR_MSG.微信返回异常_调用返回为空,new RuntimeException(StaticConf.ERROR_MSG.微信返回异常_调用返回为空.name()));
            return false;
        }
        String method_name = new Exception().getStackTrace()[1].getMethodName();
        String code = (String)info.get(StaticConf.RETURN_CODE);
        String msg = (String)info.get(StaticConf.RETURN_MSG);
        Log.i(TAG, "response | "+ method_name+" " + code + " | " + msg);
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            //showToast("调用返回非成功信息, 请查看日志");
            //ba.showToast(StaticConf.ERROR_MSG.调用返回非成功信息.name());
            new MyException(StaticConf.RESPONSE_CODE.FAILD,StaticConf.ERROR_MSG.调用返回非成功信息,new RuntimeException(StaticConf.ERROR_MSG.调用返回非成功信息.name() + msg));
            return false;
        }
        Log.i(TAG, "调用返回成功");
        return true;
    }
    @Override
    public void onTerminate() {
        Log.i(TAG, "application terminated");
        super.onTerminate();
        Log.appenderClose();
    }

}

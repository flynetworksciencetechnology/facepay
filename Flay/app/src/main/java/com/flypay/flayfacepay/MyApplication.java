package com.flypay.flayfacepay;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.RemoteException;

import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.exception.MyException;
import com.flypay.flayfacepay.job.ShowDialogJOB;
import com.flypay.flayfacepay.util.AIUIUtils;
import com.flypay.flayfacepay.util.CommonUtil;
import com.flypay.flayfacepay.util.WxFacePayUtil;
import com.flypay.flayfacepay.util.WxPayHelper;
import com.flypay.flayfacepay.util.http.CommonOkhttpClient;
import com.flypay.flayfacepay.util.http.CommonRequest;
import com.flypay.flayfacepay.util.http.RequestParams;
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
import java.io.InputStream;
import java.util.HashMap;
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
    public static String param = null;
    @Override
    public void onCreate() {
        super.onCreate();
        initXlog();
        param = getAIUIParams();

        //初始化设备
        final Context context = this.getApplicationContext();
        WxPayHelper singleton = WxPayHelper.getSingleton();
        singleton.initWxPay(context);
        Map<String, String> params = new HashMap<String, String>(){
            {
                put("ip",CommonUtil.getIpAddress());
            }
        };
        CommonOkhttpClient.sendRequest(CommonRequest.initGetRequest(URI.HOST + URI.INIT , new RequestParams(params)), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //初始化失败
                Log.e(TAG,"初始化设备失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //初始设备化成功
                Log.e(TAG,"初始化设备成功");
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

    /**
     * 读取配置
     */
    public String getAIUIParams() {
        String params = "";

        AssetManager assetManager =  this.getAssets();
        try {
            InputStream ins = assetManager.open( "cfg/aiui.cfg" );
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }

}

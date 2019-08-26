package com.flypay.flayfacepay.bp;

import android.os.RemoteException;

import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.exception.MyException;
import com.flypay.flayfacepay.util.CommonUtil;
import com.flypay.flayfacepay.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tencent.mars.xlog.Log;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FacePayService {
    private static final String TAG = CommonUtil.getTag();
    /**
     * 支付
     * @param amount
     */
    public void facePay(final String amount) {

        // 先去后台获取是否有已经存在的authinfo
        String getAuthinfo = HttpUtils.URI.GETAUTHINFO;
        getAuthinfo = setParams(getAuthinfo, null);
        Log.i("HttpUtils","url :" + getAuthinfo);
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getAuthinfo)
                .build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Log.d("HttpUtils", "run: " + response.body().string());
                    Gson gson = new Gson();
                    JsonObject obj = gson.fromJson(response.body().string(), JsonObject.class);
                    String code = obj.get("code").getAsString();
                    if( "0000".equals(code)){
                        //如果存在则直接获取后去掉用人脸支付
                        String data = obj.get("data").getAsString();
                        //变成json去请求刷脸支付
                    }else{
                        //如果不存在则重新获取data,重新获取认证,然后调用人脸支付
                        //获取数据
                        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
                            @Override
                            public void response(Map map) throws RemoteException {
                                if (map == null) {
                                    new RuntimeException("调用返回为空").printStackTrace();
                                    return;
                                }
                                String msg = (String) map.get("return_msg");

                                if (!isSuccessInfo(map,TAG)) {
                                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                                    return ;
                                }
                                final String rawdata = map.get("rawdata").toString();
                                Log.i(TAG,"获取rawdata成功" + rawdata);
                                /*
                                在这里处理您自己的业务逻辑
                                 */
                                //调用设置
                                Log.i(TAG,"初始化微信人脸支付,并且获取rawdata: " + rawdata);
                                String url = "http://localhost:8762/fly/pay/setRawdata";
                                Map<String, String> params = new HashMap<String, String>(){{
                                    put("rawdata",rawdata);
                                    put("amount",amount);
                                }};
                                //调用认证
                                String authinfo = HttpUtils.URI.AUTHINFO;
                                authinfo = setParams(authinfo, params);
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
                                            Gson gson = new Gson();
                                            JsonObject obj = gson.fromJson(response.body().string(), JsonObject.class);
                                            String code = obj.get("code").getAsString();
                                            if( "0000".equals(code)){
                                                //认证成功
                                                Log.i("FacePayService","认证成功");
                                                //调用刷脸支付
                                            }else{
                                                Log.e("FacePayService","认证失败");
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }
    //初始化参数
    private static final Map<String, String> BASE_PARAM = new HashMap<java.lang.String, java.lang.String>(){
        {
            put("uuid",CommonUtil.getDeviceUUid());
            put("ip",CommonUtil.getIpAddress());
        }
    };
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
    private static boolean isSuccessInfo(Map info,String tag) {
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
        Log.i(tag, "response | "+ method_name+" " + code + " | " + msg);
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            //showToast("调用返回非成功信息, 请查看日志");
            //ba.showToast(StaticConf.ERROR_MSG.调用返回非成功信息.name());
            new MyException(StaticConf.RESPONSE_CODE.FAILD,StaticConf.ERROR_MSG.调用返回非成功信息,new RuntimeException(StaticConf.ERROR_MSG.调用返回非成功信息.name() + msg));
            return false;
        }
        Log.i(tag, "调用返回成功");
        return true;
    }
}

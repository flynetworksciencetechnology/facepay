package com.flypay.flyfacepay.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.flypay.flyfacepay.R;
import com.flypay.flyfacepay.callpack.WxFacePayCallBack;
import com.flypay.flyfacepay.conf.StaticConf;
import com.flypay.flyfacepay.inputlistmonitor.PeripheralMonitor;
import com.flypay.flyfacepay.model.Result;
import com.flypay.flyfacepay.util.AIUIUtils;
import com.flypay.flyfacepay.util.CommonUtil;
import com.flypay.flyfacepay.util.WxPayHelper;
import com.flypay.flyfacepay.util.http.CommonOkhttpClient;
import com.flypay.flyfacepay.util.http.CommonRequest;
import com.flypay.flyfacepay.util.http.RequestParams;
import com.flypay.flyfacepay.util.http.URI;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WaitPayActivity extends BaseActivity {

    PeripheralMonitor peripheralMonitor;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (peripheralMonitor.dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_pay);
        final TextView tv = (TextView)findViewById(R.id.payment);
        peripheralMonitor = new PeripheralMonitor(new PeripheralMonitor.OnScanListener() {
            @Override
            public void onResult(String code) {
                //去后台获取调用认证,
                //语音播报金额
                //开启等待
                final String amount = String.valueOf(tv.getText());
                final Context context = getApplicationContext();
                final AIUIUtils aiuiUtils = AIUIUtils.getAIUIUtils();
                aiuiUtils.setContext(context);
                aiuiUtils.sendMessage("需要支付" + amount + "元");
                //去支付
                final WxPayHelper singleton = WxPayHelper.getSingleton();

                //生成订单
                Map<String, String> params = new HashMap<String, String>(){
                    {
                        put("amount",amount);
                    }
                };
                CommonOkhttpClient.sendRequest(CommonRequest.initPostRequest(URI.HOST + URI.CREATORDER, new RequestParams(params)), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //结束等待
                        com.tencent.mars.xlog.Log.e(TAG,"创建订单失败");
                        aiuiUtils.sendMessage("支付失败");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //结束等待
                        //查看获取结果
                        String res = response.body().string();
                        com.tencent.mars.xlog.Log.i(TAG,"获取认证成功 : " + res);
                        Gson gson = new Gson();
                        Result result = gson.fromJson(res, Result.class);
                        if( result != null && "0000".equals(result.code)){
                            //创建订单成功
                            String orderno = (String)result.data;
                            singleton.setPayCallBack(new WxFacePayCallBack(orderno,context));
                            singleton.initRawData(orderno);
                        }else{
                            //创建订单失败
                            aiuiUtils.sendMessage("支付失败");
                        }
                    }
                });

//                Map<String, String> params = new HashMap<String, String>(){
//                    {
//                        put("amount",amount);
//                    }
//                };
//                CommonOkhttpClient.sendRequest(CommonRequest.initGetRequest(URI.HOST + URI.GETAUTHINFO, new RequestParams(params)), new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        com.tencent.mars.xlog.Log.e(TAG,"获取失败");
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        //查看获取结果
//                        String res = response.body().string();
//                        com.tencent.mars.xlog.Log.i(TAG,"获取认证成功 : " + res);
//                        Gson gson = new Gson();
//                        Result result = gson.fromJson(res, Result.class);
//                        if( result != null && "0000".equals(result.code)){
//                            //请求成功
//                            //获取成功
//                            //调用刷脸
//                            StoreMerchanEquipmentInfoVO sm = gson.fromJson(gson.toJson(result.data),StoreMerchanEquipmentInfoVO.class);
//
//                            String appid = sm.appid;
//                            String mchId = sm.mchid;
//                            String subAppid = sm.subAppid;
//                            String subMchid = sm.subMchid;
//                            String storeId = sm.storeId;
//                            String authinfo = sm.authinfo;
//                            OrderInfoPO od = sm.oi;
//                            String orderno = od.orderno;
//                            String fee = od.fee;
//                            WxFacePayUtil.doGetFaceCode(appid,mchId,subAppid,subMchid,storeId,authinfo,orderno,fee);
//                        }else{
//                            //请求失败
//                            //获取失败
//                            //重新获取
//
//                            WxFacePayUtil.initAuthinfo(1,amount,getApplicationContext());
//                        }
//                    }
//                });
            }
        },tv, StaticConf.BackType.DEL,this.getApplicationContext());
    }
    private static final String TAG = CommonUtil.getTag();

}
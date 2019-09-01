package com.flypay.flyfacepay.callpack;

import android.content.Context;

import com.flypay.flyfacepay.model.Result;
import com.flypay.flyfacepay.util.AIUIUtils;
import com.flypay.flyfacepay.util.CommonUtil;
import com.flypay.flyfacepay.util.WxPayHelper;
import com.flypay.flyfacepay.util.http.CommonOkhttpClient;
import com.flypay.flyfacepay.util.http.CommonRequest;
import com.flypay.flyfacepay.util.http.RequestParams;
import com.flypay.flyfacepay.util.http.URI;
import com.google.gson.Gson;
import com.tencent.mars.xlog.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WxFacePayCallBack implements WxPayHelper.PayCallBack {
    private String orderno;
    private Context context;
    //private WxPayHelper singleton;
    public WxFacePayCallBack(String orderno,Context context) {//,WxPayHelper singleton
        this.orderno = orderno;
        this.context = context;
        //this.singleton = singleton;
    }

    @Override
    public void paySuccess(Map map) {
        //开启等待
        Map<String, String> param = new HashMap<>();
        param.put("openid",String.valueOf(map.get("openid")));
        param.put("faceCode",String.valueOf(map.get("face_code")));
        param.put("orderno",orderno);
        RequestParams requestParams = new RequestParams(param);
        CommonOkhttpClient.sendRequest(CommonRequest.initPostRequest(URI.HOST + URI.PAY, requestParams), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"支付异常");
                AIUIUtils aiuiUtils = AIUIUtils.getAIUIUtils();
                aiuiUtils.setContext(context);
                aiuiUtils.sendMessage("支付失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //结束等待
                String string = response.body().string();
                Log.e(TAG,"支付结果 :" + string);
                Gson gson = new Gson();
                Result result = gson.fromJson(string, Result.class);
                String payResult = WxPayHelper.RETURN_ERROR;
                AIUIUtils aiuiUtils = AIUIUtils.getAIUIUtils();
                aiuiUtils.setContext(context);
                if( result != null && "0000".equals(result.code)){
                    //支付成功

                    aiuiUtils.sendMessage("支付成功");
                    payResult = WxPayHelper.RETURN_SUCCESS;
                }else{
                    aiuiUtils.sendMessage("支付失败" + result.message);
                }
                WxPayHelper singleton = WxPayHelper.getSingleton();
                singleton.updateResult(payResult);
            }
        });
    }

    @Override
    public void payCancel(String failMsg) {
        AIUIUtils aiuiUtils = AIUIUtils.getAIUIUtils();
        aiuiUtils.setContext(context);
        aiuiUtils.sendMessage("支付失败");
    }

    @Override
    public void interfaceFail() {
        AIUIUtils aiuiUtils = AIUIUtils.getAIUIUtils();
        aiuiUtils.setContext(context);
        aiuiUtils.sendMessage("支付失败");
    }
    private static final String TAG = CommonUtil.getTag();
}

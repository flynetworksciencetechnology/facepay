package com.flypay.flayfacepay.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.flypay.flayfacepay.R;
import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.inputlistmonitor.PeripheralMonitor;
import com.flypay.flayfacepay.model.OrderInfoPO;
import com.flypay.flayfacepay.model.Result;
import com.flypay.flayfacepay.model.StoreMerchanEquipmentInfoVO;
import com.flypay.flayfacepay.util.CommonUtil;
import com.flypay.flayfacepay.util.WxFacePayUtil;
import com.flypay.flayfacepay.util.http.CommonOkhttpClient;
import com.flypay.flayfacepay.util.http.CommonRequest;
import com.flypay.flayfacepay.util.http.RequestParams;
import com.flypay.flayfacepay.util.http.URI;
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
                final String amount = String.valueOf(tv.getText());
                Map<String, String> params = new HashMap<String, String>(){
                    {
                        put("amount",amount);
                    }
                };
                CommonOkhttpClient.sendRequest(CommonRequest.initGetRequest(URI.HOST + URI.GETAUTHINFO, new RequestParams(params)), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        com.tencent.mars.xlog.Log.e(TAG,"获取失败");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //查看获取结果
                        String res = response.body().string();
                        com.tencent.mars.xlog.Log.i(TAG,"获取认证成功 : " + res);
                        Gson gson = new Gson();
                        Result result = gson.fromJson(res, Result.class);
                        if( result != null && "0000".equals(result.code)){
                            //请求成功
                            //获取成功
                            //调用刷脸
                            StoreMerchanEquipmentInfoVO sm = gson.fromJson(gson.toJson(result.data),StoreMerchanEquipmentInfoVO.class);

                            String appid = sm.appid;
                            String mchId = sm.mchid;
                            String subAppid = sm.subAppid;
                            String subMchid = sm.subMchid;
                            String storeId = sm.storeId;
                            String authinfo = sm.authinfo;
                            OrderInfoPO od = sm.oi;
                            String orderno = od.orderno;
                            String fee = String.valueOf(od.totalAmount);
                            WxFacePayUtil.doGetFaceCode(appid,mchId,subAppid,subMchid,storeId,authinfo,orderno,fee);
                        }else{
                            //请求失败
                            //获取失败
                            //重新获取

                            WxFacePayUtil.initAuthinfo(1,amount);
                        }
                    }
                });
            }
        },tv, StaticConf.BackType.DEL,this.getApplicationContext());
    }
    private static final String TAG = CommonUtil.getTag();

}

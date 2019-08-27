package com.flypay.flayfacepay.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.flypay.flayfacepay.R;
import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.inputlistmonitor.PeripheralMonitor;
import com.flypay.flayfacepay.util.CommonUtil;
import com.flypay.flayfacepay.util.http.CommonOkhttpClient;
import com.flypay.flayfacepay.util.http.CommonRequest;
import com.flypay.flayfacepay.util.http.URI;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    PeripheralMonitor peripheralMonitor;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "event= " + event);

        if (peripheralMonitor.dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv = (TextView)findViewById(R.id.payment);
        peripheralMonitor = new PeripheralMonitor(new PeripheralMonitor.OnScanListener() {
            @Override
            public void onResult(String code) {
                //开启人脸支付
                //Intent intent = new Intent(MainActivity.this,FacePayActivity.class);
                //startActivity(intent);
                //FacePayService
                //FacePayService pay = new FacePayService();
                //String text = tv.getText() + "";
                //pay.facePay(text);
                //去后台获取调用认证,
                CommonOkhttpClient.sendRequest(CommonRequest.initGetRequest(URI.HOST + URI.GETAUTHINFO, null), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //查看获取结果
                        //获取成功
                            //调用刷脸
                        //获取失败
                        //重新获取
                            //调用刷脸  WxFacePayUtil.initAuthinfo(1);
                    }
                });
            }
        },tv, StaticConf.BackType.DEL,this.getApplicationContext());
    }
    private static final String TAG = CommonUtil.getTag();
}

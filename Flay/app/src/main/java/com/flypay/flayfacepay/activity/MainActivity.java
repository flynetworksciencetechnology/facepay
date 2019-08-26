package com.flypay.flayfacepay.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.flypay.flayfacepay.R;
import com.flypay.flayfacepay.bp.FacePayService;
import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.inputlistmonitor.PeripheralMonitor;
import com.flypay.flayfacepay.util.CommonUtil;

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
                //showToast(code);
                //开启人脸支付
                //Intent intent = new Intent(MainActivity.this,FacePayActivity.class);
                //startActivity(intent);
                //FacePayService
                FacePayService pay = new FacePayService();
                String text = tv.getText() + "";
                pay.facePay(text);
                //MainActivity.this.finish();
            }
        },tv, StaticConf.BackType.DEL,this.getApplicationContext());
    }
    private static final String TAG = CommonUtil.getTag();
}

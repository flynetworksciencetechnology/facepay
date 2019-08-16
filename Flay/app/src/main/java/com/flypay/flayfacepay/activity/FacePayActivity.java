package com.flypay.flayfacepay.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.flypay.flayfacepay.R;
import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.inputlistmonitor.PeripheralMonitor;
import com.flypay.flayfacepay.util.CommonUtil;
import com.flypay.flayfacepay.util.SPUtils;
import com.flypay.flayfacepay.util.WxFacePayUtil;
import com.tencent.mars.xlog.Log;

/**
 * @描述 ： 用于发起刷脸支付的页面
 * @作者 ： LIF
 * @修改者 ：
 * @时间 ：2019/8/9 16:33
 * @版本 ： V1.0.0
 * @备注 ：
 *
 */
public class FacePayActivity extends BaseActivity {
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
        setContentView(R.layout.face_pay);
        peripheralMonitor = new PeripheralMonitor(new PeripheralMonitor.OnScanListener() {
            @Override
            public void onResult(String code) {
                //showToast(code);
                //返回上个页面
                if( StaticConf.BackType.BACK.equals(code)){
                    //返回上个页面
                    FacePayActivity.this.finish();
                }
            }
        },null, StaticConf.BackType.BACK,this.getApplicationContext());
        //初始化参数
        //向后台请求获取认证
        Log.i(TAG,"向后台请求获取认证");
        //获取调用凭证
        String authinfo = WxFacePayUtil.getWxpayfaceAuthinfo();
        if( authinfo == null || "".equals(authinfo)){
            //获取认证失败
            //支付失败
            showToast("支付失败!!!");
            FacePayActivity.this.finish();
            return;
        }
        //获取uuid(设备当前运行唯一串号)
        String uuid = CommonUtil.getUUID(this.getApplicationContext());
        //启动人脸支付
        //生成订单号
        String orderno = CommonUtil.getId(uuid,this.getApplicationContext(), SPUtils.PREF_KEY_ORDER_BUILD);
        Log.i(TAG,"启动人脸支付");
        WxFacePayUtil.doGetFaceCode(authinfo,orderno);
    }
    private static final String TAG = CommonUtil.getTag();
}

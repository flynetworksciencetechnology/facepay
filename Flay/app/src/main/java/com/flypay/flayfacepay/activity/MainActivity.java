package com.flypay.flayfacepay.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //跳转到待支付页面
        //可以人工阻塞等待初始化调用认证的返回,然后再去开启新的页面
        Intent intent = new Intent(MainActivity.this,WaitPayActivity.class);
        startActivity(intent);
//        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//        String ttsStr = "我是要合成的文本";   //得到待合成文本
//        byte[] ttsData = ttsStr.getBytes();  //转为二进制数据
//        StringBuffer params = new StringBuffer();  //构建合成参数
//        params.append("vcn=xiaoyan");  //合成发音人
//        params.append(",speed=50");  //合成速度
//        params.append(",pitch=50");  //合成音调
//        params.append(",volume=50");  //合成音量
//        checkAIUIAgent();
//        AIUIMessage startTts = new AIUIMessage(AIUIConstant.CMD_TTS,AIUIConstant.START, 0, params.toString(), ttsData);
//        mAIUIAgent.sendMessage(startTts);
    }
    private static final String TAG = CommonUtil.getTag();
}

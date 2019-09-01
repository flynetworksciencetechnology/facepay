package com.flypay.flyfacepay.activity;


import android.content.Intent;
import android.os.Bundle;

import com.flypay.flyfacepay.R;
import com.flypay.flyfacepay.util.CommonUtil;

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

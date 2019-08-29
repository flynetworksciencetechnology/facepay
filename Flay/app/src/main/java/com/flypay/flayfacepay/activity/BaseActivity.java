package com.flypay.flayfacepay.activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flypay.flayfacepay.R;
import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.job.ShowDialogJOB;
import com.flypay.flayfacepay.util.CommonUtil;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.tencent.mars.xlog.Log;

import java.io.IOException;
import java.io.InputStream;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = CommonUtil.getTag();
    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                //屏幕居中显示，X轴和Y轴偏移量都是0
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

}

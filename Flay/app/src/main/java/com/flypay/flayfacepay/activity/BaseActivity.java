package com.flypay.flayfacepay.activity;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flypay.flayfacepay.util.CommonUtil;

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

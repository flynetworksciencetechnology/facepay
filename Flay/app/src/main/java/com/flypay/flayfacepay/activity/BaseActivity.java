package com.flypay.flayfacepay.activity;
import androidx.appcompat.app.AppCompatActivity;

import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.job.ShowDialogJOB;
import com.flypay.flayfacepay.util.CommonUtil;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = CommonUtil.getTag();
    public void showToast(final String text) {
        runOnUiThread(new ShowDialogJOB(text,this.getApplicationContext()));
    }
}

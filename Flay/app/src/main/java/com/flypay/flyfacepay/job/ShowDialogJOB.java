package com.flypay.flyfacepay.job;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @描述 : 显示提示框线程
 * @版本 : V1.0.0
 * @日期 : 2019/8/7 14:47
 * @作者 : LiF
 * @修改人 :
 * @备注 :
 *
 */
public class ShowDialogJOB{
    String text;
    Context context;

    public ShowDialogJOB(String text, Context context) {
        this.text = text;
        this.context = context;

    }
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();//增加部分
                showText();
                Looper.loop();//增加部分
            }
        });
    }

    private void showText() {

        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        //屏幕居中显示，X轴和Y轴偏移量都是0
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}

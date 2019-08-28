package com.flypay.flayfacepay.inputlistmonitor;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.TextView;

import com.flypay.flayfacepay.util.CommonUtil;
import com.tencent.mars.xlog.Log;
import com.flypay.flayfacepay.R;
import com.flypay.flayfacepay.conf.ResourceConf;
import com.flypay.flayfacepay.conf.StaticConf;

/**
 *
 *
 *
 */
/**
 * @描述 :拦截扫码枪和外接设备 的输入事件<p>扫码枪和 外接键盘的处理是一样的
 * @版本 : V1.0.0
 * @日期 : 2019/8/7 16:50
 * @作者 : LiF
 * @修改人 :
 * @备注 :
 *
 */
public class PeripheralMonitor {

    private String codeStr = "";
    private OnScanListener listener;
    private TextView tv;
    private StaticConf.BackType type;
    private Context context;

    public boolean isInterrupt = true;

    public PeripheralMonitor(OnScanListener listener,TextView tv,StaticConf.BackType type, Context context) {
        this.listener = listener;
        this.tv = tv;
        this.type = type;
        this.context = context;
    }


    /**
     * @描述 : 处理输入事件 true 表示消费掉，拦截不在传递， false 不管
     * @版本 : V1.0.0
     * @日期 : 2019/8/7 16:51
     * @作者 : LiF
     * @修改人 :
     * @备注 :
     *
     */
    public boolean dispatchKeyEvent(KeyEvent event) {

        //系统的软键盘  按下去是 -1, 不管，不拦截
        if (event.getDeviceId() == -1) {
            return false;
        }

        //按下弹起，识别到弹起的话算一次 有效输入
        //只要是 扫码枪的事件  都要把他消费掉 不然会被editText 显示出来
        if (event.getAction() == KeyEvent.ACTION_UP) {

            //只要数字和. 有待修改
            int code = event.getKeyCode();
            Log.i(TAG,"-------------------" + code);
            //只捕获已定义按键
            if (isaKeyCode(code)) {
                //数字
                String payment = new ResourceConf(context).getResource(R.string.payment);
                if( isNum(code)){
                    codeStr += (code - KeyEvent.KEYCODE_NUMPAD_0);
                }else if( isDot(code)){
                    //点
                    codeStr += ".";
                }else if( isEnter(code)){
                    //回车
                    if (listener != null) {
                        listener.onResult(codeStr);
                        codeStr = payment;

                    }
                }else if(isBack(code)){
                    //退格键
                    if(StaticConf.BackType.DEL.equals(this.type)){

                        codeStr = payment;
                    }else{
                        //返回上个页面
                        Log.i(TAG,"返回上个页面");
                        listener.onResult(StaticConf.BackType.BACK.name());
                    }
                }
                if( tv != null)
                    tv.setText(codeStr);
                if( isEnter(code) || isBack(code)){
                    codeStr = "";
                }
            }else{
                Log.i(TAG,"-------------------" + code);
            }
        }
        //都是扫码枪来的事件，选择消费掉

        return isInterrupt;
    }

    private boolean isaKeyCode(int code) {
        //数字
        boolean num = code >= KeyEvent.KEYCODE_NUMPAD_0 && code <= KeyEvent.KEYCODE_NUMPAD_9;
        //点dot
        boolean dot = code == KeyEvent.KEYCODE_NUMPAD_DOT;
        //回车
        boolean enter = code == KeyEvent.KEYCODE_ENTER;
        //返回
        boolean back = code == KeyEvent.KEYCODE_DEL;

        return num || dot || enter || back;
    }

    private boolean isNum(int code){
        return code >= KeyEvent.KEYCODE_NUMPAD_0 && code <= KeyEvent.KEYCODE_NUMPAD_9;
    }
    private boolean isDot(int code){
        return code == KeyEvent.KEYCODE_NUMPAD_DOT;
    }
    private boolean isEnter(int code){
        return code == KeyEvent.KEYCODE_ENTER;
    }
    private boolean isBack(int code){
        return code == KeyEvent.KEYCODE_DEL;
    }
    public interface OnScanListener {

        void onResult(String code);
    }

    public void setInterrupt(boolean interrupt) {
        isInterrupt = interrupt;
    }
    private static final String TAG = CommonUtil.getTag();
}

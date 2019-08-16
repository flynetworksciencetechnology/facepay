package com.flypay.flayfacepay.exception;

import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.util.CommonUtil;
import com.tencent.mars.xlog.Log;

/**
 * @描述 : 自定义异常
 * @版本 : V1.0.0
 * @日期 : 2019/8/7 11:26
 * @作者 : LiF
 * @修改人 :
 * @备注 :
 *
 */
public class MyException extends Exception {
    public String code;
    public String msg;
    public Exception e;
    /**
     * @描述 : 空参构造,默认系统未知异常
     * @版本 : V1.0.0
     * @日期 : 2019/8/7 11:28
     * @作者 : LiF
     * @修改人 :
     * @备注 :
     *
     */
    public MyException(){
        this.code = StaticConf.RESPONSE_CODE.FAILD.getCode();
        this.msg = StaticConf.RESPONSE_CODE.FAILD.getMsg();
        this.e = new Exception();
        this.log(CommonUtil.getTag());
    }
    public MyException(StaticConf.RESPONSE_CODE CODE, StaticConf.ERROR_MSG MSG, Exception e){
        this.code = CODE.getCode();
        this.msg = MSG.name();
        this.e = e;
        this.log(CommonUtil.getTag());
    }
    public void log(String tag){
        Log.e(tag,this.msg,this.e);
    }

}

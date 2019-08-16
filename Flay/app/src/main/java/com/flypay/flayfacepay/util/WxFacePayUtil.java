package com.flypay.flayfacepay.util;

import android.app.Application;
import android.os.RemoteException;
import com.flypay.flayfacepay.activity.BaseActivity;
import com.flypay.flayfacepay.conf.StaticConf;
import com.flypay.flayfacepay.exception.MyException;
import com.tencent.mars.xlog.Log;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @描述 : 与微信人脸支付互通的工具类
 * @版本 : V1.0.0
 * @日期 : 2019/8/7 10:37
 * @作者 : LiF
 * @修改人 :
 * @备注 :
 *
 */
public class WxFacePayUtil {
    private static final String TAG = CommonUtil.getTag();
    private static String rawdata;
    //初始化
    public static void initWxFacePay(Application app){
        final String tag = getTag();
        Log.i(TAG,"初始化微信人脸支付开始");
        WxPayFace.getInstance().initWxpayface(app.getApplicationContext(), new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                //inti结果
                if(!isSuccessInfo(info,tag)){
                    //初始化失败
                }else{
                    //执行自定义逻辑
                }
            }
        });

    }
    public static void getWxpayfaceRawdata() {
        final String tag = getTag();
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                if (map == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    return;
                }
                String msg = (String) map.get("return_msg");
                rawdata = map.get("rawdata").toString();
                if (isSuccessInfo(map,tag)) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    return ;
                }
       	        /*
       	        在这里处理您自己的业务逻辑
       	         */
            }
        });
    }




    public static boolean doGetFaceCode(final String authinfo,String orderno) {
        Map<String, String> m1 = new HashMap<String, String>();
        m1.put("appid", "填您的微信公众号"); // 公众号，必填
        m1.put("mch_id", "填您的商户号"); // 商户号，必填
//        m1.put("sub_appid", "xxxxxxxxxxx"); // 子商户公众账号ID(非服务商模式不填)
//        m1.put("sub_mch_id", "xxxxxxxxxxx"); // 子商户号(非服务商模式不填)
        m1.put("store_id", "填您的门店编号"); // 门店编号，必填
//        m1.put("telephone", "用户手机号"); // 用户手机号，用于传递会员手机号到界面输入栏，非必填
        m1.put("out_trade_no", "填您的商户订单号"); // 商户订单号， 必填
        m1.put("authinfo", authinfo); // 调用凭证
        m1.put("total_fee", "填订单的金额"); // 订单金额（数字），单位：分，必填
        m1.put("face_authtype", "FACEPAY"); // FACEPAY：人脸凭证，常用于人脸支付    FACEPAY_DELAY：延迟支付   必填
        m1.put("ask_face_permit", "0"); // 展开人脸识别授权项，详情见上方接口参数，必填
//        m1.put("ask_ret_page", "0"); // 是否展示微信支付成功页，可选值："0"，不展示；"1"，展示，非必填
        WxPayFace.getInstance().getWxpayfaceCode(m1, new IWxPayfaceCallback() {
            @Override
            public void response(final Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    return;
                }
                String code = (String) info.get("return_code"); // 错误码
                String msg = (String) info.get("return_msg"); // 错误码描述
                String faceCode = info.get("face_code").toString(); // 人脸凭证，用于刷脸支付
                String openid = info.get("openid").toString(); // openid
                String sub_openid = ""; // 子商户号下的openid(服务商模式)
                int telephone_used = 0; // 获取的`face_code`，是否使用了请求参数中的`telephone`
                int underage_state = 0; // 用户年龄信息（需联系微信支付开通权限）
                if (info.get("sub_openid") != null) sub_openid = info.get("sub_openid").toString();
                if (info.get("telephone_used") != null) telephone_used = Integer.parseInt(info.get("telephone_used").toString());
                if (info.get("underage_state") != null) underage_state = Integer.parseInt(info.get("underage_state").toString());
                if (code == null || faceCode == null || openid == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    return ;
                }
       	        /*
       	        在这里处理您自己的业务逻辑
       	        解释：您在上述中已经获得了支付凭证或者用户的信息，您可以使用这些信息通过调用支付接口来完成支付的业务逻辑
       	        需要注意的是：
       	            1、上述注释中的内容并非是一定会返回的，它们是否返回取决于相应的条件
       	            2、当您确保要解开上述注释的时候，请您做好空指针的判断，不建议直接调用
       	         */
       	        //调用支付
                boolean payResult = pay(faceCode,openid,authinfo);
                //分析支付结果
                //调用update
                updateWxpayfacePayResult(authinfo,payResult);
            }
        });
        return true;
    }
    private static void updateWxpayfacePayResult(String authinfo,boolean payResult) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("appid", "填您的公众号"); // 公众号，必填
        map.put("mch_id", "填您的商户号"); // 商户号，必填
        map.put("store_id", "填您的门店编号"); // 门店编号，必填
        map.put("authinfo", "填您的调用凭证"); // 调用凭证，必填
        map.put("payresult", "SUCCESS"); // 支付结果，SUCCESS:支付成功   ERROR:支付失败   必填
        if( !payResult){
            map.put("payresult", "ERROR"); // 支付结果，SUCCESS:支付成功   ERROR:支付失败   必填
        }

        WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (info == null) {
                    new RuntimeException("调用返回为空").printStackTrace();
                    return;
                }
                String code = (String) info.get("return_code"); // 错误码
                String msg = (String) info.get("return_msg"); // 错误码描述
                if (code == null || !code.equals("SUCCESS")) {
                    new RuntimeException("调用返回非成功信息,return_msg:" + msg + "   ").printStackTrace();
                    return ;
                }
                /*
                在这里处理您自己的业务逻辑：
                执行到这里说明用户已经确认支付结果且成功了，此时刷脸支付界面关闭，您可以在这里选择跳转到其它界面
                 */
            }
        });
    }

    public static boolean pay(String faceCode, String openid, String authinfo) {

        return false;

    }
    /**
     * @描述 : 去后台获取人脸凭证
     * @版本 : V1.0.0
     * @日期 : 2019/8/13 16:35
     * @作者 : LiF
     * @修改人 :
     * @备注 :
     *
     */
    public static String getWxpayfaceAuthinfo() {

        //获取uuid
        //请求后台
        return null;
    }
    private static boolean isSuccessInfo(Map info,String tag) {
        BaseActivity ba = new BaseActivity();
        if (info == null) {
            //showToast("调用返回为空, 请查看日志");
            ba.showToast(StaticConf.ERROR_MSG.微信返回异常_调用返回为空.name());
            new MyException(StaticConf.RESPONSE_CODE.FAILD,StaticConf.ERROR_MSG.微信返回异常_调用返回为空,new RuntimeException(StaticConf.ERROR_MSG.微信返回异常_调用返回为空.name()));
            return false;
        }
        String method_name = new Exception().getStackTrace()[1].getMethodName();
        String code = (String)info.get(StaticConf.RETURN_CODE);
        String msg = (String)info.get(StaticConf.RETURN_MSG);
        Log.i(tag, "response | "+ method_name+" " + code + " | " + msg);
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            //showToast("调用返回非成功信息, 请查看日志");
            ba.showToast(StaticConf.ERROR_MSG.调用返回非成功信息.name());
            new MyException(StaticConf.RESPONSE_CODE.FAILD,StaticConf.ERROR_MSG.调用返回非成功信息,new RuntimeException(StaticConf.ERROR_MSG.调用返回非成功信息.name() + msg));
            return false;
        }
        Log.i(tag, "调用返回成功");
        return true;
    }

    /**
     * @描述 : 获取调用当前类的类名
     * @版本 : V1.0.0
     * @日期 : 2019/8/7 11:58
     * @作者 : LiF
     * @修改人 :
     * @备注 :
     *
     */
    private static String getTag(){
        return  new Exception().getStackTrace()[2].getClassName();
    }

}

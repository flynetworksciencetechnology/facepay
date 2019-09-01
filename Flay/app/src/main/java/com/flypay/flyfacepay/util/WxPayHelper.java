package com.flypay.flyfacepay.util;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.flypay.flyfacepay.model.FacePayResult;
import com.flypay.flyfacepay.model.Result;
import com.flypay.flyfacepay.util.http.CommonOkhttpClient;
import com.flypay.flyfacepay.util.http.CommonRequest;
import com.flypay.flyfacepay.util.http.RequestParams;
import com.flypay.flyfacepay.util.http.URI;
import com.google.gson.Gson;
import com.tencent.mars.xlog.Log;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WxPayHelper {
    private static final String TAG = "WxPayHelper";

    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_SUCCESS = "SUCCESS";
    public static final String RETURN_ERROR = "ERROR";
    public static final String RETURN_MSG = "return_msg";
    public static final String RETURN_FACEPAY = "FACEPAY";
    public static final String RETURN_ASK_RET_PAGE_VALUE = "0";
    private static final String PARAMS_FACE_AUTHTYPE = "face_authtype";
    private static final String PARAMS_APPID = "appid";
    private static final String PARAMS_SUB_APPID = "sub_appid";
    private static final String PARAMS_MCH_ID = "mch_id";
    private static final String PARAMS_SUB_MCH_ID = "sub_mch_id";
    private static final String PARAMS_STORE_ID = "store_id";
    private static final String PARAMS_AUTHINFO = "authinfo";
    private static final String PARAMS_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_TOTAL_FEE = "total_fee";
    private static final String PARAMS_TELEPHONE = "telephone";
    public static final String PARAMS_FACE_CODE = "face_code";
    public static final String PARAMS_OPENID = "openid";
    public static final String PARAMS_PAYRESULT = "payresult";
    public static final String PARAMS_ASK_RET_PAGE = "ask_ret_page";
    public static final String PARAMS_RAWDATA = "rawdata";
    public static final String PARAMS_ORDERNO = "orderno";
    private static FacePayResult sFacePayResult;

    private PayCallBack mPayCallBack;
    private Boolean isCanWxFacePay = true;

    private Context mContext;
    /**
     * 需要带入手机号
     */
    /**
     * 刷脸付是否初始化成功
     */
    public static boolean isWxFacePay = false;
    //private static FacePayResult sFacePayResult;

    private static WxPayHelper singleton;
    private static String sRawdata;

    private WxPayHelper() {
    }
    public static WxPayHelper getSingleton() {
        if (singleton == null) {
            synchronized (WxPayHelper.class) {
                if (singleton == null) {
                    singleton = new WxPayHelper();
                }
            }
        }
        return singleton;
    }

    public void setPayCallBack(PayCallBack payCallBack) {
        mPayCallBack = payCallBack;
    }
    /**
     * 初始化，程序启动时调用，建议传入getApplicationContext()
     *
     * @param context
     */
    public void initWxPay(Context context) {
//        if (!AppLiveData.isCanWxFacePay) {
//            //微信刷脸付app未安装
//            return;
//        }
        if(isWxFacePay){
            //已经初始化过
            return;
        }
        mContext = context;
        WxPayFace.getInstance().initWxpayface(context, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                if (!isSuccessInfo(map)) {
                    if (map == null) {
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, mContext.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸付初始化失败，调用返回为空");
                        Log.e(TAG,"微信刷脸付初始化失败，调用返回为空");
                    } else {
                        Log.d(TAG, "response: 初始化失败，请查看相关错误code");
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, mContext.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸付初始化失败，失败信息： code == " + map.get(RETURN_CODE) + "  msg ==  " + map.get(RETURN_MSG));
                        Log.e(TAG,"微信刷脸付初始化失败，失败信息： code == " + map.get(RETURN_CODE) + "  msg ==  " + map.get(RETURN_MSG));
                    }
                    isWxFacePay = false;
                } else {
                    Log.d(TAG, "response: 初始化成功");
                    isWxFacePay = true;
                }
            }
        });
    }

    /**
     * 初始化人脸识别资源，即初始化微信arr资源包
     * 该资源包由微信提供
     */
    public void initRawData(final String orderNo) {
        if (!isWxFacePay) {
            return;
        }
        //if(sRawdata != null){
        //    getAuthInfo(orderNo,sRawdata);
        //    return;
        //}
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                if (!isSuccessInfo(map)) {
                    if (map == null) {
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, mContext.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸初始化人脸识别资源失败，调用返回为空");
                        Log.d(TAG, "微信刷脸初始化人脸识别资源失败，调用返回为空");
                    } else {
                        Log.d(TAG, "response: 获取资源失败，请查看相关错误code");
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, mContext.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸付初始化人脸识别资源失败，失败信息： code == " + map.get(RETURN_CODE) + "  msg ==  " + map.get(RETURN_MSG));
                        Log.d(TAG, "微信刷脸付初始化人脸识别资源失败，失败信息： code == " + map.get(RETURN_CODE) + "  msg ==  " + map.get(RETURN_MSG));
                    }
                } else {
                    sRawdata = map.get("rawdata").toString();
                    Log.d(TAG, "response: rawdata ===  " + sRawdata);
                    //TODO 把请求服务器的方法写入该帮助类中，此处直接调用
                    try {
                        getAuthInfo(orderNo, sRawdata);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
    private void getAuthInfo(String orderNo, String rawdata) {
//        if (mAppServer == null) {
//            mAppServer = new AppServer();
//        }
        //请求后台
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAMS_RAWDATA,rawdata);
        params.put(PARAMS_ORDERNO,orderNo);
        //获取支付调用凭证
        CommonOkhttpClient.sendRequest(CommonRequest.initPostRequest(URI.HOST + URI.AUTHINFO, new RequestParams(params)), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e(TAG,"初始化调用认证失败");
                mPayCallBack.interfaceFail();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG,"初始化调用认证成功 : " + res);
                com.tencent.mars.xlog.Log.i(TAG,"获取认证成功 : " + res);
                Gson gson = new Gson();
                Result result = gson.fromJson(res, Result.class);
                if( result != null && "0000".equals(result.code)){
                    FacePayResult facePayResult = gson.fromJson(gson.toJson(result.data),FacePayResult.class);
                    sFacePayResult = facePayResult;
                    getFaceCode(facePayResult);
                } else{
                    Log.e(TAG,"获取调用认证失败,请检查网络信息");
                    //new ShowDialogJOB("获取调用认证失败,请检查网络信息",this.).run();
                }
            }
        });

    }
    /**
     * 调起刷脸支付，并接收回调
     */
    private void getFaceCode(FacePayResult facePayResult) {
        //如果是调用微信刷脸付时隐藏广告
        //MyApplication.getInstance().dismissAd();
        HashMap<String, String> map = new HashMap(16);
        map.put(PARAMS_FACE_AUTHTYPE, RETURN_FACEPAY);
        map.put(PARAMS_APPID, facePayResult.appid);
        map.put(PARAMS_MCH_ID, facePayResult.mch_id);
        map.put(PARAMS_STORE_ID, facePayResult.store_id);
        map.put(PARAMS_OUT_TRADE_NO, facePayResult.out_trade_no);
        map.put(PARAMS_TOTAL_FEE, facePayResult.total_fee);
        map.put(PARAMS_ASK_RET_PAGE,RETURN_ASK_RET_PAGE_VALUE);
        //map.put("ignore_update_pay_result","1");
//        if(!TextUtils.isEmpty(mPhoneNum)){
//            map.put(PARAMS_TELEPHONE, mPhoneNum);
//        }
        map.put(PARAMS_AUTHINFO, facePayResult.authinfo);
        if (!TextUtils.isEmpty(facePayResult.sub_appid)) {
            map.put(PARAMS_SUB_APPID, facePayResult.sub_appid);
        }
        if (!TextUtils.isEmpty(facePayResult.sub_mch_id)) {
            map.put(PARAMS_SUB_MCH_ID, facePayResult.sub_mch_id);
        }
        Gson song = new Gson();
        Log.i(TAG,"==============获取facecode参数 :" + song.toJson(map));
        WxPayFace.getInstance().getWxpayfaceCode(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                if (!isSuccessInfo(map)) {
                    if (map == null) {
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, mContext.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸付调起刷脸支付失败，调用返回为空");
                        Log.d(TAG, "微信刷脸付调起刷脸支付失败，调用返回为空");
                    } else {
                        Log.d(TAG, "response: 获取资源失败，请查看相关错误code");
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, mContext.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸付调起刷脸支付失败，失败信息： code == " + map.get(RETURN_CODE) + "  msg ==  " + map.get(RETURN_MSG));
                        Log.d(TAG, "微信刷脸付调起刷脸支付失败，失败信息： code == " + map.get(RETURN_CODE) + "  msg ==  " + map.get(RETURN_MSG));
                    }
                } else {
                    if (mPayCallBack != null) {
                        final String code = (String) map.get(RETURN_CODE);
                        if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                            Log.d(TAG, "run: 支付完成");
                            mPayCallBack.paySuccess(map);
                            String faceCode = (String) map.get(WxPayHelper.PARAMS_FACE_CODE);
                            String openId = (String) map.get(WxPayHelper.PARAMS_OPENID);
                            Log.d(TAG, "response: faceCode === " + faceCode);
                            Log.d(TAG, "response: openId === " + openId);
                        } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                            Log.d(TAG, "run: 用户取消");
                            mPayCallBack.payCancel("用户取消");
                        } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                            Log.d(TAG, "run: 扫码支付");
                            mPayCallBack.payCancel("扫码支付");
                        } else {
                            Log.d(TAG, "run: 发生错误   code === " + code);
                            mPayCallBack.payCancel("发生错误   code === " + code);
                        }
                    }
                }
            }
        });
    }
    /**
     * 微信刷脸付完成后，调用该方法关闭微信刷脸付app
     * @param payresult SUCCESS: 支付成功    ERROR: 支付失败
     */
    public void updateResult(String payresult){
        final HashMap map = new HashMap(16);
        if (sFacePayResult != null) {
            map.put(PARAMS_APPID, sFacePayResult.appid);
            map.put(PARAMS_MCH_ID, sFacePayResult.mch_id);
            map.put(PARAMS_STORE_ID, sFacePayResult.store_id);
            map.put(PARAMS_AUTHINFO, sFacePayResult.authinfo);
        }
        map.put(PARAMS_PAYRESULT, payresult);
        WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (isSuccessInfo(info)){
                    sFacePayResult = null;
                }

            }
        });
    }
    /**
     * 释放资源，建议在程序结束时调用
     *
     * @param context 建议使用getApplicationContext()
     */
    public void releaseData(Context context) {
        if(isWxFacePay){
            isWxFacePay = false;
            WxPayFace.getInstance().releaseWxpayface(context);
        }
    }
    /**
     * 取消接口回调
     */
    public void cancelCallBack() {
        if(!TextUtils.isEmpty(sRawdata)){
            sRawdata = null;
        }
        if (mPayCallBack != null) {
            mPayCallBack = null;
        }
    }

    /**
     * 微信刷脸付包名
     */
    public static final String WX_PACKAGE_NAME = "com.tencent.wxpayface";

    /**
     * 最新微信刷脸付版本号  TODO 每次微信刷脸付如果有更新时需要更改该版本号
     */
    public static final String CURRENT_VERSION_NAME = "1.30.288";

    public static final String WX_FACEPAY_FILE_NAME = "wxFacePay_" + CURRENT_VERSION_NAME + ".apk";

    /**
     * 判断微信人脸付app是否存在
     * 如果存在的话就调用人脸付的初始化
     * 如果不存在或者版本较低，需要从服务器下载对应版本
     *
     * @param context
     */
//    public void isSupportWxFacePay(Context context) {
////        String payway = SystemParam.getParamValue(SystemParam.PAY_WAY);
////        if (!TextUtils.isEmpty(payway) && !payway.contains(SystemParam.PAY_WAY_FACEPAY)) {
////            SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, context.getResources().getString(R.string.savelog_wxfacepay),
////                    "未配置刷脸付支付方式，不进行下载");
////            return;
////        }
//        //当前系统安装的微信刷脸付版本号
//        String wxVersionName = "";
//        //判断微信人脸付app是否存在
//        boolean wxAvilible = Util.isAvilible(context, WX_PACKAGE_NAME);
//        if (wxAvilible) {
//            try {
//                //获取微信人脸付版本号 versionName
//                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(WX_PACKAGE_NAME, 0);
//                wxVersionName = packageInfo.versionName;
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            //已经安装
//            //判断版本号是否一致
//            if (wxVersionName.compareTo(CURRENT_VERSION_NAME) >= 0) {
//                //更改状态为可以微信刷脸付
//                isCanWxFacePay = true;
//            } else {
//                //如果当前版本比最新版本低的话需要下载
//                isCanWxFacePay = false;
//                downLoadWxFacePayApp(context);
//            }
//        } else {
//            //未安装，需要下载 进行安装
//            isCanWxFacePay = false;
//            downLoadWxFacePayApp(context);
//        }
//    }


    /**
     * 下载更新app
     */
//    private void downloadApk(final Context context, String url) {
//        final String apkUrl = url;
//        String sdpath = Util.getDiskCacheDir(context);
//        String name = WX_FACEPAY_FILE_NAME;
//        String fileUrl = apkUrl;
//        if (!fileUrl.contains("http")) {
//            fileUrl = GlobalUrlConfig.currentHost + fileUrl;
//        }
//        OkHttpUtils
//                .get()
//                .url(fileUrl)
//                .addHeader("Accept-Encoding", "*")
//                .build()
//                .execute(new FileCallBack(sdpath, name) {
//                    @Override
//                    public void onBefore(Request request, int id) {
//                    }
//
//                    @Override
//                    public void inProgress(float progress, long total, int id) {
//
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        isCanWxFacePay = false;
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, context.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸付app下载失败");
//                        Log.d(TAG, "onResponse: " + "微信刷脸付app下载失败");
//                    }
//
//                    @Override
//                    public void onResponse(File file, int id) {
//                        SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, context.getResources().getString(R.string.savelog_wxfacepay),
//                                "微信刷脸付app下载完成，开始安装");
//                        Log.d(TAG, "onResponse: " + "微信刷脸付app下载完成");
//                        installWxFacePay(context, file.getAbsolutePath());
//                    }
//                });
//    }
//
//    /**
//     * 调用安装
//     *
//     * @param context
//     * @param path
//     */
//    private void installWxFacePay(Context context, String path) {
//        YuLianTool.installApk(context, path, "", "", false);
//    }
//
//    /**
//     * 获取微信人脸付app下载地址
//     */
//    public void downLoadWxFacePayApp(final Context context) {
//        if (mAppServer == null) {
//            mAppServer = new AppServer();
//        }
//        mAppServer.downloadWxApp(WX_FACEPAY_FILE_NAME, new BaseHandler<String>() {
//            @Override
//            protected void showDialog(String msg) {
//
//            }
//
//            @Override
//            protected void dismissDialog(String msg) {
//
//            }
//
//            @Override
//            protected void handleSuccessMessage(String s) {
//                downloadApk(context, s);
//            }
//
//            @Override
//            protected void handleFailMessage(String msg) {
//                AppLiveData.isCanWxFacePay = false;
//                SqlLiteClientLogUtil.getInstance().saveLog(GlobalUrlConfig.MODULE_APP, context.getResources().getString(R.string.savelog_wxfacepay),
//                        "微信刷脸付app获取下载地址失败");
//            }
//        });
//    }
    /**
     * 支付结果回调接口
     */
    public interface PayCallBack {
        /**
         * 支付成功回调
         *
         * @param successMap
         */
        void paySuccess(Map successMap);

        /**
         * 支付失败回调
         *
         * @param failMsg
         */
        void payCancel(String failMsg);

        /**
         * 接口调用失败
         */
        void interfaceFail();
    }
    private boolean isSuccessInfo(Map info) {
        if (info == null) {
            Log.d(TAG, "isSuccessInfo: 调用返回为空, 请查看日志");
            new RuntimeException("调用返回为空").printStackTrace();
            return false;
        }
        String code = (String) info.get(RETURN_CODE);
        String msg = (String) info.get(RETURN_MSG);
        Log.d(TAG, "response | getWxpayfaceRawdata " + code + " | " + msg);
        if (!TextUtils.isEmpty(code)) {
            if (mPayCallBack != null) {
                if (code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                    Log.d(TAG, "用户手动取消");
                    mPayCallBack.payCancel("用户手动取消");
                    return true;
                } else if (code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                    Log.d(TAG, "用户选择扫码支付");
                    mPayCallBack.payCancel("用户选择扫码支付");
                    return true;
                }
            }
        }
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            Log.d(TAG, "isSuccessInfo: 调用返回非成功信息, 请查看日志");
            new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
            return false;
        }
        Log.d(TAG, "调用返回成功");
        return true;
    }
}

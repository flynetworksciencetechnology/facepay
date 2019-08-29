package com.flypay.flayfacepay.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Toast;

import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.tencent.mars.xlog.Log;

import java.io.IOException;
import java.io.InputStream;

public class AIUIUtils {

    private static final String TAG = CommonUtil.getTag();
    private static AIUIUtils mAIUIUtils;
    private static AIUIAgent mAIUIAgent;
    private static AIUIListener mAIUIListener;
    private Context context;
    public static AIUIUtils getAIUIUtils(Context context) {
        context = context;
        if (mAIUIUtils == null) {
            synchronized (AIUIUtils.class) {
                if (mAIUIUtils == null) {
                    mAIUIUtils = new AIUIUtils();
                }
            }
        }
        return mAIUIUtils;
    }
    public void setAIUIListener(AIUIListener mAIUIListener) {
        this.mAIUIListener = mAIUIListener;
    }

    public void sendMessage(String message){
        StringBuffer params = new StringBuffer();  //构建合成参数
        params.append("vcn=xiaoyan");  //合成发音人
        params.append(",speed=50");  //合成速度
        params.append(",pitch=50");  //合成音调
        params.append(",volume=50");  //合成音量
        AIUIMessage startTts = new AIUIMessage(AIUIConstant.CMD_TTS,AIUIConstant.START, 0, params.toString(), message.getBytes());
        checkAIUIAgent();
        mAIUIAgent.sendMessage(startTts);
    }

    private boolean checkAIUIAgent(){
        if( null == mAIUIAgent ){
            Log.i( TAG, "create aiui agent" );
            //创建AIUIAgent
            if( mAIUIListener == null){
                mAIUIListener = defaultAIUIListener;
            }
            mAIUIAgent = AIUIAgent.createAgent( this.context, getAIUIParams(), mAIUIListener );
        }

        if( null == mAIUIAgent ){
            Log.e(TAG, "创建 AIUI Agent 失败！");
        }

        return null != mAIUIAgent;
    }
    private AIUIListener defaultAIUIListener = new AIUIListener() {

        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_TTS: {
                    switch (event.arg1) {
                        case AIUIConstant.TTS_SPEAK_BEGIN:
                            Log.e(TAG,"开始播放");
                            break;

                        case AIUIConstant.TTS_SPEAK_PROGRESS://" + mTtsBufferProgress +"
                            Log.e(TAG,"缓冲进度为, 播放进度为" + event.data.getInt("percent"));
                            break;

                        case AIUIConstant.TTS_SPEAK_PAUSED:
                            Log.e(TAG,"暂停播放");
                            break;

                        case AIUIConstant.TTS_SPEAK_RESUMED:
                            Log.e(TAG,"恢复播放");

                            break;

                        case AIUIConstant.TTS_SPEAK_COMPLETED:
                            Log.e(TAG,"播放完成");
                            break;

                        default:
                            break;
                    }
                }
                break;

                default:
                    break;
            }

        }
    };

    /**
     * 读取配置
     */
    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager =  this.context.getAssets();
        try {
            InputStream ins = assetManager.open( "cfg/aiui.cfg" );
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }

}

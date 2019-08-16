package com.flypay.flayfacepay.util;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @描述 : 公共工具类
 * @版本 : V1.0.0
 * @日期 : 2019/8/7 11:00
 * @作者 : LiF
 * @修改人 :
 * @备注 :
 *
 */
public class CommonUtil {

    /**
     * @描述 : 获取当前类的类名,目前用做log
     * @版本 : V1.0.0
     * @日期 : 2019/8/7 11:02
     * @作者 : LiF
     * @修改人 :
     * @备注 :
     *
     */
    public static String getTag(){
        return new Exception().getStackTrace()[1].getClassName();
    }

    public static String getUUID(Context context) {
        SPUtils spUtils = SPUtils.getInstance(context);
        String uuid = spUtils.getString(SPUtils.PREF_KEY_UUID);
        if( TextUtils.isEmpty(uuid)){
            uuid = getDeviceUUid();
            if (TextUtils.isEmpty(uuid)) {
                uuid = getAppUUid(context);

            }
            spUtils.put(SPUtils.PREF_KEY_UUID,uuid);
        }
        return uuid;
    }

    /**
     * @描述 : 根据uuid生成订单号
     * @版本 : V1.0.0
     * @日期 : 2019/8/13 17:48
     * @作者 : LiF
     * @修改人 :
     * @备注 :
     *
     */
    private static final Integer MAX = 6;
    public static String getId(String uuid,Context context,String idBuild){
        SPUtils spUtils = SPUtils.getInstance(context);
        //获取商户信息

        //进行订单号运算
        if( !spUtils.contains(idBuild)){
            spUtils.put(idBuild,1L);
        }
        long id = spUtils.getLong(idBuild);
        //idbuild+1
        spUtils.put(idBuild,id + 1);
        //截取UUID后四位作为前四位
        int length = uuid.length();
        uuid = uuid.substring(length - 4 , length);
        //查看当前id长度
        String idStr = String.valueOf(id);
        int id_length = idStr.length();
        int temp = 6 - id_length;
        if( temp > 0 ){
            //补0
            for (int i = 0 ; i < temp ; i++){
                idStr = "0" + idStr;
            }
        }
        idStr = uuid + idStr;
        return idStr;
    }
    private static String getAppUUid(Context context) {
        return UUID.randomUUID().toString();
    }
    private static String getDeviceUUid()
    {
        String androidId = getAndroidID();
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)androidId.hashCode() << 32));
        return deviceUuid.toString();
    }
    private static String getAndroidID() {
        String id = null;
        try{
            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class, String.class);

            id = (String) (get.invoke(c, "ro.serialno", "unknown"));
        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return id == null ? "" : id;
    }
}

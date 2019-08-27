package com.flypay.flayfacepay.util;

import android.content.Context;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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
     */
    public static String getTag() {
        return new Exception().getStackTrace()[1].getClassName();
    }

    public static String getDeviceUUid() {
        String androidId = getAndroidID();
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) androidId.hashCode() << 32));
        return deviceUuid.toString();
    }

    private static String getAndroidID() {
        String id = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class, String.class);

            id = (String) (get.invoke(c, "ro.serialno", "unknown"));
        } catch (NoSuchMethodException e) {
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

    public static void init(final Context context) {
        //HttpUtils.asynGET(HttpUtils.URI.INIT,null);
    }

    /**
     * @return String
     * @Title: getIpAddress
     * @Description: 获取设备ip地址
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces(); enNetI
                    .hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
}
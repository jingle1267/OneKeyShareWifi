package com.ihongqiqu.sharewifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.lang.reflect.Method;

/**
 * wifi AP 管理类
 * 提供 AP 状态，开关 AP
 * <p>
 * Created by zhenguo on 1/11/17.
 */
public class WifiAPMgr {

    private final String TAG = WifiAPMgr.class.getSimpleName();

    private WifiManager wifiManager;

    private static String apName = "freeAP";
    // 密码必须大于8位数
    private static String apPwd = "123456789";

    public WifiAPMgr(Context context) {
        this(context, apName, apPwd);
    }

    public WifiAPMgr(Context context, String apPwd, String apName) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiAPMgr.apName = apName;
        WifiAPMgr.apPwd = apPwd;
    }

    /**
     * 判断热点开启状态
     */
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    private WIFI_AP_STATE getWifiApState() {
        int tmp;
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(wifiManager));
            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    public enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING,
        WIFI_AP_STATE_DISABLED,
        WIFI_AP_STATE_ENABLING,
        WIFI_AP_STATE_ENABLED,
        WIFI_AP_STATE_FAILED
    }

    /**
     * check whether wifi hotspot on or off
     */
    public boolean isApOn() {
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    /**
     * wifi热点开关
     *
     * @param enabled true：打开  false：关闭
     * @return true：成功  false：失败
     */
    public boolean setWifiApEnabled(boolean enabled) {
        if (BuildConfig.DEBUG) Log.d("WifiAPMgr", "开启热点");
        if (enabled) {
            // wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
            if (BuildConfig.DEBUG) Log.d("WifiAPMgr", "关闭wifi");
        } else {
            // wifiManager.setWifiEnabled(true);
        }
        try {
            // 热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            // 配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = apName;
            // 配置热点的密码
            apConfig.preSharedKey = apPwd;
            // 安全：WPA2_PSK
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            // 通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            // 返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }

}

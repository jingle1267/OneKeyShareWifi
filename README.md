# OneKeyShareWifi

  Android 一键建立热点，并生成用户名和密码

#### OneKeyShareWifi 主要提供以下功能：

1. 获取当前 AP 的开关状态
2. 打开和关闭 AP

#### 核心实现

  检测 AP 的状态，通过反射实现：
  
```java
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
```

  打开和关闭 AP，反射实现代码如下：
  
```java
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
```

#### 参考地址：

1. [http://stackoverflow.com/questions/6394599/android-turn-on-off-wifi-hotspot-programmatically](http://stackoverflow.com/questions/6394599/android-turn-on-off-wifi-hotspot-programmatically)
2. [http://blog.csdn.net/u013049709/article/details/42235829](http://blog.csdn.net/u013049709/article/details/42235829)

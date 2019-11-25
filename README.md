## OneKeyShareWifi

  Android 一键建立热点，并生成用户名和密码。

  产品提了一个需求，希望在手机上能够一键共享自己的移动网络。且希望用户能够通过扫描二维码链接上网络。

### 思路
  
  经过各种谷歌和文档资料查询，发现 Android 的 API 是不支持对 AP 的操作。但是，这是难不倒我们的工程师的，通过谷歌搜索出来很多通过反射来管理 AP 的方法。这样第一个问题就算解决了。
  
  然而，想要实现任何应用扫码连接共享的网络，有很多问题。二维码其实就是一个字符串的一种展现形式。想通过二维码链接网络，其实就是想通过一个字符串来改变世界啊！现在用户量较大的扫二维码应用都会有一些自己的协议，要实现我们的功能，我们必须要选择了通用的协议。扫二维码通用的协议，想来想去就只有链接地址了。一般扫码应用都是支持打开链接地址的。接下来的任务就要交给我们的 H5。想要通过 H5 来实现连接 WiFi 这个功能是不现实(不知道利用 H5 的漏洞能不能搞)。其实如果用户没有网络，二维码携带任何信息都没有什么卵用的。
  
  通过反射技术来实现对热点的开关控制实现并不复杂，接下来简单介绍一下。

### 实现功能

  一键建立热点主要需要实现如下功能：

1. 获取当前 AP 的开关状态
2. 打开和关闭 AP

### 实现代码

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

### 效果展示

下载 APK 安装体验，下载地址：
[https://raw.githubusercontent.com/jingle1267/OneKeyShareWifi/master/app-debug.apk](https://raw.githubusercontent.com/jingle1267/OneKeyShareWifi/master/app-debug.apk)

### 源码地址

[https://github.com/jingle1267/OneKeyShareWifi](https://github.com/jingle1267/OneKeyShareWifi)

### 参考地址：

1. [http://stackoverflow.com/questions/6394599/android-turn-on-off-wifi-hotspot-programmatically](http://stackoverflow.com/questions/6394599/android-turn-on-off-wifi-hotspot-programmatically)
2. [http://blog.csdn.net/u013049709/article/details/42235829](http://blog.csdn.net/u013049709/article/details/42235829)

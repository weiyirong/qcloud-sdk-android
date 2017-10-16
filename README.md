# qcloud-sdk-android
腾讯云Android SDK发布仓库

## 前言  
您可以在新建项目中，配置好SDK或者在已有的项目中集成进SDK，或者先运行下Demo感受下SDK是如何运作的。下载Demo体验请前点击  [Demo地址](https://github.com/tencentyun/qcloud-sdk-android-samples.git)。  

SDK API 说明文档，请查阅 [SDK API Document](https://github.com/tencentyun/qcloud-sdk-android/blob/master/COS_XML_Android_SDK.md)

## SDK 下载包 [SDK Libs](https://github.com/tencentyun/qcloud-sdk-android/releases)

## 集成SDK

SDK包含的 jar包：

cos-xml-android-sdk-1.2.jar

qcloud-network-android-sdk-1.2.jar

okhttp-3.8.1.jar

okio-1.13.0.jar

slf4j-android-1.6.1-RC1.jar

xstream-1.4.7.jar

fastjson-1.1.60.android.jar

SDK 需要网络访问相关的一些权限，需要在 AndroidManifest.xml 中增加如下权限声明：
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

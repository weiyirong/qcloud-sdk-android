## qcloud-sdk-android
腾讯云Android SDK发布仓库

## 前言  
您可以在新建项目中，配置好SDK或者在已有的项目中集成进SDK，或者先运行下Demo感受下SDK是如何运作的.

# Demo [Demo地址](https://github.com/tencentyun/qcloud-sdk-android-samples.git). 

# SDK API 说明文档，请查阅 [SDK API Document](https://github.com/tencentyun/qcloud-sdk-android/blob/master/COS_XML_Android_SDK.md).

# SDK 变更说明， 请查阅 [SDK Chanage Documnet](https://github.com/tencentyun/qcloud-sdk-android/blob/master/CHANGELOG.md).

# SDK 下载包 [SDK Libs](https://github.com/tencentyun/qcloud-sdk-android/releases).

## 集成SDK

# SDK包含的 jar包：

- cos-xml-android-sdk-1.2.jar

- qcloud-network-android-sdk-1.2.jar

# SDK 依赖的第三方包:

- compile 'com.squareup.okhttp3:okhttp:3.8.1'

- compile 'com.alibaba:fastjson:1.1.62.android'

- compile 'com.google.code.gson:gson:2.8.0'

- compile ('com.thoughtworks.xstream:xstream:1.4.7') {
exclude group: 'xmlpull', module: 'xmlpull'
}


# SDK 需要一些相关的访问权限:
```html
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

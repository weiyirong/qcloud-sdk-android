# qcloud-sdk-android
腾讯云Android SDK发布仓库

## 前言  
您可以在新建项目，配置好SDK或者在已有的项目中集成进SDK，或者先运行下Demo感受下SDK是如何运作的。下载Demo体验请前点击[Demo地址](https://github.com/tencentyun/qcloud-sdk-android-samples.git)。  

## 集成SDK
在这里我们推荐您使用 Gradle 的方式来进行集成。在您的 build.gradle 中加入需要依赖的库即可，例如：
```
compile 'com.tencent.qcloud:network:1.1'
compile 'com.tencent.cos:cosxml:1.1'
```    

每个库可能还需要依赖一些第三方库才能正常工作，具体的集成方式可以详见具体库的文档。

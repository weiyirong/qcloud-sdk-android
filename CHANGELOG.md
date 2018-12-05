## V5.4.18
1. 修正了SDK签名算法, 默认签所有参数和头部字段，增加了安全性，如果是采用 jar 包集成的方式，qcloud-foundation jar 也需要同步更新；
2. 废弃了设置签名时间的接口，签名过期时间默认和密钥过期时间一样；
3. 修正了 PostObject 在主线程获取密钥的 bug；
4. 重构了设置域名的接口，增加了多种设置方式；
5. 增加了接口获取请求性能参数；
6. 修复了上传请求不会触发本地超时机制的bug；
7. 修复了其他一些bug；

## V5.4.17

1. CosClientException中增加了errorcode,用于区分CosClientException类型
2. 修复了使用TransferManager上传、下载、复制遇到不能抛出异常错误的bug问题
3. 为了满足Android 版本兼容性，更改了日志显示控件，使用ListView替代RecycleView
4. 更改了mta依赖问题，使用gradle方式替代jar包形式

## V5.4.14 ~ V5.4.16

1. 修复了遇到的bug
2. 增加了查询日志显示功能
3. CosXmlService 支持 QCloudSigner 方式鉴权。

## V5.4.13

1. 添加COSXMLUploadTask 代替 UploadServer
2. 添加COSDownloadTask 代替 Downloader
3. 添加COSXMLCopyTask 代替 CopyServer
4. 引入腾讯 mta检测

## V5.4.13 

1. 修复 bug。

## V5.4.12 

1. gradle 集成由 aar 包变为 jar 包；
2. 给 CosSimpleService 添加 addVerifiedHost() 接口，不对特定的 host 校验 HTTPS 证书。

## 5.4.11

1. 修复 QCloudTask executeNow() 方法在特殊情况下的偶发 NullPointerExcetion；
2. CosXmlSimpleService 缓存信息路径由 getExternalCacheDir() 变更为 getFilesDir()。

## V5.4.8 ~ 5.4.10

1. 修复特殊情况计算签名的bug。
2. 支持动态加速，包括普通上传和 UploadService 上传；
3. 支持 SSEC 和 SEE-KMS 加密。

## V5.4.7

1. 增加 bolts-tasks 库。

## V5.4.6

1. 修复 UploadService 上传 bug。

## V5.4.5

1. 修复 bug。


## V1.3 

缩小了包的体积大小；


## V1.2 

1. 所有 Request API, 均只提供了带参数的构造方法；
2. 支持 CAM方式 获取临时密钥，具体请查看 `com.tencent.qcloud.core.network.auth.LocalSessionCredentialProvider`

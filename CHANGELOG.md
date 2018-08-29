
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

package com.tencent.qcloud.netdemo.cosjson;


import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.request.serializer.body.RequestJsonBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseJsonBodyLowestSerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpOkSerializer;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class DeleteDirRequest extends QCloudHttpRequest {


    private String bucket;

    private String path;

    public DeleteDirRequest(String bucket, String path) {

        // 初始化bucket和path
        this.bucket = bucket;
        this.path = path;
    }


    @Override
    public void build() {
        // 请求类型：
        // Constance.REQUEST_TYPE_CMD : 命令型请求  默认值
        // Constance.REQUEST_TYPE_LOAD : 上传下载式请求
        //requestType = QCloudRequestConst.QCLOUD_REQUEST_TYPE_CMD;

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_HIGH;

        // HTTP请求的method
        // Constance.HTTP_METHOD_POST  POST 默认值
        // Constance.HTTP_METHOD_GET   GET
        requestOriginBuilder.method(QCloudNetWorkConst.HTTP_METHOD_POST);

        // 获得序列化url所需参数
        requestOriginBuilder.pathAddRear(bucket);
        requestOriginBuilder.pathAddRear(path);

        // HTTP请求的Header
        //Map<String, String> headers = requestData.getHeaderKeyValues();

        // HTTP请求的body
        RequestJsonBodySerializer requestJsonBodySerializer = new RequestJsonBodySerializer();
        requestJsonBodySerializer.bodyKeyValue(ModelConstance.OP, ModelConstance.OP_DELETE);
        requestBodySerializer = requestJsonBodySerializer;

        // 签名需要的参数
        //getSignKeyValues().put("b", bucket);
        //getSignKeyValues().put("f", "C://Files//");

        //sourceSerializer = new CosV4SignatureSourceSerializer(bucket, path, 6000);

        // 执行任务需要的一些耗时任务
        //List<QCloudRequestAction> actions = getRequestActions();
        //actions.add(new QCloudSignatureEncryptAction(requestData, sourceSerializer, sourceEncryptor));

        // 请求序列化过程



        // 相应反序列化过程
        responseSerializer = new HttpOkSerializer();

        responseBodySerializer = new ResponseJsonBodyLowestSerializer(DeleteDirResult.class);
    }
}

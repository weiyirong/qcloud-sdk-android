package com.tencent.qcloud.netdemo.cosjson;

import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.request.serializer.body.RequestFormDataSerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseJsonBodyLowestSerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpOkSerializer;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class PutObjectRequest extends QCloudHttpRequest {

    private final String bucket;

    private final String cosPath;

    private final String filePath;

    public PutObjectRequest(String bucket, String cosPath, String filePath) {

        this.bucket = bucket;
        this.cosPath = cosPath;
        this.filePath = filePath;
    }

    public String getBucket() {
        return bucket;
    }

    public String getCosPath() {
        return cosPath;
    }

    @Override
    public void build() {

        //requestType = QCloudRequestConst.QCLOUD_REQUEST_TYPE_LOAD;
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        requestOriginBuilder.method(QCloudNetWorkConst.HTTP_METHOD_POST);

        requestOriginBuilder.pathAddRear(bucket);
        requestOriginBuilder.pathAddRear(cosPath);



        // 签名需要的参数
        getSignKeyValues().put("b", bucket);
        getSignKeyValues().put("f", "");


        // 执行任务需要的一些耗时任务
        //List<QCloudRequestAction> actions = getRequestActions();
        //actions.add(new QCloudSignatureEncryptAction(requestData, sourceSerializer, sourceEncryptor));


        RequestFormDataSerializer formDataSerializer = new RequestFormDataSerializer();

        formDataSerializer.bodyKeyValue(ModelConstance.OP, ModelConstance.OP_UPLOAD);
        formDataSerializer.bodyKeyValue(ModelConstance.BIZ_ATTR, "");
        formDataSerializer.bodyKeyValue("insertOnly", "0");

        formDataSerializer.uploadFile(filePath, "filecontent");

        requestBodySerializer = formDataSerializer;

        // 相应反序列化过程
        responseSerializer = new HttpOkSerializer();

        responseBodySerializer = new ResponseJsonBodyLowestSerializer(PutObjectResult.class);
    }
}

package com.tencent.qcloud.netdemo.cosjson;

import android.content.Context;

import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.QCloudResultListener;
import com.tencent.qcloud.network.QCloudService;
import com.tencent.qcloud.network.action.QCloudRequestAction;
import com.tencent.qcloud.network.common.QCloudNetWorkConst;
import com.tencent.qcloud.network.cosv4.CosV4CredentialProvider;
import com.tencent.qcloud.network.action.QCloudSignatureAction;

import java.util.List;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CosService extends QCloudService{

    /**
     *
     * @param context
     * @param serviceConfig 服务配置
     */
    public CosService(Context context, CosServiceConfig serviceConfig, CosV4CredentialProvider credentialProvider) {

        super(context, serviceConfig, credentialProvider);

        // 设置网络地址参数   // gz.file.myqcloud.com
        //serviceConfig.set(serviceConfig.getRegion()+".file.myqcloud.com"); // bucket-appid.cosgz.myqcloud.com
        serviceConfig.setUserAgent("cos-sdk-android-4.2.0");

    }

    /**
     * 如果用户使用了这个构造函数，那么用户在发送所有的请求前都必须主动设置签名
     *
     * @param context
     * @param cosServiceConfig
     */
    public CosService(Context context, CosServiceConfig cosServiceConfig) {
        this(context, cosServiceConfig, null);
    }


    /**
     * 同步发送请求
     * @param createDirRequest
     * @return
     * @throws QCloudException
     */
    public CreateDirResult createDir(CreateDirRequest createDirRequest) throws QCloudException{

        buildRequest(createDirRequest);
        return (CreateDirResult) requestManager.send(createDirRequest);
    }


    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws QCloudException {

        buildRequest(putObjectRequest);
        return (PutObjectResult) requestManager.send(putObjectRequest);

    }

    public DeleteDirResult deleteDir(DeleteDirRequest deleteDirRequest) throws QCloudException {

        buildRequest(deleteDirRequest);
        return (DeleteDirResult) requestManager.send(deleteDirRequest);
    }

    public QueryStateResult queryState(QueryStateRequest queryStateRequest) throws QCloudException {

        buildRequest(queryStateRequest);
        return (QueryStateResult) requestManager.send(queryStateRequest);
    }

    public UpdateFileAttrsResult updateFileAttrs(UpdateFileAttrsRequest updateFileAttrsRequest) throws QCloudException {

        buildRequest(updateFileAttrsRequest);
        return (UpdateFileAttrsResult) requestManager.send(updateFileAttrsRequest);
    }

    /**
     * 取消请求
     * @param cosRequest
     */
    public void cancel(QCloudHttpRequest cosRequest) {

        requestManager.cancel(cosRequest);
    }

    // 设置请求公用不变参数
    @Override
    protected void buildRequest(QCloudHttpRequest cosRequest) throws QCloudException{

        super.buildRequest(cosRequest);
        cosRequest.getRequestOriginBuilder().pathAddFront(((CosServiceConfig)serviceConfig).getAppid());
        cosRequest.getRequestOriginBuilder().pathAddFront("files/v2");


        // 执行任务需要的一些耗时任务
//        List<QCloudRequestAction> actions = cosRequest.getRequestActions();
//        if (!cosRequest.getRequestOriginBuilder().getHeaders().containsKey(QCloudNetWorkConst.HTTP_HEADER_AUTHORIZATION)
//                && credentialProvider != null) { // 如果用户没有直接设置签名，那么需要向用户请求签名
//            actions.add(new QCloudSignatureAction(cosRequest, (CosV4CredentialProvider) credentialProvider));
//        }

    }

}

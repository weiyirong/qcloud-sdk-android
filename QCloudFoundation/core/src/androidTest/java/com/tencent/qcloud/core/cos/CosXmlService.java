package com.tencent.qcloud.core.cos;

import android.content.Context;

import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.network.QCloudCall;
import com.tencent.qcloud.core.network.QCloudHttpRequest;
import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.QCloudResultListener;
import com.tencent.qcloud.core.network.QCloudService;
import com.tencent.qcloud.core.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.network.exception.QCloudClientException;

public class CosXmlService {

    private QCloudService mService;

    public CosXmlService(Context context, CosXmlServiceConfig serviceConfig, QCloudCredentialProvider basicCredentialProvider) {
        mService = new QCloudService.Builder(context)
                .serviceConfig(serviceConfig)
                .credentialProvider(basicCredentialProvider)
                .build();
        CosXmlServiceConfig.instance = serviceConfig;
    }

    public QCloudService getCloudService() {
        return mService;
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketRequest}
     */
    public QCloudCall getBucketAsync(GetBucketRequest request, QCloudResultListener cosXmlResultListener){
        return mService.enqueue(request, cosXmlResultListener);
    }

    public <T extends QCloudResult> T execute(QCloudHttpRequest<T> request) throws QCloudClientException {
        return mService.execute(request);
    }

    public <T extends QCloudResult> QCloudCall enqueue(QCloudHttpRequest<T> request,
                                                       QCloudResultListener<QCloudHttpRequest<T>, T> listener) {
        return mService.enqueue(request, listener);
    }

    public void cancelAll() {
        mService.cancelAll();
    }


}

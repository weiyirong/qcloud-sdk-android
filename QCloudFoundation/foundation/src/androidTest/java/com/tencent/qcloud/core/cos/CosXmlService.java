package com.tencent.qcloud.core.cos;

import android.content.Context;

import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.cos.bucket.DeleteBucketRequest;
import com.tencent.qcloud.core.cos.bucket.DeleteBucketResult;
import com.tencent.qcloud.core.cos.bucket.GetBucketRequest;
import com.tencent.qcloud.core.cos.bucket.GetBucketResult;
import com.tencent.qcloud.core.cos.bucket.PutBucketRequest;
import com.tencent.qcloud.core.cos.bucket.PutBucketResult;
import com.tencent.qcloud.core.cos.common.RequestContentType;
import com.tencent.qcloud.core.cos.common.RequestHeader;
import com.tencent.qcloud.core.cos.common.RequestMethod;
import com.tencent.qcloud.core.cos.object.DeleteObjectRequest;
import com.tencent.qcloud.core.cos.object.DeleteObjectResult;
import com.tencent.qcloud.core.cos.object.GetObjectRequest;
import com.tencent.qcloud.core.cos.object.PutObjectRequest;
import com.tencent.qcloud.core.cos.object.PutObjectResult;
import com.tencent.qcloud.core.http.BuildConfig;
import com.tencent.qcloud.core.http.HttpConstants;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.http.QCloudHttpClient;
import com.tencent.qcloud.core.http.QCloudHttpRequest;
import com.tencent.qcloud.core.http.RequestBodySerializer;
import com.tencent.qcloud.core.http.ResponseBodyConverter;
import com.tencent.qcloud.core.task.Task;

import java.io.File;
import java.util.List;

public class CosXmlService {

    private QCloudHttpClient client;
    private QCloudCredentialProvider credentialProvider;
    private Context context;

    public CosXmlService(Context context, CosXmlServiceConfig serviceConfig, QCloudCredentialProvider basicCredentialProvider) {
        this.context = context;
        client = QCloudHttpClient.getDefault();
        client.setDebuggable(true);
        client.addVerifiedHost(serviceConfig.getHost());
        credentialProvider = basicCredentialProvider;
        CosXmlServiceConfig.instance = serviceConfig;
    }

    public <T> HttpResult<T> execute(QCloudHttpRequest<T> request) throws QCloudClientException, QCloudServiceException {
        return client.resolveRequest(request, credentialProvider).executeNow();
    }

    public <T> Task<HttpResult<T>> schedule(QCloudHttpRequest<T> request, QCloudResultListener<HttpResult<T>> listener) {
        return client.resolveRequest(request, credentialProvider)
                .schedule()
                .addResultListener(listener);
    }

    public QCloudHttpRequest<GetBucketResult> buildGetBucketRequest(GetBucketRequest request) {
        return newRequestBuilder(request)
                .method(RequestMethod.GET)
                .addHeader(RequestHeader.CONTENT_TYPE,RequestContentType.X_WWW_FORM_URLENCODE)

//                .query("prefix",request.getPrefix())
//                .query("delimiter",request.getDelimiter())
//                .query("encoding-type",request.getEncodingType())
//                .query("marker",request.getMarker())
//                .query("max-keys",request.getMaxKeys() + "")

                .converter(new ResponseXmlS3BodyConverter<GetBucketResult>(GetBucketResult.class))
                .build();
    }

    public QCloudHttpRequest<DeleteBucketResult> buildDeleteBucketRequest(DeleteBucketRequest request) {
        return newRequestBuilder(request)
                .method(RequestMethod.DELETE)
                .addHeader(RequestHeader.CONTENT_TYPE,RequestContentType.X_WWW_FORM_URLENCODE)
                .converter(new ResponseXmlS3BodyConverter<DeleteBucketResult>(DeleteBucketResult.class))
                .build();
    }

    public QCloudHttpRequest<PutBucketResult> buildPutBucketRequest(PutBucketRequest request) {
        return newRequestBuilder(request)
                .method(RequestMethod.PUT)
                .addHeader(RequestHeader.CONTENT_TYPE,RequestContentType.X_WWW_FORM_URLENCODE)
                .body(RequestBodySerializer.bytes(RequestContentType.TEXT_PLAIN, new byte[0]))
                .converter(new ResponseXmlS3BodyConverter<PutBucketResult>(PutBucketResult.class))
                .build();
    }

    public QCloudHttpRequest<Void> buildGetObjectRequest(GetObjectRequest request) {
        return newRequestBuilder(request)
                .method(RequestMethod.GET)
                .addHeader(RequestHeader.CONTENT_TYPE,RequestContentType.X_WWW_FORM_URLENCODE)
                .path(request.getCosPath())

                .query("response-content-type",request.getRspContentType())
                .query("response-content-language",request.getRspContentLanguage())
                .query("response-expires",request.getRspExpires())
                .query("response-cache-control",request.getRspCacheControl())
                .query("response-content-dispositio",request.getRspContentDispositon())
                .query("response-content-encoding",request.getRspContentEncoding())

                .converter(ResponseBodyConverter.file(request.getSavePath()))
                .build();
    }

    public QCloudHttpRequest<DeleteObjectResult> buildDeleteObjectRequest(DeleteObjectRequest request) {
        return newRequestBuilder(request)
                .method(RequestMethod.DELETE)
                .addHeader(RequestHeader.CONTENT_TYPE,RequestContentType.X_WWW_FORM_URLENCODE)
                .path(request.getCosPath())
                .converter(new ResponseXmlS3BodyConverter<DeleteObjectResult>(DeleteObjectResult.class))
                .build();
    }

    public QCloudHttpRequest<PutObjectResult> buildPutObjectRequest(PutObjectRequest request) {
        QCloudHttpRequest.Builder<PutObjectResult> builder =  newRequestBuilder(request)
                .method(RequestMethod.PUT)
                .addHeader(RequestHeader.CONTENT_TYPE, RequestContentType.TEXT_PLAIN)
                .path(request.getCosPath())
                .converter(new ResponseXmlS3BodyConverter<PutObjectResult>(PutObjectResult.class));

        if(request.getSrcPath() != null){
            builder.body(RequestBodySerializer.file(RequestContentType.TEXT_PLAIN, new File(request.getSrcPath())));
        } else if(request.getData() != null){
            builder.body(RequestBodySerializer.bytes(RequestContentType.TEXT_PLAIN, request.getData()));
        }else if(request.getInputStream() != null){
            builder.body(RequestBodySerializer.stream(RequestContentType.TEXT_PLAIN, context,
                    request.getInputStream(), 0, request.getFileLength()));
        }

        return builder.build();
    }

    private <T> QCloudHttpRequest.Builder<T> newRequestBuilder(CosXmlRequest<T> request) {
        return new QCloudHttpRequest.Builder<T>()
                .scheme(HttpConstants.Scheme.HTTP)
                .host(request.getBucket() + CosXmlServiceConfig.getInstance().host)
                .userAgent("cos-xml-android-sdk-" + BuildConfig.VERSION_NAME)
                .setUseCache(false)
                .tag("cosxml")
                .signer("CosXmlSigner", request.getSignSourceProvider());
    }

    public void cancelAll() {
        List<HttpTask> tasks = client.getTasksByTag("cosxml");
        for (HttpTask task : tasks) {
            task.cancel();
        }
    }
}

package com.tencent.cos.xml;

import android.content.Context;

import com.tencent.cos.xml.common.Range;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.transfer.ResponseFileBodySerializer;
import com.tencent.cos.xml.transfer.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpConstants;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.http.QCloudHttpClient;
import com.tencent.qcloud.core.http.QCloudHttpRequest;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.List;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public class CosXmlSimpleService implements SimpleCosXml {
    protected QCloudHttpClient client;
    protected QCloudCredentialProvider credentialProvider;
    protected String scheme;
    protected String region;
    protected String appid;
    protected String tag = "CosXml";
    public CosXmlSimpleService(Context context, CosXmlServiceConfig configuration, QCloudCredentialProvider qCloudCredentialProvider){
        QCloudLogger.setUp(context);
        client = QCloudHttpClient.getDefault();
        client.addVerifiedHost("*.myqcloud.com");
        client.setDebuggable(configuration.isDebuggable());
        scheme = configuration.getProtocol();
        region = configuration.getRegion();
        appid = configuration.getAppid();
        credentialProvider = qCloudCredentialProvider;
    }
    protected <T1 extends CosXmlRequest, T2 extends CosXmlResult> QCloudHttpRequest buildHttpRequest(T1 cosXmlRequest, T2 cosXmlResult) throws CosXmlClientException {
        cosXmlRequest.checkParameters();
        String host = cosXmlRequest.getHost(appid, region);
        QCloudHttpRequest.Builder<T2> httpRequestBuilder = new QCloudHttpRequest.Builder<T2>()
                .method(cosXmlRequest.getMethod())
                .scheme(scheme)
                .host(host)
                .path(cosXmlRequest.getPath())
                .addHeader(HttpConstants.Header.HOST, host)
                .userAgent(CosXmlServiceConfig.DEFAULT_USER_AGENT)
                .setUseCache(false)
                .tag(tag)
                .signer("CosXmlSigner", cosXmlRequest.getSignSourceProvider());

        httpRequestBuilder.query(cosXmlRequest.getQueryString());
        httpRequestBuilder.addHeaders(cosXmlRequest.getRequestHeaders());

        if(cosXmlRequest.isNeedMD5()){
            httpRequestBuilder.contentMD5();
        }

        if(cosXmlRequest.getRequestBody() != null){
              httpRequestBuilder.body(cosXmlRequest.getRequestBody());
        }

        if(cosXmlRequest instanceof GetObjectRequest){
            String absolutePath = ((GetObjectRequest) cosXmlRequest).getDownloadPath();
            Range range = ((GetObjectRequest) cosXmlRequest).getRange();
            long start = 0;
            if(range != null){
                start = range.getStart();
            }
            httpRequestBuilder.converter(new ResponseFileBodySerializer<T2>((GetObjectResult) cosXmlResult, absolutePath, start));
        }else {
            httpRequestBuilder.converter(new ResponseXmlS3BodySerializer<T2>(cosXmlResult));
        }

        QCloudHttpRequest httpRequest = httpRequestBuilder.build();
        return httpRequest;
    }

    protected <T1 extends CosXmlRequest, T2 extends  CosXmlResult> T2 execute(T1 cosXmlRequest, T2 cosXmlResult)
            throws CosXmlClientException, CosXmlServiceException {
        try {
            QCloudHttpRequest<T2> httpRequest = buildHttpRequest(cosXmlRequest, cosXmlResult);
            HttpTask<T2> httpTask = client.resolveRequest(httpRequest, credentialProvider);

            cosXmlRequest.setTask(httpTask);

            if(cosXmlRequest instanceof AppendObjectRequest){
                httpTask.addProgressListener(((AppendObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof PutObjectRequest){
                httpTask.addProgressListener(((PutObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof UploadPartRequest){
                httpTask.addProgressListener(((UploadPartRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof GetObjectRequest){
                httpTask.addProgressListener(((GetObjectRequest) cosXmlRequest).getProgressListener());
            }
            return httpTask.executeNow().content();
        } catch (QCloudServiceException e) {
            throw (CosXmlServiceException) e;
        } catch (QCloudClientException e) {
           throw new CosXmlClientException(e);
        }
    }

    protected  <T1 extends CosXmlRequest, T2 extends  CosXmlResult> void schedule(final T1 cosXmlRequest, T2 cosXmlResult,
                                                                              final CosXmlResultListener cosXmlResultListener) {

        QCloudResultListener<HttpResult<T2>> qCloudResultListener = new QCloudResultListener<HttpResult<T2>>() {
            @Override
            public void onSuccess(HttpResult<T2> result) {
                cosXmlResultListener.onSuccess(cosXmlRequest, result.content());
            }

            @Override
            public void onFailure(QCloudClientException clientException, QCloudServiceException serviceException) {
                if(clientException != null){
                    cosXmlResultListener.onFail(cosXmlRequest, new CosXmlClientException(clientException), null);
                }else {
                    cosXmlResultListener.onFail(cosXmlRequest,null, (CosXmlServiceException)serviceException);
                }

            }
        };

        try {
            QCloudHttpRequest<T2> httpRequest = buildHttpRequest(cosXmlRequest, cosXmlResult);
            HttpTask<T2> httpTask = client.resolveRequest(httpRequest, credentialProvider);

            cosXmlRequest.setTask(httpTask);

            if(cosXmlRequest instanceof AppendObjectRequest){
                httpTask.addProgressListener(((AppendObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof PutObjectRequest){
                httpTask.addProgressListener(((PutObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof UploadPartRequest){
                httpTask.addProgressListener(((UploadPartRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof GetObjectRequest){
                httpTask.addProgressListener(((GetObjectRequest) cosXmlRequest).getProgressListener());
            }

            httpTask.schedule().addResultListener(qCloudResultListener);
        }catch (QCloudClientException e) {
            cosXmlResultListener.onFail(cosXmlRequest, new CosXmlClientException(e),
                    null);
        }
    }

    public String getAccessUrl(CosXmlRequest cosXmlRequest){
        String host = cosXmlRequest.getHost(appid, region);
        String path = cosXmlRequest.getPath();
        return host + path;
    }

    @Override
    public InitMultipartUploadResult initMultipartUpload(InitMultipartUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new InitMultipartUploadResult());
    }

    @Override
    public void initMultipartUploadAsync(InitMultipartUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new InitMultipartUploadResult(), cosXmlResultListener);
    }

    @Override
    public ListPartsResult listParts(ListPartsRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new ListPartsResult());
    }

    @Override
    public void listPartsAsync(ListPartsRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new ListPartsResult(), cosXmlResultListener);
    }

    @Override
    public UploadPartResult uploadPart(UploadPartRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new UploadPartResult());
    }

    @Override
    public void uploadPartAsync(UploadPartRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new UploadPartResult(), cosXmlResultListener);
    }

    @Override
    public AbortMultiUploadResult abortMultiUpload(AbortMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new AbortMultiUploadResult());
    }

    @Override
    public void abortMultiUploadAsync(AbortMultiUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new AbortMultiUploadResult(), cosXmlResultListener);
    }

    @Override
    public CompleteMultiUploadResult completeMultiUpload(CompleteMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        CompleteMultiUploadResult completeMultiUploadResult =  new CompleteMultiUploadResult();
        completeMultiUploadResult.accessUrl = getAccessUrl(request);
        return execute(request, completeMultiUploadResult);
    }

    @Override
    public void completeMultiUploadAsync(CompleteMultiUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        CompleteMultiUploadResult completeMultiUploadResult =  new CompleteMultiUploadResult();
        completeMultiUploadResult.accessUrl = getAccessUrl(request);
        schedule(request, completeMultiUploadResult, cosXmlResultListener);
    }

    @Override
    public DeleteObjectResult deleteObject(DeleteObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteObjectResult());
    }

    @Override
    public void deleteObjectAsync(DeleteObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteObjectResult(), cosXmlResultListener);
    }

    @Override
    public GetObjectResult getObject(GetObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetObjectResult());
    }

    @Override
    public void getObjectAsync(GetObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetObjectResult(), cosXmlResultListener);
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        PutObjectResult putObjectResult =  new PutObjectResult();
        putObjectResult.accessUrl = getAccessUrl(request);
        return execute(request, putObjectResult);
    }

    @Override
    public void putObjectAsync(PutObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        PutObjectResult putObjectResult =  new PutObjectResult();
        putObjectResult.accessUrl = getAccessUrl(request);
        schedule(request, putObjectResult, cosXmlResultListener);
    }

    @Override
    public void cancel(CosXmlRequest cosXmlRequest) {
        if (cosXmlRequest != null && cosXmlRequest.getHttpTask() != null) {
            cosXmlRequest.getHttpTask().cancel();
        }
    }

    @Override
    public void cancelAll() {
        List<HttpTask> tasks = client.getTasksByTag(tag);
        for(HttpTask task : tasks){
            task.cancel();
        }
    }

    @Override
    public void release() {
        cancelAll();
    }
}

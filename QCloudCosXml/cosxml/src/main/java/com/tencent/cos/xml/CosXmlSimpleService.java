package com.tencent.cos.xml;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.cos.xml.common.ClientErrorCode;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.BaseMultipartUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.CopyObjectResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.cos.xml.model.object.GetObjectBytesRequest;
import com.tencent.cos.xml.model.object.GetObjectBytesResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.HeadObjectResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.PostObjectRequest;
import com.tencent.cos.xml.model.object.PostObjectResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartCopyRequest;
import com.tencent.cos.xml.model.object.UploadPartCopyResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.transfer.ResponseBytesConverter;
import com.tencent.cos.xml.transfer.ResponseFileBodySerializer;
import com.tencent.cos.xml.transfer.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.utils.URLEncodeUtils;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;
import com.tencent.qcloud.core.auth.QCloudSigner;
import com.tencent.qcloud.core.auth.SignerFactory;
import com.tencent.qcloud.core.auth.StaticCredentialProvider;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpConstants;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.http.NetworkClient;
import com.tencent.qcloud.core.http.OkHttpClientImpl;
import com.tencent.qcloud.core.http.QCloudHttpClient;
import com.tencent.qcloud.core.http.QCloudHttpRequest;
import com.tencent.qcloud.core.http.QCloudHttpRetryHandler;
import com.tencent.qcloud.core.logger.FileLogAdapter;
import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.task.QCloudTask;
import com.tencent.qcloud.core.task.RetryStrategy;
import com.tencent.qcloud.core.task.TaskExecutors;
import com.tencent.qcloud.core.util.ContextHolder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executor;


/**
 * Created by bradyxiao on 2017/11/30.
 */

public class CosXmlSimpleService implements SimpleCosXml {

    protected static volatile QCloudHttpClient client;
    protected QCloudCredentialProvider credentialProvider;
    protected String tag = "CosXml";
    protected String signerType = "CosXmlSigner";
    protected CosXmlServiceConfig config;
    public static String appCachePath;      // 用于缓存临时文件

    private String requestDomain;

    private String getServiceRequestDomain;

    /**
     * cos android SDK 服务
     *
     * @param context                  Application 上下文{@link android.app.Application}
     * @param configuration            cos android SDK 服务配置{@link CosXmlServiceConfig}
     * @param qCloudCredentialProvider cos android SDK 签名提供者 {@link QCloudCredentialProvider}
     */
    public CosXmlSimpleService(Context context, CosXmlServiceConfig configuration,
                               QCloudCredentialProvider qCloudCredentialProvider) {
        this(context, configuration);
        credentialProvider = qCloudCredentialProvider;
    }

    /**
     * cos android SDK 服务
     *
     * @param context       Application 上下文{@link android.app.Application}
     * @param configuration cos android SDK 服务配置{@link CosXmlServiceConfig}
     */
    public CosXmlSimpleService(Context context, CosXmlServiceConfig configuration) {
        if(configuration.isDebuggable()){
            FileLogAdapter fileLogAdapter = FileLogAdapter.getInstance(context, "QLog");
            LogServerProxy.init(context, fileLogAdapter);
            QCloudLogger.addAdapter(fileLogAdapter);
        }
        MTAProxy.init(context.getApplicationContext());
        appCachePath = context.getApplicationContext().getFilesDir().getPath();

        if (client == null) {
            synchronized (CosXmlSimpleService.class) {
                if (client == null) {
//                    QCloudHttpClient.Builder builder = new QCloudHttpClient.Builder()
//                            .setConnectionTimeout(configuration.getConnectionTimeout())
//                            .setSocketTimeout(configuration.getSocketTimeout());
//                    RetryStrategy retryStrategy = configuration.getRetryStrategy();
//                    if (retryStrategy != null) {
//                        builder.setRetryStrategy(retryStrategy);
//                    }
//                    QCloudHttpRetryHandler qCloudHttpRetryHandler = configuration.getQCloudHttpRetryHandler();
//                    if(qCloudHttpRetryHandler != null){
//                        builder.setQCloudHttpRetryHandler(qCloudHttpRetryHandler);
//                    }
//                    builder.enableDebugLog(configuration.isDebuggable());
//                    if(configuration.isEnableQuic()){
//                        try {
//                            Class clazz = Class.forName("com.tencent.qcloud.quic.QuicClientImpl");
//                            builder.setNetworkClient((NetworkClient) clazz.newInstance());
//                        } catch (Exception e) {
//                            throw new IllegalStateException(e.getMessage(), e);
//                        }
//                    }else {
//                        builder.setNetworkClient(new OkHttpClientImpl());
//                    }
                    QCloudHttpClient.Builder builder = new QCloudHttpClient.Builder();
                    init(builder, configuration);
                    client = builder.build();
                }
            }
        }
        config = configuration;
        client.addVerifiedHost("*." + configuration.getEndpointSuffix());
        client.addVerifiedHost("*." + configuration.getEndpointSuffix(
                configuration.getRegion(), true));
        client.setDebuggable(configuration.isDebuggable());
        if (configuration.isTrustAllHost()) {
        }

        ContextHolder.setContext(context);
    }

    /**
     * cos android SDK 服务
     *
     * @param context       Application 上下文{@link android.app.Application}
     * @param configuration cos android SDK 服务配置 {@link CosXmlServiceConfig}
     * @param qCloudSigner  cos android SDK 签名提供者 {@link QCloudSigner}
     */
    public CosXmlSimpleService(Context context, CosXmlServiceConfig configuration,
                               QCloudSigner qCloudSigner) {
        this(context, configuration);
        credentialProvider = new StaticCredentialProvider(null);
        signerType = "UserCosXmlSigner";
        SignerFactory.registerSigner(signerType, qCloudSigner);
    }

    private void init(QCloudHttpClient.Builder builder, CosXmlServiceConfig configuration){
        builder.setConnectionTimeout(configuration.getConnectionTimeout())
                .setSocketTimeout(configuration.getSocketTimeout());
        RetryStrategy retryStrategy = configuration.getRetryStrategy();
        if (retryStrategy != null) {
            builder.setRetryStrategy(retryStrategy);
        }
        QCloudHttpRetryHandler qCloudHttpRetryHandler = configuration.getQCloudHttpRetryHandler();
        if(qCloudHttpRetryHandler != null){
            builder.setQCloudHttpRetryHandler(qCloudHttpRetryHandler);
        }
        builder.enableDebugLog(configuration.isDebuggable());
        if(configuration.isEnableQuic()){
            try {
                Class clazz = Class.forName("com.tencent.qcloud.quic.QuicClientImpl");
                builder.setNetworkClient((NetworkClient) clazz.newInstance());
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }else {
            builder.setNetworkClient(new OkHttpClientImpl());
        }
        builder.addPrefetchHost(configuration.getEndpointSuffix());
    }

    public void setNetworkClient(CosXmlServiceConfig configuration){
        synchronized (CosXmlSimpleService.class){
            QCloudHttpClient.Builder builder = new QCloudHttpClient.Builder();
            init(builder, configuration);
            client.setNetworkClientType(builder);
        }
        config = configuration;
        client.addVerifiedHost("*." + configuration.getEndpointSuffix());
        client.addVerifiedHost("*." + configuration.getEndpointSuffix(
                configuration.getRegion(), true));
        client.setDebuggable(configuration.isDebuggable());
    }

    public void addCustomerDNS(String domainName, String[] ipList) throws CosXmlClientException {
        try {
            client.addDnsRecord(domainName, ipList);
        } catch (UnknownHostException e) {
            throw new CosXmlClientException(ClientErrorCode.POOR_NETWORK.getCode(), e);
        }
    }

    public void addVerifiedHost(String hostName) {
        client.addVerifiedHost(hostName);
    }

    /**
     * 设置除 get service 请求外其他所有请求的域名
     *
     * @param domain
     */
    public void setDomain(String domain) {

        this.requestDomain = domain;
    }

    /**
     * 设置 get service 请求的域名
     *
     * @param domain
     */
    public void setServiceDomain(String domain) {

        this.getServiceRequestDomain = domain;
    }

    private String getRequestHost(CosXmlRequest request, boolean accelerate, boolean isHeader) throws CosXmlClientException {

        if (request instanceof GetServiceRequest ) {
            if (!TextUtils.isEmpty(getServiceRequestDomain)) {
                return getServiceRequestDomain;
            }
        } else if (!TextUtils.isEmpty(requestDomain)) {
            return requestDomain;
        }

        return request.getHost(config, accelerate, isHeader);
    }

    /**
     * 构建请求
     */
    protected <T1 extends CosXmlRequest, T2 extends CosXmlResult> QCloudHttpRequest buildHttpRequest
    (T1 cosXmlRequest, T2 cosXmlResult) throws CosXmlClientException {

        QCloudHttpRequest.Builder<T2> httpRequestBuilder = new QCloudHttpRequest.Builder<T2>()
                .method(cosXmlRequest.getMethod())
                .userAgent(config.getUserAgent())
                .tag(tag);

        httpRequestBuilder.addHeaders(config.getCommonHeaders());
        httpRequestBuilder.addNoSignHeaderKeys(config.getNoSignHeaders());
        //add url
        String requestURL = cosXmlRequest.getRequestURL();
        if (requestURL != null) {
            try {
                httpRequestBuilder.url(new URL(requestURL));
                String hostHeader = getRequestHost(cosXmlRequest, cosXmlRequest.isSupportAccelerate(), true);
                httpRequestBuilder.addHeader(HttpConstants.Header.HOST, hostHeader);
            } catch (MalformedURLException e) {
                throw new CosXmlClientException(ClientErrorCode.BAD_REQUEST.getCode(), e);
            }
        } else {
            cosXmlRequest.checkParameters();
            String host = getRequestHost(cosXmlRequest, cosXmlRequest.isSupportAccelerate(), false);
            String hostHeader = getRequestHost(cosXmlRequest, cosXmlRequest.isSupportAccelerate(), true);
            httpRequestBuilder.scheme(config.getProtocol())
                    .host(host)
                    .path(cosXmlRequest.getPath(config))
                    .addHeader(HttpConstants.Header.HOST, hostHeader);
            if(config.getPort() != -1)httpRequestBuilder.port(config.getPort());
            httpRequestBuilder.query(cosXmlRequest.getQueryString());
        }


        if (cosXmlRequest instanceof CopyObjectRequest) {
            ((CopyObjectRequest) cosXmlRequest).setCopySource(((CopyObjectRequest) cosXmlRequest).getCopySource(), config);
        }

        //add headers
        httpRequestBuilder.addHeaders(cosXmlRequest.getRequestHeaders());
        if (cosXmlRequest.isNeedMD5()) {
            httpRequestBuilder.contentMD5();
        }


        // add sign
        if (credentialProvider == null) {
            httpRequestBuilder.signer(null, null);
        } else {
            httpRequestBuilder.signer(signerType, cosXmlRequest.getSignSourceProvider());
        }
        // add credential scope
        httpRequestBuilder.credentialScope(cosXmlRequest.getSTSCredentialScope(config));

        if (cosXmlRequest.getRequestBody() != null) {
            httpRequestBuilder.body(cosXmlRequest.getRequestBody());
        }

        if (cosXmlRequest instanceof GetObjectRequest) {
            String absolutePath = ((GetObjectRequest) cosXmlRequest).getDownloadPath();
            httpRequestBuilder.converter(new ResponseFileBodySerializer<T2>((GetObjectResult) cosXmlResult, absolutePath, ((GetObjectRequest) cosXmlRequest).getFileOffset()));
        } else if (cosXmlRequest instanceof GetObjectBytesRequest) {
            httpRequestBuilder.converter(new ResponseBytesConverter<T2>((GetObjectBytesResult) cosXmlResult));
        } else if (buildHttpRequestBodyConverter(cosXmlRequest, cosXmlResult, httpRequestBuilder)) {

        } else {
            httpRequestBuilder.converter(new ResponseXmlS3BodySerializer<T2>(cosXmlResult));
        }
        QCloudHttpRequest httpRequest = httpRequestBuilder.build();
        return httpRequest;
    }

    protected <T1 extends CosXmlRequest, T2 extends CosXmlResult> boolean buildHttpRequestBodyConverter(T1 cosXmlRequest, T2 cosXmlResult, QCloudHttpRequest.Builder<T2> httpRequestBuilder) {
        return false;
    }

    /**
     * 同步执行
     */
    protected <T1 extends CosXmlRequest, T2 extends CosXmlResult> T2 execute(T1 cosXmlRequest, T2 cosXmlResult)
            throws CosXmlClientException, CosXmlServiceException {
        try {
            QCloudHttpRequest<T2> httpRequest = buildHttpRequest(cosXmlRequest, cosXmlResult);
            HttpTask<T2> httpTask;

            httpTask = client.resolveRequest(httpRequest, credentialProvider);

            cosXmlRequest.setTask(httpTask);

            if (cosXmlRequest instanceof AppendObjectRequest) {
                httpTask.addProgressListener(((AppendObjectRequest) cosXmlRequest).getProgressListener());
            } else if (cosXmlRequest instanceof PutObjectRequest) {
                httpTask.addProgressListener(((PutObjectRequest) cosXmlRequest).getProgressListener());
            } else if (cosXmlRequest instanceof UploadPartRequest) {
                httpTask.addProgressListener(((UploadPartRequest) cosXmlRequest).getProgressListener());
                // httpTask.setWeight(cosXmlRequest.getWeight());
            } else if (cosXmlRequest instanceof GetObjectRequest) {
                httpTask.addProgressListener(((GetObjectRequest) cosXmlRequest).getProgressListener());
            } else if (cosXmlRequest instanceof PostObjectRequest) {
                httpTask.addProgressListener(((PostObjectRequest) cosXmlRequest).getProgressListener());
            }

            HttpResult<T2> httpResult = httpTask.executeNow();
            MTAProxy.getInstance().reportSendAction(cosXmlRequest.getClass().getSimpleName());
            return httpResult != null ? httpResult.content() : null;
        } catch (QCloudServiceException e) {
            throw MTAProxy.getInstance().reportXmlServerException(cosXmlRequest, e);
        } catch (QCloudClientException e) {
            throw MTAProxy.getInstance().reportXmlClientException(cosXmlRequest, e);
        }
    }

    /**
     * 异步执行
     */
    protected <T1 extends CosXmlRequest, T2 extends CosXmlResult> void schedule(final T1 cosXmlRequest, T2 cosXmlResult,
                                                                                final CosXmlResultListener cosXmlResultListener) {

        QCloudResultListener<HttpResult<T2>> qCloudResultListener = new QCloudResultListener<HttpResult<T2>>() {
            @Override
            public void onSuccess(HttpResult<T2> result) {
                cosXmlResultListener.onSuccess(cosXmlRequest, result.content());
            }

            @Override
            public void onFailure(QCloudClientException clientException, QCloudServiceException serviceException) {
                if (clientException != null) {
                    CosXmlClientException xmlClientException = MTAProxy.getInstance().reportXmlClientException(cosXmlRequest, clientException);
                    cosXmlResultListener.onFail(cosXmlRequest, xmlClientException,null);
                } else if (serviceException != null) {
                    CosXmlServiceException xmlServiceException = MTAProxy.getInstance().reportXmlServerException(cosXmlRequest, serviceException);
                    cosXmlResultListener.onFail(cosXmlRequest, null, xmlServiceException);
                } else {
                    cosXmlResultListener.onFail(cosXmlRequest, new CosXmlClientException(ClientErrorCode.UNKNOWN.getCode(), "Unknown Error"), null);
                }
            }
        };

        try {
            QCloudHttpRequest<T2> httpRequest = buildHttpRequest(cosXmlRequest, cosXmlResult);

            HttpTask<T2> httpTask;
            if (cosXmlRequest instanceof PostObjectRequest) {
                httpTask = client.resolveRequest(httpRequest, null);
            } else {
                httpTask = client.resolveRequest(httpRequest, credentialProvider);
            }

            cosXmlRequest.setTask(httpTask);

            if (cosXmlRequest instanceof AppendObjectRequest) {
                httpTask.addProgressListener(((AppendObjectRequest) cosXmlRequest).getProgressListener());
            } else if (cosXmlRequest instanceof PutObjectRequest) {
                httpTask.addProgressListener(((PutObjectRequest) cosXmlRequest).getProgressListener());
            } else if (cosXmlRequest instanceof UploadPartRequest) {
                httpTask.addProgressListener(((UploadPartRequest) cosXmlRequest).getProgressListener());
                httpTask.setOnRequestWeightListener(new QCloudTask.OnRequestWeightListener() {
                    @Override
                    public int onWeight() {
                        return cosXmlRequest.getWeight();
                    }
                });
            } else if (cosXmlRequest instanceof GetObjectRequest) {
                httpTask.addProgressListener(((GetObjectRequest) cosXmlRequest).getProgressListener());
            } else if (cosXmlRequest instanceof PostObjectRequest) {
                httpTask.addProgressListener(((PostObjectRequest) cosXmlRequest).getProgressListener());
            }

            Executor executor = config.getExecutor();
            if(executor != null){
                httpTask.scheduleOn(executor);
            }else if(cosXmlRequest instanceof BaseMultipartUploadRequest){
                httpTask.scheduleOn(TaskExecutors.UPLOAD_EXECUTOR, cosXmlRequest.getPriority());
            }
            else {
                httpTask.schedule();
            }
            httpTask.addResultListener(qCloudResultListener);
            MTAProxy.getInstance().reportSendAction(cosXmlRequest.getClass().getSimpleName());
        } catch (QCloudClientException e) {
            CosXmlClientException clientException = MTAProxy.getInstance().reportXmlClientException(cosXmlRequest, e);
            cosXmlResultListener.onFail(cosXmlRequest, clientException,null);
        }
    }

    /**
     * 获取请求的访问地址
     *
     * @param cosXmlRequest
     * @return String
     */
    public String getAccessUrl(CosXmlRequest cosXmlRequest) {
        String requestURL = cosXmlRequest.getRequestURL();
        if (requestURL != null) {
            int index = requestURL.indexOf("?");
            return index > 0 ? requestURL.substring(0, index) : requestURL;
        }
        String host = null;
        try {
            host = cosXmlRequest.getHost(config, false);
            host = getRequestHost(cosXmlRequest, false, false);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        }
        String path = "/";
        try {
            path = URLEncodeUtils.cosPathEncode(cosXmlRequest.getPath(config));
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        }
        return config.getProtocol() + "://" + host + path;
    }

    /**
     * 获取预签名文件URL
     *
     * @param cosXmlRequest
     * @return String
     * @throws CosXmlClientException cos client exception
     */
    public String getPresignedURL(CosXmlRequest cosXmlRequest) throws CosXmlClientException {
        try {
            //step1: obtain sign, contain token if it exist.
            QCloudLifecycleCredentials qCloudLifecycleCredentials = (QCloudLifecycleCredentials) credentialProvider.getCredentials();
            QCloudSigner signer = SignerFactory.getSigner(signerType);
            QCloudHttpRequest request = buildHttpRequest(cosXmlRequest, null);
            signer.sign(request, qCloudLifecycleCredentials);
            String sign = request.header(HttpConstants.Header.AUTHORIZATION);
            String token = request.header("x-cos-security-token");
            if(!TextUtils.isEmpty(token)){
                sign = sign + "&x-cos-security-token=" + token;
            }
            // step2: obtain host and path
            String url = getAccessUrl(cosXmlRequest);

            //step3: return url + ? + sign
            return url + "?" + sign;
        } catch (QCloudClientException e) {
            throw new CosXmlClientException(ClientErrorCode.INVALID_CREDENTIALS.getCode(), e);
        }
    }

    /**
     * <p>
     * 初始化分块上传的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#initMultipartUpload(InitMultipartUploadRequest request)}
     * </p>
     */
    @Override
    public InitMultipartUploadResult initMultipartUpload(InitMultipartUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new InitMultipartUploadResult());
    }

    /**
     * <p>
     * 初始化分块上传的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#initMultipartUploadAsync(InitMultipartUploadRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void initMultipartUploadAsync(InitMultipartUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new InitMultipartUploadResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 查询特定分块上传中的已上传的块的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#listParts(ListPartsRequest request)}
     * </p>
     */
    @Override
    public ListPartsResult listParts(ListPartsRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new ListPartsResult());
    }

    /**
     * <p>
     * 查询特定分块上传中的已上传的块的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#listPartsAsync(ListPartsRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void listPartsAsync(ListPartsRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new ListPartsResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 上传一个对象某个分片块的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#uploadPart(UploadPartRequest request)}
     * </p>
     */
    @Override
    public UploadPartResult uploadPart(UploadPartRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new UploadPartResult());
    }

    /**
     * <p>
     * 上传一个对象某个分片块的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#uploadPartAsync(UploadPartRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void uploadPartAsync(UploadPartRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new UploadPartResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 舍弃一个分块上传且删除已上传的分片块的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#abortMultiUpload(AbortMultiUploadRequest request)}
     * </p>
     */
    @Override
    public AbortMultiUploadResult abortMultiUpload(AbortMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new AbortMultiUploadResult());
    }

    /**
     * <p>
     * 舍弃一个分块上传且删除已上传的分片块的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#abortMultiUploadAsync(AbortMultiUploadRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void abortMultiUploadAsync(AbortMultiUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new AbortMultiUploadResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 完成整个分块上传的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#completeMultiUpload(CompleteMultiUploadRequest request)}
     * </p>
     */
    @Override
    public CompleteMultiUploadResult completeMultiUpload(CompleteMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        CompleteMultiUploadResult completeMultiUploadResult = new CompleteMultiUploadResult();
        completeMultiUploadResult.accessUrl = getAccessUrl(request);
        return execute(request, completeMultiUploadResult);
    }

    /**
     * <p>
     * 完成整个分块上传的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#completeMultiUploadAsync(CompleteMultiUploadRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void completeMultiUploadAsync(CompleteMultiUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        CompleteMultiUploadResult completeMultiUploadResult = new CompleteMultiUploadResult();
        completeMultiUploadResult.accessUrl = getAccessUrl(request);
        schedule(request, completeMultiUploadResult, cosXmlResultListener);
    }

    /**
     * <p>
     * 删除 COS 上单个对象的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#deleteObject(DeleteObjectRequest request)}
     * </p>
     */
    @Override
    public DeleteObjectResult deleteObject(DeleteObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteObjectResult());
    }

    /**
     * <p>
     * 删除 COS 上单个对象的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#deleteObjectAsync(DeleteObjectRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void deleteObjectAsync(DeleteObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteObjectResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 获取 COS 对象的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#getObject(GetObjectRequest request)}
     * </p>
     */
    @Override
    public GetObjectResult getObject(GetObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetObjectResult());
    }

    /**
     * <p>
     * 获取 COS 对象的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#getObjectAsync(GetObjectRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void getObjectAsync(GetObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetObjectResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 简单上传的同步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#putObject(PutObjectRequest request)}
     * </p>
     */
    @Override
    public PutObjectResult putObject(PutObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        PutObjectResult putObjectResult = new PutObjectResult();
        putObjectResult.accessUrl = getAccessUrl(request);
        return execute(request, putObjectResult);
    }

    /**
     * <p>
     * 简单上传的异步方法.&nbsp;
     * <p>
     * 详细介绍，请查看:{@link  SimpleCosXml#putObjectAsync(PutObjectRequest request, CosXmlResultListener cosXmlResultListener)}
     * </p>
     */
    @Override
    public void putObjectAsync(PutObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        PutObjectResult putObjectResult = new PutObjectResult();
        putObjectResult.accessUrl = getAccessUrl(request);
        schedule(request, putObjectResult, cosXmlResultListener);
    }

    @Override
    public PostObjectResult postObject(PostObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        PostObjectResult postObjectResult = new PostObjectResult();
        return execute(request, postObjectResult);
    }

    @Override
    public void postObjectAsync(PostObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        PostObjectResult postObjectResult = new PostObjectResult();
        schedule(request, postObjectResult, cosXmlResultListener);
    }

    @Override
    public byte[] getObject(String bucketName, String objectName) throws CosXmlClientException, CosXmlServiceException {

        GetObjectBytesRequest getObjectBytesRequest = new GetObjectBytesRequest(bucketName, objectName);
        GetObjectBytesResult getObjectBytesResult = execute(getObjectBytesRequest, new GetObjectBytesResult());
        return getObjectBytesResult != null ? getObjectBytesResult.data : new byte[0];
    }

    @Override
    public HeadObjectResult headObject(HeadObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new HeadObjectResult());
    }

    @Override
    public void headObjectAsync(HeadObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new HeadObjectResult(), cosXmlResultListener);
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new CopyObjectResult());
    }

    @Override
    public void copyObjectAsync(CopyObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new CopyObjectResult(), cosXmlResultListener);
    }

    @Override
    public UploadPartCopyResult copyObject(UploadPartCopyRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new UploadPartCopyResult());
    }

    @Override
    public void copyObjectAsync(UploadPartCopyRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new UploadPartCopyResult(), cosXmlResultListener);
    }

    /**
     * 取消请求任务.&nbsp;
     * 详细介绍，请查看:{@link  SimpleCosXml#cancel(CosXmlRequest)}
     */
    @Override
    public void cancel(CosXmlRequest cosXmlRequest) {
        if (cosXmlRequest != null && cosXmlRequest.getHttpTask() != null) {
            cosXmlRequest.getHttpTask().cancel();
        }
    }

    /**
     * 取消所有的请求任务.&nbsp;
     * 详细介绍，请查看:{@link  SimpleCosXml#cancelAll()}
     */
    @Override
    public void cancelAll() {
        List<HttpTask> tasks = client.getTasksByTag(tag);
        for (HttpTask task : tasks) {
            task.cancel();
        }
    }

    /**
     * 释放所有的请求.&nbsp;
     * 详细介绍，请查看:{@link  SimpleCosXml#release()}
     */
    @Override
    public void release() {
        cancelAll();
    }

    public String getAppid() {
        return config.getAppid();
    }

    /**
     * @return
     * @see #getRegion(CosXmlRequest)
     */
    @Deprecated
    public String getRegion() {
        return config.getRegion();
    }

    public String getRegion(CosXmlRequest cosXmlRequest) {
        return cosXmlRequest.getRegion() == null ? config.getRegion() : cosXmlRequest.getRegion();
    }

    public CosXmlServiceConfig getConfig() {
        return config;
    }


    /**
     * 添加 日志信息
     */
    public File[] getLogFiles(int limit) {
        LogServerProxy logServerProxy = LogServerProxy.getInstance();
        if (logServerProxy != null) {
            FileLogAdapter fileLogAdapter = logServerProxy.getFileLogAdapter();
            if (fileLogAdapter != null) {
                return fileLogAdapter.getLogFilesDesc(limit);
            }
        }
        return null;
    }


}

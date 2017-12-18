package com.tencent.cos.xml;

import android.content.Context;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketReplicationRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketReplicationResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketTaggingResult;
import com.tencent.cos.xml.model.bucket.GetBucketACLRequest;
import com.tencent.cos.xml.model.bucket.GetBucketACLResult;
import com.tencent.cos.xml.model.bucket.GetBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.GetBucketCORSResult;
import com.tencent.cos.xml.model.bucket.GetBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.GetBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.GetBucketLocationRequest;
import com.tencent.cos.xml.model.bucket.GetBucketLocationResult;
import com.tencent.cos.xml.model.bucket.GetBucketReplicationRequest;
import com.tencent.cos.xml.model.bucket.GetBucketReplicationResult;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.bucket.GetBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.GetBucketTaggingResult;
import com.tencent.cos.xml.model.bucket.GetBucketVersioningRequest;
import com.tencent.cos.xml.model.bucket.GetBucketVersioningResult;
import com.tencent.cos.xml.model.bucket.HeadBucketRequest;
import com.tencent.cos.xml.model.bucket.HeadBucketResult;
import com.tencent.cos.xml.model.bucket.ListMultiUploadsRequest;
import com.tencent.cos.xml.model.bucket.ListMultiUploadsResult;
import com.tencent.cos.xml.model.bucket.PutBucketACLRequest;
import com.tencent.cos.xml.model.bucket.PutBucketACLResult;
import com.tencent.cos.xml.model.bucket.PutBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.PutBucketCORSResult;
import com.tencent.cos.xml.model.bucket.PutBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.PutBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.PutBucketReplicationRequest;
import com.tencent.cos.xml.model.bucket.PutBucketReplicationResult;
import com.tencent.cos.xml.model.bucket.PutBucketRequest;
import com.tencent.cos.xml.model.bucket.PutBucketResult;
import com.tencent.cos.xml.model.bucket.PutBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.PutBucketTaggingResult;
import com.tencent.cos.xml.model.bucket.PutBucketVersioningRequest;
import com.tencent.cos.xml.model.bucket.PutBucketVersioningResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.AppendObjectResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.CopyObjectResult;
import com.tencent.cos.xml.model.object.DeleteMultiObjectRequest;
import com.tencent.cos.xml.model.object.DeleteMultiObjectResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.cos.xml.model.object.GetObjectACLRequest;
import com.tencent.cos.xml.model.object.GetObjectACLResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.HeadObjectResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.OptionObjectRequest;
import com.tencent.cos.xml.model.object.OptionObjectResult;
import com.tencent.cos.xml.model.object.PutObjectACLRequest;
import com.tencent.cos.xml.model.object.PutObjectACLResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartCopyRequest;
import com.tencent.cos.xml.model.object.UploadPartCopyResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;
import com.tencent.qcloud.core.network.QCloudRequest;
import com.tencent.qcloud.core.network.QCloudResult;
import com.tencent.qcloud.core.network.QCloudResultListener;
import com.tencent.qcloud.core.network.QCloudService;
import com.tencent.qcloud.core.network.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.network.exception.QCloudClientException;


/**
 *
 * <p>
 * {@link CosXml}接口实现类。
 * </p>
 * <p>
 * 注意：在整个app的生命周期内，CosXmlService最好只初始化一个实例。
 * </p>
 *
 * <p>
 * 对请求签名是COS服务中重要的一步，本SDK给您提供了多种签名方式供您选用，详情请参见{}
 * </p>
 *
 * <br>
 * 示例代码：
 * <pre>
 * // 1、初始化CosXml
 * CosXml cosXml = new CosXmlService(appContext, cosXmlServiceConfig, basicCredentialProvider);
 *
 * // 2、初始化发送请求
 * GetServiceRequest getServiceRequest = new GetServiceRequest();
 * getServiceRequest.setSign(600, null, null);
 *
 * // 3、同步发送请求
 * // 注意：同步发送请求是一个阻塞操作，请不要直接在主线程中执行。
 * GetServiceResult getServiceResult = cosXml.getService(getServiceRequest);
 *
 * // 3、您也可以异步发送请求
 * //cosXmlService.getServiceAsync(null, new CosXmlResultListener() {
 * //  public void onSuccess(CosXmlRequest request, CosXmlResult result) {
 * //
 * //  }
 * //
 * //  public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
 * //
 * //  }
 * //});
 *
 * </pre>
 *
 *
 */

public class CosXmlService implements CosXml {

    private QCloudService mService;
    /**
     * 初始化COS XML 服务实例
     *
     * @param context  应用Context
     * @param serviceConfig  cos xml服务的配置
     * @param basicCredentialProvider cos xml服务的签名，详情参见{@link QCloudCredentialProvider}
     *
     */
    public CosXmlService(Context context, CosXmlServiceConfig serviceConfig, QCloudCredentialProvider basicCredentialProvider) {
        mService = new QCloudService.Builder(context)
                .serviceConfig(serviceConfig)
                .credentialProvider(basicCredentialProvider)
                .build();
        CosXmlServiceConfig.instance = serviceConfig;
    }

    //COS Service API

    /**
     * Synchronous request
     * Get Service API for cos
     * @param request {@link GetServiceRequest}
     * @return GetServiceResult {@link GetServiceResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetServiceResult getService(GetServiceRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetServiceResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetServiceRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getServiceAsync(GetServiceRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    //COS Object API

    /**
     *  Synchronous request
     *  Abort MultiUpload API for cos
     * @param request {@link AbortMultiUploadRequest}
     * @return AbortMultiUploadResult {@link AbortMultiUploadResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public AbortMultiUploadResult abortMultiUpload(AbortMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (AbortMultiUploadResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link AbortMultiUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void abortMultiUploadAsync(AbortMultiUploadRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Append Object for cos
     * Synchronous request
     * @param request {@link AppendObjectRequest}
     * @return AppendObjectResult {@link AppendObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public AppendObjectResult appendObject(AppendObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        AppendObjectResult appendObjectResult = (AppendObjectResult) sendRequest(request);
        generateAccessUrl(request, appendObjectResult);
        return appendObjectResult;
    }

    /**
     * asynchronous request
     * @param request {@link AppendObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void appendObjectAsync(AppendObjectRequest request, final CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Complete MultiUpload for cos
     * synchronous request
     * @param request {@link CompleteMultiUploadRequest}
     * @return CompleteMultiUploadResult {@link CompleteMultiUploadResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public CompleteMultiUploadResult completeMultiUpload(CompleteMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        CompleteMultiUploadResult completeMultiUploadResult = (CompleteMultiUploadResult) sendRequest(request);
        generateAccessUrl(request, completeMultiUploadResult);
        return completeMultiUploadResult;
    }

    /**
     * asynchronous request
     * @param request {@link CompleteMultiUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void completeMultiUploadAsync(CompleteMultiUploadRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Delete Multi objects for cos
     * synchronous request
     * @param request {@link DeleteMultiObjectRequest}
     * @return DeleteMultiObjectResult {@link DeleteMultiObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public DeleteMultiObjectResult deleteMultiObject(DeleteMultiObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (DeleteMultiObjectResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link DeleteMultiObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void deleteMultiObjectAsync(DeleteMultiObjectRequest request, final CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Delete object for cos
     * synchronous request
     * @param request {@link DeleteObjectRequest}
     * @return DeleteObjectResult {@link DeleteObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public DeleteObjectResult deleteObject(DeleteObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (DeleteObjectResult) sendRequest(request);
    }


    /**
     * asynchronous request
     * @param request {@link DeleteObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void deleteObjectAsync(DeleteObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * get object ACL for cos
     * synchronous request
     * @param request {@link GetObjectACLRequest}
     * @return GetObjectACLResult {@link GetObjectACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetObjectACLResult getObjectACL(GetObjectACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetObjectACLResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetObjectACLRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getObjectACLAsync(GetObjectACLRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Get Object for cos
     * synchronous request
     * @param request {@link GetObjectRequest}
     * @return GetObjectResult {@link GetObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetObjectResult getObject(GetObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetObjectResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getObjectAsync(GetObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Head Object for cos
     * synchronous request
     * @param request {@link HeadObjectRequest}
     * @return HeadObjectResult {@link HeadObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public HeadObjectResult headObject(HeadObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (HeadObjectResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link HeadObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void headObjectAsync(HeadObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Init multipart upload for cos
     * synchronous request
     * @param request {@link InitMultipartUploadRequest}
     * @return InitMultipartUploadResult {@link InitMultipartUploadResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public InitMultipartUploadResult initMultipartUpload(InitMultipartUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (InitMultipartUploadResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link InitMultipartUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void initMultipartUploadAsync(InitMultipartUploadRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * List parts for cos
     * synchronous request
     * @param request {@link ListPartsRequest}
     * @return ListPartsResult {@link ListPartsResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public ListPartsResult listParts(ListPartsRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (ListPartsResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link ListPartsRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void listPartsAsync(ListPartsRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Option Object for cos
     * synchronous request
     * @param request {@link OptionObjectRequest}
     * @return OptionObjectResult {@link OptionObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public OptionObjectResult optionObject(OptionObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (OptionObjectResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link OptionObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void optionObjectAsync(OptionObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Put object ACL for cos
     * synchronous request
     * @param request {@link PutObjectACLRequest}
     * @return PutObjectACLResult {@link PutObjectACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public PutObjectACLResult putObjectACL(PutObjectACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutObjectACLResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutObjectACLRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void putObjectACLAsync(PutObjectACLRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Put objcet for cos
     * synchronous request
     * @param request {@link PutObjectRequest}
     * @return PutObjectResult {@link PutObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public PutObjectResult putObject(PutObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        PutObjectResult putObjectResult = (PutObjectResult) sendRequest(request);
        generateAccessUrl(request, putObjectResult);
        return putObjectResult;
    }

    /**
     * asynchronous request
     * @param request {@link PutObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void putObjectAsync(PutObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Copy Object for cos
     * synchronous request
     * @param request, {@link CopyObjectRequest}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    @Override
    public CopyObjectResult copyObject(CopyObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (CopyObjectResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link CopyObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    @Override
    public void copyObjectAsync(CopyObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Upload Part Copy Object for cos
     * synchronous request
     * @param request, {@link UploadPartCopyRequest}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    @Override
    public UploadPartCopyResult copyObject(UploadPartCopyRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (UploadPartCopyResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link CopyObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    @Override
    public void copyObjectAsync(UploadPartCopyRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Upload parts for cos
     * synchronous request
     * @param request {@link UploadPartRequest}
     * @return UploadPartResult {@link UploadPartResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public UploadPartResult uploadPart(UploadPartRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (UploadPartResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link UploadPartRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void uploadPartAsync(UploadPartRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    //COS Bucket API

    /**
     * Delete Bucket for cos
     * synchronous request
     * @param request {@link DeleteBucketCORSRequest}
     * @return DeleteBucketCORSResult {@link DeleteBucketCORSResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public DeleteBucketCORSResult deleteBucketCORS(DeleteBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (DeleteBucketCORSResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link DeleteBucketCORSRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void deleteBucketCORSAsync(DeleteBucketCORSRequest request, final CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Delete Bucket Lifecycle for cos
     * synchronous request
     * @param request {@link DeleteBucketLifecycleRequest}
     * @return DeleteBucketLifecycleResult {@link DeleteBucketLifecycleResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public DeleteBucketLifecycleResult deleteBucketLifecycle(DeleteBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (DeleteBucketLifecycleResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link DeleteBucketLifecycleRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void deleteBucketLifecycleAsync(DeleteBucketLifecycleRequest request,CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Delete Bucket for cos
     * synchronous request
     * @param request {@link DeleteBucketRequest}
     * @return DeleteBucketResult {@link DeleteBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public DeleteBucketResult deleteBucket(DeleteBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (DeleteBucketResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link DeleteBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void deleteBucketAsync(DeleteBucketRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Delete bucket Tag for cos
     * synchronous request
     * @param request {@link DeleteBucketTaggingRequest}
     * @return DeleteBucketTaggingResult {@link DeleteBucketTaggingResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public DeleteBucketTaggingResult deleteBucketTagging(DeleteBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (DeleteBucketTaggingResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link DeleteBucketTaggingRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void deleteBucketTaggingAsync(DeleteBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Get bucket ACL for cos
     * synchronous request
     * @param request {@link GetBucketACLRequest}
     * @return GetBucketACLResult {@link GetBucketACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetBucketACLResult getBucketACL(GetBucketACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketACLResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getBucketACLAsync(GetBucketACLRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Get bucket cors for cos
     * synchronous request
     * @param request {@link GetBucketCORSRequest}
     * @return GetBucketCORSResult {@link GetBucketCORSResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetBucketCORSResult getBucketCORS(GetBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketCORSResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketCORSRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getBucketCORSAsync(GetBucketCORSRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Get bucket lifecycle for cos
     * synchronous request
     * @param request {@link GetBucketLifecycleRequest}
     * @return GetBucketLifecycleResult {@link GetBucketLifecycleResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetBucketLifecycleResult getBucketLifecycle(GetBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketLifecycleResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketLifecycleRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getBucketLifecycleAsync(GetBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Get Bucket Location for cos
     * synchronous request
     * @param request {@link GetBucketLocationRequest}
     * @return GetBucketLocationResult {@link GetBucketLocationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetBucketLocationResult getBucketLocation(GetBucketLocationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketLocationResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketLocationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getBucketLocationAsync(GetBucketLocationRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Get bucket for cos
     * synchronous request
     * @param request {@link GetBucketRequest}
     * @return GetBucketResult {@link GetBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetBucketResult getBucket(GetBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getBucketAsync(GetBucketRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * get bucket tag for cos
     * synchronous request
     * @param request {@link GetBucketTaggingRequest}
     * @return GetBucketTaggingResult {@link GetBucketTaggingResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public GetBucketTaggingResult getBucketTagging(GetBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketTaggingResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketTaggingRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void getBucketTaggingAsync(GetBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Head bucket for cos
     * synchronous request
     * @param request {@link HeadBucketRequest}
     * @return HeadBucketResult {@link HeadBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public HeadBucketResult headBucket(HeadBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (HeadBucketResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link HeadBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void headBucketAsync(HeadBucketRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * List MultiUploads for cos
     * synchronous request
     * @param request {@link ListMultiUploadsRequest}
     * @return ListMultiUploadsResult {@link ListMultiUploadsResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public ListMultiUploadsResult listMultiUploads(ListMultiUploadsRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (ListMultiUploadsResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link ListMultiUploadsRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void listMultiUploadsAsync(ListMultiUploadsRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Put Bucket ACL for cos
     * synchronous request
     * @param request {@link PutBucketACLRequest}
     * @return PutBucketACLResult {@link PutBucketACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public PutBucketACLResult putBucketACL(PutBucketACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutBucketACLResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutBucketACLRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void putBucketACLAsync(PutBucketACLRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Put Bucket cors for cos
     * synchronous request
     * @param request {@link PutBucketCORSRequest}
     * @return PutBucketCORSResult {@link PutBucketCORSResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public PutBucketCORSResult putBucketCORS(PutBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutBucketCORSResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutBucketCORSRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void putBucketCORSAsync(PutBucketCORSRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Put bucket LifeCycle
     * synchronous request
     * @param request {@link PutBucketLifecycleRequest}
     * @return PutBucketLifecycleResult {@link PutBucketLifecycleResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public PutBucketLifecycleResult putBucketLifecycle(PutBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutBucketLifecycleResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutBucketLifecycleRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void putBucketLifecycleAsync(PutBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Put bucket for cos
     * synchronous request
     * @param request {@link PutBucketRequest}
     * @return PutBucketResult {@link PutBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public PutBucketResult putBucket(PutBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutBucketResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void putBucketAsync(PutBucketRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * Put Bucket Tag for cos
     * synchronous request
     * @param request {@link PutBucketTaggingRequest}
     * @return PutBucketTaggingResult {@link PutBucketTaggingResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    public PutBucketTaggingResult putBucketTagging(PutBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutBucketTaggingResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutBucketTaggingRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    public void putBucketTaggingAsync(PutBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener){
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * get Bucket Versioning for cos
     * synchronous request
     * @param request {@link GetBucketVersioningRequest}
     * @return GetBucketVersioningResult {@link GetBucketVersioningResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    @Override
    public GetBucketVersioningResult getBucketVersioning(GetBucketVersioningRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketVersioningResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketVersioningRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    @Override
    public void getBucketVersioningAsync(GetBucketVersioningRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * put Bucket Versioning for cos
     * synchronous request
     * @param request {@link PutBucketVersioningRequest}
     * @return PutBucketVersioningResult {@link PutBucketVersioningResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    @Override
    public PutBucketVersioningResult putBucketVersioning(PutBucketVersioningRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutBucketVersioningResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutBucketVersioningRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    @Override
    public void putBucketVersionAsync(PutBucketVersioningRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * get Bucket Replication for cos
     * synchronous request
     * @param request {@link GetBucketReplicationRequest}
     * @return GetBucketReplicationResult {@link GetBucketReplicationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    @Override
    public GetBucketReplicationResult getBucketReplication(GetBucketReplicationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (GetBucketReplicationResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link GetBucketReplicationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    @Override
    public void getBucketReplicationAsync(GetBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * put Bucket Replication for cos
     * synchronous request
     * @param request {@link PutBucketReplicationRequest}
     * @return PutBucketReplicationResult {@link PutBucketReplicationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    @Override
    public PutBucketReplicationResult putBucketReplication(PutBucketReplicationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (PutBucketReplicationResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link PutBucketReplicationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    @Override
    public void putBucketReplicationAsync(PutBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    /**
     * delete Bucket Replication for cos
     * synchronous request
     * @param request {@link DeleteBucketReplicationRequest}
     * @return DeleteBucketReplicationResult {@link DeleteBucketReplicationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    @Override
    public DeleteBucketReplicationResult deleteBucketReplication(DeleteBucketReplicationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return (DeleteBucketReplicationResult) sendRequest(request);
    }

    /**
     * asynchronous request
     * @param request {@link DeleteBucketReplicationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    @Override
    public void deleteBucketReplicationAsync(DeleteBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener) {
        sendRequestAsync(request, cosXmlResultListener );
    }

    @Override
    public void cancel(CosXmlRequest cosXmlRequest) {
         mService.cancel(cosXmlRequest);
    }


    /**
     * try to cancel all request
     */
    @Override
    public void cancelAll() {
        mService.cancelAll();
    }

    /**
     *  release resource for sdk(such as shutdown thread pools)
     */
    public void release(){
        mService.release();
    }


    private CosXmlResult sendRequest(CosXmlRequest request) throws CosXmlClientException, CosXmlServiceException {
        CosXmlResult cosXmlResult = null;
        try {
            cosXmlResult = (CosXmlResult) mService.execute(request);
        } catch (QCloudClientException e) {
           throw new CosXmlClientException(e);
        }
        isServiceException(cosXmlResult);
        return cosXmlResult;
    }

    private void isServiceException(CosXmlResult cosXmlResult) throws CosXmlServiceException {
        if(cosXmlResult != null){
            int httpCode = cosXmlResult.getHttpCode();
            if(httpCode >=300 || httpCode < 200){
                throw new CosXmlServiceException(httpCode, cosXmlResult.getHttpMessage(), cosXmlResult.error);
            }
        }
    }

    private void sendRequestAsync(final CosXmlRequest cosXmlRequest, final CosXmlResultListener cosXmlResultListener){
        mService.enqueue(cosXmlRequest, new QCloudResultListener(){
            @Override
            public void onSuccess(QCloudRequest request, QCloudResult result) {
                getResultByQCloudResult(cosXmlRequest, (CosXmlResult) result, cosXmlResultListener);
            }

            @Override
            public void onFailed(QCloudRequest request, QCloudClientException clientException, com.tencent.qcloud.core.network.exception.QCloudServiceException serviceException) {
                cosXmlResultListener.onFail(cosXmlRequest, new CosXmlClientException(clientException), null);
            }
        });
    }

    private void getResultByQCloudResult(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult,
                                         CosXmlResultListener cosXmlResultListener) {
        if (cosXmlResult != null && cosXmlResultListener != null) {
            try {
                isServiceException(cosXmlResult);
                if (cosXmlRequest instanceof AppendObjectRequest ||
                        cosXmlRequest instanceof PutObjectRequest
                        || cosXmlRequest instanceof CompleteMultiUploadRequest) {
                    generateAccessUrl(cosXmlRequest, cosXmlResult);
                }
                cosXmlResultListener.onSuccess(cosXmlRequest, cosXmlResult);
            } catch (CosXmlServiceException e) {
                cosXmlResultListener.onFail(cosXmlRequest, null, e);
            }
        }
    }

    private void generateAccessUrl(final CosXmlRequest cosXmlRequest, final CosXmlResult cosXmlResult){
            if(cosXmlRequest == null || cosXmlResult == null){
                return;
            }
            if(cosXmlResult.getHttpCode() >= 200 && cosXmlResult.getHttpCode() < 300){
                StringBuilder accessUrl = new StringBuilder();
                CosXmlServiceConfig qCloudServiceConfig = CosXmlServiceConfig.getInstance();
                if(qCloudServiceConfig != null){
                    accessUrl.append(qCloudServiceConfig.getScheme())
                            .append("://")
                            .append(cosXmlRequest.getBucket())
                            .append(qCloudServiceConfig.getHttpHost())
                            .append(cosXmlRequest.getRequestPath());
                }
                cosXmlResult.accessUrl = accessUrl.toString();
            }
    }

}

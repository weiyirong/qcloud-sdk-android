package com.tencent.cos.xml;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketCORSResult;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleResult;
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
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.bucket.GetBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.GetBucketTaggingResult;
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
import com.tencent.cos.xml.model.bucket.PutBucketRequest;
import com.tencent.cos.xml.model.bucket.PutBucketResult;
import com.tencent.cos.xml.model.bucket.PutBucketTaggingRequest;
import com.tencent.cos.xml.model.bucket.PutBucketTaggingResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.AppendObjectResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
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
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;
import com.tencent.cos.xml.model.tag.COSXMLError;
import com.tencent.cos.xml.sign.CosXmlCredentialProvider;
import com.tencent.qcloud.network.QCloudHttpRequest;
import com.tencent.qcloud.network.QCloudRequest;
import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.QCloudResultListener;
import com.tencent.qcloud.network.QCloudService;
import com.tencent.qcloud.network.QCloudServiceConfig;
import com.tencent.qcloud.network.auth.BasicCredentialProvider;
import com.tencent.qcloud.network.common.QCloudRequestConst;
import com.tencent.qcloud.network.exception.QCloudException;

import java.util.Locale;


/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 *
 * cos-Android-sdk for Users
 *
 */

public class CosXmlService extends QCloudService{
    /**
     * CosXMLService  construction method
     * @param context  application's context
     * @param serviceConfig  some config for sdk, such network(http or https,etc),{@link CosXmlServiceConfig}
     * @param cloudCredentialProvider xml api sign ,you need to implement this. for example,
     * {@link  com.tencent.cos.xml.sign.CosXmlLocalCredentialProvider},{@link CosXmlCredentialProvider}
     */
    @Deprecated
    public CosXmlService(Context context, CosXmlServiceConfig serviceConfig, CosXmlCredentialProvider cloudCredentialProvider) {
        super(context, serviceConfig, cloudCredentialProvider);
    }

    public CosXmlService(Context context, CosXmlServiceConfig serviceConfig, BasicCredentialProvider basicCredentialProvider) {
        super(context, serviceConfig, basicCredentialProvider);
    }

    //COS Service API

    /**
     * Synchronous request
     * Get Service API for cos
     * @param request ,{@link GetServiceRequest}
     * @return GetServiceResult, {@link GetServiceResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetServiceResult getService(GetServiceRequest request) throws QCloudException {
        buildRequest(request);
        return (GetServiceResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetServiceRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getServiceAsync(GetServiceRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetServiceResult(), cosXmlResultListener );
    }

    //COS Object API

    /**
     *  Synchronous request
     *  Abort MultiUpload API for cos
     * @param request, {@link AbortMultiUploadRequest}
     * @return AbortMultiUploadResult, {@link AbortMultiUploadResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public AbortMultiUploadResult abortMultiUpload(AbortMultiUploadRequest request) throws QCloudException {
        buildRequest(request);
        return (AbortMultiUploadResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link AbortMultiUploadRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void abortMultiUploadAsync(AbortMultiUploadRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new AbortMultiUploadResult(), cosXmlResultListener );
    }

    /**
     * Append Object for cos
     * Synchronous request
     * @param request, {@link AppendObjectRequest}
     * @return AppendObjectResult, {@link AppendObjectResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public AppendObjectResult appendObject(AppendObjectRequest request) throws QCloudException {
        buildRequest(request);
        AppendObjectResult appendObjectResult = (AppendObjectResult) requestManager.send(request);
        generateAccessUrl(request, appendObjectResult);
        return appendObjectResult;
    }

    /**
     * asynchronous request
     * @param request, {@link AppendObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void appendObjectAsync(AppendObjectRequest request, final CosXmlResultListener cosXmlResultListener) {
        requestSend(request, new AppendObjectResult(), cosXmlResultListener );
    }

    /**
     * Complete MultiUpload for cos
     * synchronous request
     * @param request, {@link CompleteMultiUploadRequest}
     * @return CompleteMultiUploadResult, {@link CompleteMultiUploadResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public CompleteMultiUploadResult completeMultiUpload(CompleteMultiUploadRequest request) throws QCloudException {
        buildRequest(request);
        CompleteMultiUploadResult completeMultiUploadResult = (CompleteMultiUploadResult) requestManager.send(request);
        generateAccessUrl(request, completeMultiUploadResult);
        return completeMultiUploadResult;
    }

    /**
     * asynchronous request
     * @param request, {@link CompleteMultiUploadRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void completeMultiUploadAsync(CompleteMultiUploadRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new CompleteMultiUploadResult(), cosXmlResultListener );
    }

    /**
     * Delete Multi objects for cos
     * synchronous request
     * @param request, {@link DeleteMultiObjectRequest}
     * @return DeleteMultiObjectResult, {@link DeleteMultiObjectResult}
     * @throws QCloudException, {@link CosXmlResultListener}
     */
    public DeleteMultiObjectResult deleteMultiObject(DeleteMultiObjectRequest request) throws QCloudException {
        buildRequest(request);
        return (DeleteMultiObjectResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link DeleteMultiObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void deleteMultiObjectAsync(DeleteMultiObjectRequest request, final CosXmlResultListener cosXmlResultListener) {
        requestSend(request, new DeleteMultiObjectResult(), cosXmlResultListener );
    }

    /**
     * Delete object for cos
     * synchronous request
     * @param request, {@link DeleteObjectRequest}
     * @return DeleteObjectResult, {@link DeleteObjectResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public DeleteObjectResult deleteObject(DeleteObjectRequest request) throws QCloudException {
        buildRequest(request);
        return (DeleteObjectResult) requestManager.send(request);
    }


    /**
     * asynchronous request
     * @param request, {@link DeleteObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void deleteObjectAsync(DeleteObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new DeleteObjectResult(), cosXmlResultListener );
    }

    /**
     * get object ACL for cos
     * synchronous request
     * @param request, {@link GetObjectACLRequest}
     * @return GetObjectACLResult, {@link GetObjectACLResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetObjectACLResult getObjectACL(GetObjectACLRequest request) throws QCloudException {
        buildRequest(request);
        return (GetObjectACLResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetObjectACLRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getObjectACLAsync(GetObjectACLRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetObjectACLResult(), cosXmlResultListener );
    }

    /**
     * Get Object for cos
     * synchronous request
     * @param request, {@link GetObjectRequest}
     * @return GetObjectResult, {@link GetObjectResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetObjectResult getObject(GetObjectRequest request) throws QCloudException {
        buildRequest(request);
        return (GetObjectResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getObjectAsync(GetObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetObjectResult(), cosXmlResultListener );
    }

    /**
     * Head Object for cos
     * synchronous request
     * @param request, {@link HeadObjectRequest}
     * @return HeadObjectResult， {@link HeadObjectResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public HeadObjectResult headObject(HeadObjectRequest request) throws QCloudException {
        buildRequest(request);
        return (HeadObjectResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link HeadObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void headObjectAsync(HeadObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new HeadObjectResult(), cosXmlResultListener );
    }

    /**
     * Init multipart upload for cos
     * synchronous request
     * @param request, {@link InitMultipartUploadRequest}
     * @return InitMultipartUploadResult, {@link InitMultipartUploadResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public InitMultipartUploadResult initMultipartUpload(InitMultipartUploadRequest request) throws QCloudException {
        buildRequest(request);
        return (InitMultipartUploadResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link InitMultipartUploadRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void initMultipartUploadAsync(InitMultipartUploadRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new InitMultipartUploadResult(), cosXmlResultListener );
    }

    /**
     * List parts for cos
     * synchronous request
     * @param request, {@link ListPartsRequest}
     * @return ListPartsResult, {@link ListPartsResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public ListPartsResult listParts(ListPartsRequest request) throws QCloudException {
        buildRequest(request);
        return (ListPartsResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link ListPartsRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void listPartsAsync(ListPartsRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new ListPartsResult(), cosXmlResultListener );
    }

    /**
     * Option Object for cos
     * synchronous request
     * @param request, {@link OptionObjectRequest}
     * @return OptionObjectResult, {@link OptionObjectResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public OptionObjectResult optionObject(OptionObjectRequest request) throws QCloudException {
        buildRequest(request);
        return (OptionObjectResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link OptionObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void optionObjectAsync(OptionObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new OptionObjectResult(), cosXmlResultListener );
    }

    /**
     * Put object ACL for cos
     * synchronous request
     * @param request, {@link PutObjectACLRequest}
     * @return PutObjectACLResult, {@link PutObjectACLResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public PutObjectACLResult putObjectACL(PutObjectACLRequest request) throws QCloudException {
        buildRequest(request);
        return (PutObjectACLResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link PutObjectACLRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void putObjectACLAsync(PutObjectACLRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new PutObjectACLResult(), cosXmlResultListener );
    }

    /**
     * Put objcet for cos
     * synchronous request
     * @param request, {@link PutObjectRequest}
     * @return PutObjectResult, {@link PutObjectResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public PutObjectResult putObject(PutObjectRequest request) throws QCloudException {
        buildRequest(request);
        PutObjectResult putObjectResult = (PutObjectResult) requestManager.send(request);
        generateAccessUrl(request, putObjectResult);
        return putObjectResult;
    }

    /**
     * asynchronous request
     * @param request, {@link PutObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void putObjectAsync(PutObjectRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new PutObjectResult(), cosXmlResultListener );
    }

    /**
     * Upload parts for cos
     * synchronous request
     * @param request, {@link UploadPartRequest}
     * @return UploadPartResult, {@link UploadPartResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public UploadPartResult uploadPart(UploadPartRequest request) throws QCloudException {
        buildRequest(request);
        return (UploadPartResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link UploadPartRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void uploadPartAsync(UploadPartRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new UploadPartResult(), cosXmlResultListener );
    }

    //COS Bucket API

    /**
     * Delete Bucket for cos
     * synchronous request
     * @param request, {@link DeleteBucketCORSRequest}
     * @return DeleteBucketCORSResult, {@link DeleteBucketCORSResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public DeleteBucketCORSResult deleteBucketCORS(DeleteBucketCORSRequest request) throws QCloudException {
        buildRequest(request);
        return (DeleteBucketCORSResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link DeleteBucketCORSRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void deleteBucketCORSAsync(DeleteBucketCORSRequest request, final CosXmlResultListener cosXmlResultListener){
        requestSend(request, new DeleteBucketCORSResult(), cosXmlResultListener );
    }

    /**
     * Delete Bucket Lifecycle for cos
     * synchronous request
     * @param request, {@link DeleteBucketLifecycleRequest}
     * @return DeleteBucketLifecycleResult, {@link DeleteBucketLifecycleResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public DeleteBucketLifecycleResult deleteBucketLifecycle(DeleteBucketLifecycleRequest request) throws QCloudException {
        buildRequest(request);
        return (DeleteBucketLifecycleResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link DeleteBucketLifecycleRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void deleteBucketLifecycleAsync(DeleteBucketLifecycleRequest request,CosXmlResultListener cosXmlResultListener){
        requestSend(request, new DeleteBucketLifecycleResult(), cosXmlResultListener );
    }

    /**
     * Delete Bucket for cos
     * synchronous request
     * @param request, {@link DeleteBucketRequest}
     * @return DeleteBucketResult, {@link DeleteBucketResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public DeleteBucketResult deleteBucket(DeleteBucketRequest request) throws QCloudException {
        buildRequest(request);
        return (DeleteBucketResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link DeleteBucketRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void deleteBucketAsync(DeleteBucketRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new DeleteBucketResult(), cosXmlResultListener );
    }

    /**
     * Delete bucket Tag for cos
     * synchronous request
     * @param request, {@link DeleteBucketTaggingRequest}
     * @return DeleteBucketTaggingResult, {@link DeleteBucketTaggingResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public DeleteBucketTaggingResult deleteBucketTagging(DeleteBucketTaggingRequest request) throws QCloudException {
        buildRequest(request);
        return (DeleteBucketTaggingResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request,{@link DeleteBucketTaggingRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void deleteBucketTaggingAsync(DeleteBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new DeleteBucketTaggingResult(), cosXmlResultListener );
    }

    /**
     * Get bucket ACL for cos
     * synchronous request
     * @param request, {@link GetBucketACLRequest}
     * @return GetBucketACLResult, {@link GetBucketACLResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetBucketACLResult getBucketACL(GetBucketACLRequest request) throws QCloudException {
        buildRequest(request);
        return (GetBucketACLResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getBucketACLAsync(GetBucketACLRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetBucketACLResult(), cosXmlResultListener );
    }

    /**
     * Get bucket cors for cos
     * synchronous request
     * @param request, {@link GetBucketCORSRequest}
     * @return GetBucketCORSResult, {@link GetBucketCORSResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetBucketCORSResult getBucketCORS(GetBucketCORSRequest request) throws QCloudException {
        buildRequest(request);
        return (GetBucketCORSResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetBucketCORSRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getBucketCORSAsync(GetBucketCORSRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetBucketCORSResult(), cosXmlResultListener );
    }

    /**
     * Get bucket lifecycle for cos
     * synchronous request
     * @param request, {@link GetBucketLifecycleRequest}
     * @return GetBucketLifecycleResult, {@link GetBucketLifecycleResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetBucketLifecycleResult getBucketLifecycle(GetBucketLifecycleRequest request) throws QCloudException {
        buildRequest(request);
        return (GetBucketLifecycleResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetBucketLifecycleRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getBucketLifecycleAsync(GetBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetBucketLifecycleResult(), cosXmlResultListener );
    }

    /**
     * Get Bucket Location for cos
     * synchronous request
     * @param request, {@link GetBucketLocationRequest}
     * @return GetBucketLocationResult, {@link GetBucketLocationResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetBucketLocationResult getBucketLocation(GetBucketLocationRequest request) throws QCloudException {
        buildRequest(request);
        return (GetBucketLocationResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetBucketLocationRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getBucketLocationAsync(GetBucketLocationRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetBucketLocationResult(), cosXmlResultListener );
    }

    /**
     * Get bucket for cos
     * synchronous request
     * @param request, {@link GetBucketRequest}
     * @return GetBucketResult, {@link GetBucketResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetBucketResult getBucket(GetBucketRequest request) throws QCloudException {
        buildRequest(request);
        return (GetBucketResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetBucketRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getBucketAsync(GetBucketRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetBucketResult(), cosXmlResultListener );
    }

    /**
     * get bucket tag for cos
     * synchronous request
     * @param request, {@link GetBucketTaggingRequest}
     * @return GetBucketTaggingResult, {@link GetBucketTaggingResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public GetBucketTaggingResult getBucketTagging(GetBucketTaggingRequest request) throws QCloudException {
        buildRequest(request);
        return (GetBucketTaggingResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link GetBucketTaggingRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void getBucketTaggingAsync(GetBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new GetBucketTaggingResult(), cosXmlResultListener );
    }

    /**
     * Head bucket for cos
     * synchronous request
     * @param request, {@link HeadBucketRequest}
     * @return HeadBucketResult, {@link HeadBucketResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public HeadBucketResult headBucket(HeadBucketRequest request) throws QCloudException {
        buildRequest(request);
        return (HeadBucketResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link HeadBucketRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void headBucketAsync(HeadBucketRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new HeadBucketResult(), cosXmlResultListener );
    }

    /**
     * List MultiUploads for cos
     * synchronous request
     * @param request, {@link ListMultiUploadsRequest}
     * @return ListMultiUploadsResult, {@link ListMultiUploadsResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public ListMultiUploadsResult listMultiUploads(ListMultiUploadsRequest request) throws QCloudException {
        buildRequest(request);
        return (ListMultiUploadsResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link ListMultiUploadsRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void listMultiUploadsAsync(ListMultiUploadsRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new ListMultiUploadsResult(), cosXmlResultListener );
    }

    /**
     * Put Bucket ACL for cos
     * synchronous request
     * @param request, {@link PutBucketACLRequest}
     * @return PutBucketACLResult, {@link PutBucketACLResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public PutBucketACLResult putBucketACL(PutBucketACLRequest request) throws QCloudException {
        buildRequest(request);
        return (PutBucketACLResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link PutBucketACLRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void putBucketACLAsync(PutBucketACLRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new PutBucketACLResult(), cosXmlResultListener );
    }

    /**
     * Put Bucket cors for cos
     * synchronous request
     * @param request, {@link PutBucketCORSRequest}
     * @return PutBucketCORSResult, {@link PutBucketCORSResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public PutBucketCORSResult putBucketCORS(PutBucketCORSRequest request) throws QCloudException {
        buildRequest(request);
        return (PutBucketCORSResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link PutBucketCORSRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void putBucketCORSAsync(PutBucketCORSRequest request, CosXmlResultListener cosXmlResultListener) {
        requestSend(request, new PutBucketCORSResult(), cosXmlResultListener );
    }

    /**
     * Put bucket LifeCycle
     * synchronous request
     * @param request, {@link PutBucketLifecycleRequest}
     * @return PutBucketLifecycleResult, {@link PutBucketLifecycleResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public PutBucketLifecycleResult putBucketLifecycle(PutBucketLifecycleRequest request) throws QCloudException {
        buildRequest(request);
        return (PutBucketLifecycleResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link PutBucketLifecycleRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void putBucketLifecycleAsync(PutBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new PutBucketLifecycleResult(), cosXmlResultListener );
    }

    /**
     * Put bucket for cos
     * synchronous request
     * @param request, {@link PutBucketRequest}
     * @return PutBucketResult, {@link PutBucketResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public PutBucketResult putBucket(PutBucketRequest request) throws QCloudException {
        buildRequest(request);
        return (PutBucketResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link PutBucketRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void putBucketAsync(PutBucketRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new PutBucketResult(), cosXmlResultListener );
    }

    /**
     * Put Bucket Tag for cos
     * synchronous request
     * @param request, {@link PutBucketTaggingRequest}
     * @return PutBucketTaggingResult, {@link PutBucketTaggingResult}
     * @throws QCloudException, {@link QCloudException}
     */
    public PutBucketTaggingResult putBucketTagging(PutBucketTaggingRequest request) throws QCloudException {
        buildRequest(request);
        return (PutBucketTaggingResult) requestManager.send(request);
    }

    /**
     * asynchronous request
     * @param request, {@link PutBucketTaggingRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    public void putBucketTaggingAsync(PutBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener){
        requestSend(request, new PutBucketTaggingResult(), cosXmlResultListener );
    }

    /**
     *  try to cancel this request
     * @param cosXmlRequest, {@link CosXmlRequest}
     * @return boolean : true, cancelled is success; or false, cancel is failed, because of has already send or finished etc.
     */
    public boolean cancel(CosXmlRequest cosXmlRequest) {
        return requestManager.cancel(cosXmlRequest);
    }

    /**
     * try to cancel all request
     */
    public void cancelAll() {

        requestManager.cancelAll();
    }

    /**
     *  release resource for sdk(such as shutdown thread pools)
     */
    public void release(){
        requestManager.release();
    }

    /**
     * build request for QCloudHttpRequest
     * @param cosRequest, {@link QCloudHttpRequest}
     * @throws QCloudException, {@link QCloudException}
     */
    @Override
    protected void buildRequest(QCloudHttpRequest cosRequest) throws QCloudException {
        CosXmlRequest cosXmlRequest = (CosXmlRequest) cosRequest;
        cosXmlRequest.checkParameters();
        super.buildRequest(cosRequest);
    }

    private void getResultByException(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult,
                                      QCloudException qcloudException, CosXmlResultListener cosXmlResultListener){
        if(qcloudException != null){
            if(cosXmlResult != null && cosXmlResultListener != null){
               cosXmlResult.setHttpCode(2500 + qcloudException.getExceptionType().getCode());
                cosXmlResult.setHttpMessage("exception");
                cosXmlResult.error = new COSXMLError();
                cosXmlResult.error.code = qcloudException.getExceptionType().getMessage();
                cosXmlResult.error.message = qcloudException.getDetailMessage();
                cosXmlResult.error.traceId = "check exception message";
                cosXmlResult.error.requestId = "check exception message";
                if(cosXmlRequest != null){
                    cosXmlResult.error.resource = cosXmlRequest.getBucket();
                }
                cosXmlResultListener.onFail(cosXmlRequest, cosXmlResult);
            }else{
                qcloudException.printStackTrace();
            }
        }
    }

    private void getResultByQCloudResult(CosXmlRequest cosXmlRequest, QCloudResult qCloudResult,
                                         CosXmlResultListener cosXmlResultListener){
        if(qCloudResult != null && cosXmlResultListener != null){
            if(qCloudResult.getHttpCode() >= 300 || qCloudResult.getHttpCode() < 200){
                cosXmlResultListener.onFail(cosXmlRequest, (CosXmlResult) qCloudResult);
            }else {
                if(cosXmlRequest instanceof AppendObjectRequest ||
                        cosXmlRequest instanceof PutObjectRequest
                        || cosXmlRequest instanceof CompleteMultiUploadRequest){
                    generateAccessUrl(cosXmlRequest, (CosXmlResult) qCloudResult);
                }
                cosXmlResultListener.onSuccess(cosXmlRequest, (CosXmlResult) qCloudResult);
            }
        }
    }

    private void requestSend(final CosXmlRequest cosXmlRequest, final CosXmlResult cosXmlResult,
                             final CosXmlResultListener cosXmlResultListener){
        try {
            buildRequest(cosXmlRequest);
        } catch (QCloudException e) {
            getResultByException(cosXmlRequest, cosXmlResult, e, cosXmlResultListener);
            return;
        }
        requestManager.send(cosXmlRequest, new QCloudResultListener() {
            @Override
            public void onSuccess(QCloudRequest request, QCloudResult result) {
                getResultByQCloudResult(cosXmlRequest,result,cosXmlResultListener);
            }

            @Override
            public void onFailed(QCloudRequest request, QCloudException exception) {
                getResultByException(cosXmlRequest,cosXmlResult,exception,cosXmlResultListener);
            }
        });
    }

    private void generateAccessUrl(final CosXmlRequest cosXmlRequest, final CosXmlResult cosXmlResult){
        if(cosXmlRequest == null || cosXmlResult == null){
            return;
        }
        if(cosXmlResult.getHttpCode() >= 200 && cosXmlResult.getHttpCode() < 300){
            StringBuilder accessUrl = new StringBuilder();
            QCloudServiceConfig qCloudServiceConfig = getServiceConfig();
            if(qCloudServiceConfig != null){
                accessUrl.append(qCloudServiceConfig.getHttpProtocol())
                        .append("://")
                        .append(cosXmlRequest.getBucket())
                        .append(qCloudServiceConfig.getHttpHost())
                        .append(cosXmlRequest.getRequestPath());
            }
            cosXmlResult.accessUrl = accessUrl.toString();
        }
    }


}

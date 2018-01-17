package com.tencent.cos.xml;

import android.content.Context;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
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
import com.tencent.cos.xml.model.bucket.ListBucketVersionsRequest;
import com.tencent.cos.xml.model.bucket.ListBucketVersionsResult;
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
import com.tencent.cos.xml.model.bucket.PutBucketVersioningRequest;
import com.tencent.cos.xml.model.bucket.PutBucketVersioningResult;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.AppendObjectResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.CopyObjectResult;
import com.tencent.cos.xml.model.object.DeleteMultiObjectRequest;
import com.tencent.cos.xml.model.object.DeleteMultiObjectResult;
import com.tencent.cos.xml.model.object.GetObjectACLRequest;
import com.tencent.cos.xml.model.object.GetObjectACLResult;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.HeadObjectResult;
import com.tencent.cos.xml.model.object.OptionObjectRequest;
import com.tencent.cos.xml.model.object.OptionObjectResult;
import com.tencent.cos.xml.model.object.PutObjectACLRequest;
import com.tencent.cos.xml.model.object.PutObjectACLResult;
import com.tencent.cos.xml.model.object.UploadPartCopyRequest;
import com.tencent.cos.xml.model.object.UploadPartCopyResult;
import com.tencent.cos.xml.model.service.GetServiceRequest;
import com.tencent.cos.xml.model.service.GetServiceResult;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public class CosXmlService extends CosXmlSimpleService implements CosXml {

    public CosXmlService(Context context, CosXmlServiceConfig configuration, QCloudCredentialProvider qCloudCredentialProvider){
        super(context, configuration, qCloudCredentialProvider);
    }

    @Override
    public GetServiceResult getService(GetServiceRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetServiceResult());
    }

    @Override
    public void getServiceAsync(GetServiceRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetServiceResult(), cosXmlResultListener);
    }

    @Override
    public AppendObjectResult appendObject(AppendObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        AppendObjectResult appendObjectResult =  new AppendObjectResult();
        appendObjectResult.accessUrl = getAccessUrl(request);
        return execute(request, appendObjectResult);
    }

    @Override
    public void appendObjectAsync(AppendObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        AppendObjectResult appendObjectResult =  new AppendObjectResult();
        appendObjectResult.accessUrl = getAccessUrl(request);
        schedule(request, appendObjectResult, cosXmlResultListener);
    }

    @Override
    public DeleteMultiObjectResult deleteMultiObject(DeleteMultiObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteMultiObjectResult());
    }

    @Override
    public void deleteMultiObjectAsync(DeleteMultiObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteMultiObjectResult(), cosXmlResultListener);
    }

    @Override
    public GetObjectACLResult getObjectACL(GetObjectACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetObjectACLResult());
    }

    @Override
    public void getObjectACLAsync(GetObjectACLRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetObjectACLResult(), cosXmlResultListener);
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
    public OptionObjectResult optionObject(OptionObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new OptionObjectResult());
    }

    @Override
    public void optionObjectAsync(OptionObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new OptionObjectResult(), cosXmlResultListener);
    }

    @Override
    public PutObjectACLResult putObjectACL(PutObjectACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new PutObjectACLResult());
    }

    @Override
    public void putObjectACLAsync(PutObjectACLRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new PutObjectACLResult(), cosXmlResultListener);
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

    @Override
    public DeleteBucketCORSResult deleteBucketCORS(DeleteBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteBucketCORSResult());
    }

    @Override
    public void deleteBucketCORSAsync(DeleteBucketCORSRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteBucketCORSResult(), cosXmlResultListener);
    }

    @Override
    public DeleteBucketLifecycleResult deleteBucketLifecycle(DeleteBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteBucketLifecycleResult());
    }

    @Override
    public void deleteBucketLifecycleAsync(DeleteBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteBucketLifecycleResult(), cosXmlResultListener);
    }

    @Override
    public DeleteBucketResult deleteBucket(DeleteBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteBucketResult());
    }

    @Override
    public void deleteBucketAsync(DeleteBucketRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteBucketResult(), cosXmlResultListener);
    }

    @Override
    public DeleteBucketTaggingResult deleteBucketTagging(DeleteBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteBucketTaggingResult());
    }

    @Override
    public void deleteBucketTaggingAsync(DeleteBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteBucketTaggingResult(), cosXmlResultListener);
    }

    @Override
    public GetBucketACLResult getBucketACL(GetBucketACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketACLResult());
    }

    @Override
    public void getBucketACLAsync(GetBucketACLRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketACLResult(), cosXmlResultListener);
    }

    @Override
    public GetBucketCORSResult getBucketCORS(GetBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketCORSResult());
    }

    @Override
    public void getBucketCORSAsync(GetBucketCORSRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketCORSResult(), cosXmlResultListener);
    }

    @Override
    public GetBucketLifecycleResult getBucketLifecycle(GetBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketLifecycleResult());
    }

    @Override
    public void getBucketLifecycleAsync(GetBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketLifecycleResult(), cosXmlResultListener);
    }

    @Override
    public GetBucketLocationResult getBucketLocation(GetBucketLocationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketLocationResult());
    }

    @Override
    public void getBucketLocationAsync(GetBucketLocationRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketLocationResult(), cosXmlResultListener);
    }

    @Override
    public GetBucketResult getBucket(GetBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketResult());
    }

    @Override
    public void getBucketAsync(GetBucketRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketResult(), cosXmlResultListener);
    }

    @Override
    public GetBucketTaggingResult getBucketTagging(GetBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketTaggingResult());
    }

    @Override
    public void getBucketTaggingAsync(GetBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketTaggingResult(), cosXmlResultListener);
    }

    @Override
    public HeadBucketResult headBucket(HeadBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new HeadBucketResult());
    }

    @Override
    public void headBucketAsync(HeadBucketRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new HeadBucketResult(), cosXmlResultListener);
    }

    @Override
    public ListMultiUploadsResult listMultiUploads(ListMultiUploadsRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new ListMultiUploadsResult());
    }

    @Override
    public void listMultiUploadsAsync(ListMultiUploadsRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new ListMultiUploadsResult(), cosXmlResultListener);
    }

    @Override
    public PutBucketACLResult putBucketACL(PutBucketACLRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new PutBucketACLResult());
    }

    @Override
    public void putBucketACLAsync(PutBucketACLRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new PutBucketACLResult(), cosXmlResultListener);
    }

    @Override
    public PutBucketCORSResult putBucketCORS(PutBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new PutBucketCORSResult());
    }

    @Override
    public void putBucketCORSAsync(PutBucketCORSRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new PutBucketCORSResult(), cosXmlResultListener);
    }

    @Override
    public PutBucketLifecycleResult putBucketLifecycle(PutBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new PutBucketLifecycleResult());
    }

    @Override
    public void putBucketLifecycleAsync(PutBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new PutBucketLifecycleResult(), cosXmlResultListener);
    }

    @Override
    public PutBucketResult putBucket(PutBucketRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new PutBucketResult());
    }

    @Override
    public void putBucketAsync(PutBucketRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new PutBucketResult(), cosXmlResultListener);
    }

//    @Override
//    public PutBucketTaggingResult putBucketTagging(PutBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException {
//        return execute(request, new PutBucketTaggingResult());
//    }
//
//    @Override
//    public void putBucketTaggingAsync(PutBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener) {
//        schedule(request, new UploadPartResult(), cosXmlResultListener);
//    }

    @Override
    public GetBucketVersioningResult getBucketVersioning(GetBucketVersioningRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketVersioningResult());
    }

    @Override
    public void getBucketVersioningAsync(GetBucketVersioningRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketVersioningResult(), cosXmlResultListener);
    }

    @Override
    public PutBucketVersioningResult putBucketVersioning(PutBucketVersioningRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new PutBucketVersioningResult());
    }

    @Override
    public void putBucketVersionAsync(PutBucketVersioningRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new PutBucketVersioningResult(), cosXmlResultListener);
    }

    @Override
    public GetBucketReplicationResult getBucketReplication(GetBucketReplicationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetBucketReplicationResult());
    }

    @Override
    public void getBucketReplicationAsync(GetBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetBucketReplicationResult(), cosXmlResultListener);
    }

    @Override
    public PutBucketReplicationResult putBucketReplication(PutBucketReplicationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new PutBucketReplicationResult());
    }

    @Override
    public void putBucketReplicationAsync(PutBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new PutBucketReplicationResult(), cosXmlResultListener);
    }

    @Override
    public DeleteBucketReplicationResult deleteBucketReplication(DeleteBucketReplicationRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteBucketReplicationResult());
    }

    @Override
    public void deleteBucketReplicationAsync(DeleteBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteBucketReplicationResult(), cosXmlResultListener);
    }

    @Override
    public ListBucketVersionsResult listBucketVersions(ListBucketVersionsRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new ListBucketVersionsResult());
    }

    @Override
    public void listBucketVersionsAsync(ListBucketVersionsRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new ListBucketVersionsResult(), cosXmlResultListener);
    }
}

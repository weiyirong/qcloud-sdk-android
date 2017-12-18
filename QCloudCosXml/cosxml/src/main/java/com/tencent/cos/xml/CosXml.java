package com.tencent.cos.xml;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlRequest;
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
import com.tencent.cos.xml.transfer.MultipartUploadService;


/**
 *
 * <p>
 * 提供访问腾讯云COS服务的SDK接口，注意这里封装的是COS XML接口，COS JSON接口相关的SDK请参见<a
 * href="https://cloud.tencent.com/document/product/436/6517">COS V4</a>，由于COS XML接口相比JSON接口
 * 有更丰富的特性，因此我们更推荐您使用XML SDK。
 * </p>
 *
 * <p>
 * {@link CosXml}为每个接口都提供了同步和异步操作，如同步接口为{@link CosXml#getService(GetServiceRequest)}，
 * 则对应的异步接口为{@link CosXml#getServiceAsync(GetServiceRequest, CosXmlResultListener)}
 *
 * </p>
 *
 * <br>
 * COS XML SDK中一共有三种类型的接口：
 * <ul>
 * <li>Service接口 ：用户账户相关接口，目前仅有list bucket功能</li>
 * <li>Bucket接口 ：和bucket相关接口，主要用于设置、删除和获取bucket属性</li>
 * <li>Object接口 ：和Object相关接口，主要用于上传下载文件以及设置和删除文件属性</li>
 * </ul>
 *
 * <br>
 *
 * 更多信息请参见<a
 * href="https://cloud.tencent.com/document/product/436/7751"> 腾讯云COS XML API文档</a>
 *
 * @see CosXmlService
 *
 */

public interface CosXml {


    /**
     * <p>
     * 获取bucket列表
     * </p>
     * <p>
     * 该接口会一次性返回请求者所有的bucket列表。
     * </p>
     *
     * @param getServiceRequest {@link GetServiceRequest}
     * @return GetServiceResult {@link GetServiceResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetServiceResult getService(GetServiceRequest getServiceRequest) throws CosXmlClientException, CosXmlServiceException;


    /**
     * {@link CosXml#getService(GetServiceRequest)}的异步接口
     *
     * @param getServiceRequest {@link GetServiceRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     * @see CosXml#getService(GetServiceRequest)
     */
    void getServiceAsync(GetServiceRequest getServiceRequest, final CosXmlResultListener cosXmlResultListener);


    //COS Object API

    /**
     * <p>
     * 分片上传相关接口，初始化分片上传。
     * </p>
     * <p>
     * 请求成功后会返回 UploadId，用于后续的 Upload Part 请求。
     * </p>
     *
     * @param request {@link InitMultipartUploadRequest}
     * @return InitMultipartUploadResult {@link InitMultipartUploadResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     * @see MultipartUploadService
     */
    InitMultipartUploadResult initMultipartUpload(InitMultipartUploadRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#initMultipartUpload(InitMultipartUploadRequest)}的异步接口
     *
     * @param request {@link InitMultipartUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void initMultipartUploadAsync(InitMultipartUploadRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 分片上传相关接口，列出上传分片接口
     * </p>
     *
     * <p>
     * 查询特定分块上传中的已上传的块，即罗列出指定 UploadId 所属的所有已上传成功的分块。
     * </p>
     *
     * @param request {@link ListPartsRequest}
     * @return ListPartsResult {@link ListPartsResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     * @see MultipartUploadService
     */
    ListPartsResult listParts(ListPartsRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#listParts(ListPartsRequest)} 的异步方法
     *
     * @param request {@link ListPartsRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void listPartsAsync(ListPartsRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 分片上传相关接口，上传分片。
     * </p>
     *
     * <p>
     * 在初始化以后的上传分块。
     * </p>
     *
     * @param request {@link UploadPartRequest}
     * @return UploadPartResult {@link UploadPartResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     * @see MultipartUploadService
     */
    UploadPartResult uploadPart(UploadPartRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     *
     * {@link CosXml#uploadPart(UploadPartRequest)} 异步接口
     *
     * @param request {@link UploadPartRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void uploadPartAsync(UploadPartRequest request, final CosXmlResultListener cosXmlResultListener);


    /**
     * <p>
     * 分片上传相关接口，删除整个分块上传。
     * </p>
     * <p>
     * 调用该接口后，该uploadId所标识的整个分块上传过程将被删除，包括正在上传和已经上传的分块数据。
     * </p>
     * <p>
     *
     * 建议您及时完成分块上传或者舍弃分块上传，因为已上传但是未终止的块会占用存储空间进而产生存储费用。
     * </p>
     *
     * @param abortMultiUploadRequest {@link AbortMultiUploadRequest}
     * @return AbortMultiUploadResult {@link AbortMultiUploadResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     * @see MultipartUploadService
     */
    AbortMultiUploadResult abortMultiUpload(AbortMultiUploadRequest abortMultiUploadRequest) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#abortMultiUpload(AbortMultiUploadRequest)}的异步接口
     *
     * @param abortMultiUploadRequest {@link AbortMultiUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     * @see CosXml#abortMultiUpload(AbortMultiUploadRequest)
     */
    void abortMultiUploadAsync(AbortMultiUploadRequest abortMultiUploadRequest, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 分片上传相关接口，完成整个分块上传。
     * </p>
     * <p>
     * 当使用 Upload Parts 上传完所有块以后，必须调用该 API 来完成整个文件的分块上传。
     * </p>
     *
     * @param request {@link CompleteMultiUploadRequest}
     * @return CompleteMultiUploadResult {@link CompleteMultiUploadResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     * @see MultipartUploadService
     */
    CompleteMultiUploadResult completeMultiUpload(CompleteMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#completeMultiUpload(CompleteMultiUploadRequest)}的异步接口
     *
     * @param request {@link CompleteMultiUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void completeMultiUploadAsync(CompleteMultiUploadRequest request, final CosXmlResultListener cosXmlResultListener);


    /**
     * <p>
     * 将一个对象以分块追加的方式上传至COS。
     * </p>
     *
     * <p>
     * 对象首次使用该接口上传时，该对象的属性自动为appendable，使用其他接口上传时则属性自动为normal（如果该对象已存在则属性会被覆盖为 normal），
     * 可以使用{@link CosXml#getObject(GetObjectRequest)}或{@link CosXml#headObject(HeadObjectRequest)}接口获取 x-cos-object-type响应头来判断对象属性。
     * 对象属性为appendable时才能使用本接口追加上传。
     * </p>
     * <p>
     * 追加上传的对象每个分块最小为 4K，建议大小 1M-5G。
     * </p>
     *
     * @param appendObjectRequest {@link AppendObjectRequest}
     * @return AppendObjectResult {@link AppendObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    AppendObjectResult appendObject(AppendObjectRequest appendObjectRequest) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#appendObject(AppendObjectRequest)}的异步接口
     *
     * @param request {@link AppendObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void appendObjectAsync(AppendObjectRequest request, final CosXmlResultListener cosXmlResultListener);


    /**
     * <p>
     * 中批量删除 Object，单次请求最大支持批量删除 1000 个 Object。
     * </p>
     * <p>
     * 对于响应结果，COS 提供 Verbose 和 Quiet 两种模式：Verbose 模式将返回每个 Object 的删除结果；Quiet 模式只返回报错的 Object 信息。
     * </p>
     *
     * @param request {@link DeleteMultiObjectRequest}
     * @return DeleteMultiObjectResult {@link DeleteMultiObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    DeleteMultiObjectResult deleteMultiObject(DeleteMultiObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#deleteMultiObject(DeleteMultiObjectRequest)}的异步接口
     * @param request {@link DeleteMultiObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteMultiObjectAsync(DeleteMultiObjectRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 删除存储桶 Bucket 中的一个文件（Object）删除。
     * </p>
     *
     * <p>
     * 该操作需要请求者对 Bucket 有 WRITE 权限。
     * </p>
     *
     * @param request {@link DeleteObjectRequest}
     * @return DeleteObjectResult {@link DeleteObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    DeleteObjectResult deleteObject(DeleteObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * <p>
     * {@link CosXml#deleteObject(DeleteObjectRequest)}的异步接口
     * </p>
     *
     * @param request {@link DeleteObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteObjectAsync(DeleteObjectRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 某个 Object 的访问权限。
     * </p>
     * <p>
     * 只有 Bucket 的持有者才有权限操作
     * </p>
     *
     * @param request {@link GetObjectACLRequest}
     * @return GetObjectACLResult {@link GetObjectACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetObjectACLResult getObjectACL(GetObjectACLRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getObjectACL(GetObjectACLRequest)}的异步接口
     *
     * @param request {@link GetObjectACLRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getObjectACLAsync(GetObjectACLRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 将一个Object下载至本地。
     * </p>
     * <p>
     * 需要请求者对目标 Object 具有读权限或目标 Object 对所有人都开放了读权限（公有读）。
     * </p>
     * @param request {@link GetObjectRequest}
     * @return GetObjectResult {@link GetObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetObjectResult getObject(GetObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getObject(GetObjectRequest)}的异步接口
     *
     * @param request {@link GetObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getObjectAsync(GetObjectRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 获取对应 Object 的 meta 信息数据。
     * </p>
     *
     * <p>
     * Head 的权限与 Get 的权限一致
     * </p>
     *
     * @param request {@link HeadObjectRequest}
     * @return HeadObjectResult {@link HeadObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    HeadObjectResult headObject(HeadObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#headObject(HeadObjectRequest)}的异步接口
     *
     * @param request {@link HeadObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void headObjectAsync(HeadObjectRequest request, final CosXmlResultListener cosXmlResultListener);


    /**
     * <p>
     * 实现 Object 跨域访问配置的预请求。
     * </p>
     * <p>
     * 发送跨域请求之前会发送一个 OPTIONS 请求并带上特定的来源域，HTTP 方法和 HEADER 信息等给 COS，以决定是否可以发送真正的跨域请求。当 CORS 配置不存在时，请求返回 403 Forbidden。
     * </p>
     *
     * <p>
     * 可以通过{@link CosXml#putBucketCORS(PutBucketCORSRequest)} 来开启Bucket的CORS支持
     * </p>
     *
     * @param request {@link OptionObjectRequest}
     * @return OptionObjectResult {@link OptionObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    OptionObjectResult optionObject(OptionObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#optionObject(OptionObjectRequest)} 的异步接口
     *
     * @param request {@link OptionObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void optionObjectAsync(OptionObjectRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 给Object配置ACL。
     * </p>
     *
     * <p>
     * PutObjectACL 是一个覆盖操作，传入新的 ACL 将覆盖原有 ACL。
     * </p>
     *
     * @param request {@link PutObjectACLRequest}
     * @return PutObjectACLResult {@link PutObjectACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutObjectACLResult putObjectACL(PutObjectACLRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putObjectACL(PutObjectACLRequest)} 的异步接口
     *
     * @param request {@link PutObjectACLRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putObjectACLAsync(PutObjectACLRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 简单上传文件。
     * </p>
     *
     * <p>
     * 简单上传适合于上传小文件，大文件请使用分片上传。
     * </p>
     *
     * @param request {@link PutObjectRequest}
     * @return PutObjectResult {@link PutObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     * @see MultipartUploadService
     */
    PutObjectResult putObject(PutObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putObject(PutObjectRequest)} 的异步接口
     *
     * @param request {@link PutObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putObjectAsync(PutObjectRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * Copy Object for cos
     * synchronous request
     * @param request, {@link CopyObjectRequest}
     * @return CopyObjectResult, {@link CopyObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    CopyObjectResult copyObject(CopyObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * asynchronous request
     * @param request, {@link CopyObjectRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    void copyObjectAsync(CopyObjectRequest request, final CosXmlResultListener cosXmlResultListener);


    /**
     * Upload Part Copy Object for cos
     * synchronous request
     * @param request, {@link UploadPartCopyRequest}
     * @return CopyObjectResult, {@link CopyObjectResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    UploadPartCopyResult copyObject(UploadPartCopyRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * asynchronous request
     * @param request, {@link UploadPartCopyRequest}
     * @param cosXmlResultListener, {@link CosXmlResultListener}
     */
    void copyObjectAsync(UploadPartCopyRequest request,final CosXmlResultListener cosXmlResultListener);

    //COS Bucket API

    /**
     * <p>
     * 删除Bucket的跨域访问配置信息
     * </p>
     *
     *
     * @param request {@link DeleteBucketCORSRequest}
     * @return DeleteBucketCORSResult {@link DeleteBucketCORSResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    DeleteBucketCORSResult deleteBucketCORS(DeleteBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#deleteBucketCORS(DeleteBucketCORSRequest)} 的异步接口
     *
     * @param request {@link DeleteBucketCORSRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteBucketCORSAsync(DeleteBucketCORSRequest request, final CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 删除 Bucket 的生命周期配置。
     * </p>
     *
     * @param request {@link DeleteBucketLifecycleRequest}
     * @return DeleteBucketLifecycleResult {@link DeleteBucketLifecycleResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    DeleteBucketLifecycleResult deleteBucketLifecycle(DeleteBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#deleteBucketLifecycle(DeleteBucketLifecycleRequest)} 的异步接口
     *
     * @param request {@link DeleteBucketLifecycleRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteBucketLifecycleAsync(DeleteBucketLifecycleRequest request,CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 删除Bucket。
     * </p>
     *
     * @param request {@link DeleteBucketRequest}
     * @return DeleteBucketResult {@link DeleteBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    DeleteBucketResult deleteBucket(DeleteBucketRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#deleteBucket(DeleteBucketRequest)} 的异步接口。
     *
     * @param request {@link DeleteBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteBucketAsync(DeleteBucketRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 暂时无法使用。
     * </p>
     *
     * @param request {@link DeleteBucketTaggingRequest}
     * @return DeleteBucketTaggingResult {@link DeleteBucketTaggingResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    DeleteBucketTaggingResult deleteBucketTagging(DeleteBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * <p>
     * 暂时无法使用
     * </p>
     *
     * {@link CosXml#deleteBucketTagging(DeleteBucketTaggingRequest)} 的异步接口。
     *
     * @param request{@link DeleteBucketTaggingRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteBucketTaggingAsync(DeleteBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 获取Bucket的ACL信息。
     * </p>
     *
     * @param request {@link GetBucketACLRequest}
     * @return GetBucketACLResult {@link GetBucketACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketACLResult getBucketACL(GetBucketACLRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getBucketACL(GetBucketACLRequest)} 的异步接口。
     *
     * @param request {@link GetBucketACLRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketACLAsync(GetBucketACLRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 获取Bucket的CORS信息。
     * </p>
     *
     * @param request {@link GetBucketCORSRequest}
     * @return GetBucketCORSResult {@link GetBucketCORSResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketCORSResult getBucketCORS(GetBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException;


    /**
     * {@link CosXml#getBucketCORS(GetBucketCORSRequest)} 的异步接口。
     *
     * @param request {@link GetBucketCORSRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketCORSAsync(GetBucketCORSRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 获取Bucket的生命周期配置。
     * </p>
     *
     * @param request {@link GetBucketLifecycleRequest}
     * @return GetBucketLifecycleResult {@link GetBucketLifecycleResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketLifecycleResult getBucketLifecycle(GetBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getBucketLifecycle(GetBucketLifecycleRequest)} 的异步接口。
     *
     * @param request {@link GetBucketLifecycleRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketLifecycleAsync(GetBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 获取 Bucket 所在的地域信息。
     * </p>
     *
     * @param request {@link GetBucketLocationRequest}
     * @return GetBucketLocationResult {@link GetBucketLocationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketLocationResult getBucketLocation(GetBucketLocationRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getBucketLocation(GetBucketLocationRequest)} 的异步接口。
     *
     * @param request {@link GetBucketLocationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketLocationAsync(GetBucketLocationRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 列出Bucket下的Object。
     * </p>
     * <p>
     * 支持分页获取。
     * </p>
     *
     * @param request {@link GetBucketRequest}
     * @return GetBucketResult {@link GetBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketResult getBucket(GetBucketRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getBucket(GetBucketRequest)} 的异步接口。
     *
     * @param request {@link GetBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketAsync(GetBucketRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 暂时不支持。
     * </p>
     *
     * @param request {@link GetBucketTaggingRequest}
     * @return GetBucketTaggingResult {@link GetBucketTaggingResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketTaggingResult getBucketTagging(GetBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getBucketTagging(GetBucketTaggingRequest)} 的异步接口。
     *
     * @param request {@link GetBucketTaggingRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketTaggingAsync(GetBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener);


    /**
     * <p>
     * 判断Bucket是否存在，且是否有权限访问。
     * </p>
     *
     * @param request {@link HeadBucketRequest}
     * @return HeadBucketResult {@link HeadBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    HeadBucketResult headBucket(HeadBucketRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#headBucket(HeadBucketRequest)} 的同步接口。
     *
     * @param request {@link HeadBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void headBucketAsync(HeadBucketRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 查询正在进行中的分块上传。
     * </p>
     *
     * <p>
     * 支持分页查询，单次查询最多可以列出1000个正在进行中的分块上传。
     * </p>
     *
     * @param request {@link ListMultiUploadsRequest}
     * @return ListMultiUploadsResult {@link ListMultiUploadsResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    ListMultiUploadsResult listMultiUploads(ListMultiUploadsRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#listMultiUploads(ListMultiUploadsRequest)} 的异步接口。
     *
     * @param request {@link ListMultiUploadsRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void listMultiUploadsAsync(ListMultiUploadsRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 设置Bucket的ACL表。
     * </p>
     *
     * <p>
     * Put Bucket ACL 是一个覆盖操作，传入新的 ACL 将覆盖原有 ACL。
     * </p>
     *
     * @param request {@link PutBucketACLRequest}
     * @return PutBucketACLResult {@link PutBucketACLResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutBucketACLResult putBucketACL(PutBucketACLRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putBucketACL(PutBucketACLRequest)} 的异步接口。
     *
     * @param request {@link PutBucketACLRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putBucketACLAsync(PutBucketACLRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 设置Bucket的CORS。
     * </p>
     *
     * <p>
     * 使用 PutBucketCORS 接口创建的规则权限是覆盖当前的所有规则而不是新增一条权限规则。
     * </p>
     *
     * @param request {@link PutBucketCORSRequest}
     * @return PutBucketCORSResult {@link PutBucketCORSResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutBucketCORSResult putBucketCORS(PutBucketCORSRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putBucketCORS(PutBucketCORSRequest)} 的异步接口。
     *
     * @param request {@link PutBucketCORSRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putBucketCORSAsync(PutBucketCORSRequest request, CosXmlResultListener cosXmlResultListener);


    /**
     * <p>
     * 为 Bucket 创建一个新的生命周期配置。
     * </p>
     *
     * <p>
     * 如果该 Bucket 已配置生命周期，使用该接口创建新的配置的同时则会覆盖原有的配置。
     * </p>
     *
     * @param request {@link PutBucketLifecycleRequest}
     * @return PutBucketLifecycleResult {@link PutBucketLifecycleResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutBucketLifecycleResult putBucketLifecycle(PutBucketLifecycleRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putBucketLifecycle(PutBucketLifecycleRequest)} 的异步接口。
     *
     * @param request {@link PutBucketLifecycleRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putBucketLifecycleAsync(PutBucketLifecycleRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 在指定账号下创建一个 Bucket。
     * </p>
     *
     * @param request {@link PutBucketRequest}
     * @return PutBucketResult {@link PutBucketResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutBucketResult putBucket(PutBucketRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putBucket(PutBucketRequest)} 的异步接口。
     *
     * @param request {@link PutBucketRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putBucketAsync(PutBucketRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 暂时不支持
     * </p>
     *
     * @param request {@link PutBucketTaggingRequest}
     * @return PutBucketTaggingResult {@link PutBucketTaggingResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutBucketTaggingResult putBucketTagging(PutBucketTaggingRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putBucketTagging(PutBucketTaggingRequest)} 的异步接口。
     *
     * @param request {@link PutBucketTaggingRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putBucketTaggingAsync(PutBucketTaggingRequest request, CosXmlResultListener cosXmlResultListener);


    /**
     * <p>
     * 获取指定Bucket的版本控制信息。
     * </p>
     *
     * @param request {@link GetBucketVersioningRequest}
     * @return GetBucketVersioningResult {@link GetBucketVersioningResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketVersioningResult getBucketVersioning(GetBucketVersioningRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getBucketVersioning(GetBucketVersioningRequest)} 的异步接口。
     *
     * @param request {@link GetBucketVersioningRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketVersioningAsync(GetBucketVersioningRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 设置指定Bucket的版本控制功能。
     * </p>
     *
     * @param request {@link PutBucketVersioningRequest}
     * @return PutBucketVersioningResult {@link PutBucketVersioningResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutBucketVersioningResult putBucketVersioning(PutBucketVersioningRequest request)throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putBucketVersioning(PutBucketVersioningRequest)} 的异步接口。
     *
     * @param request {@link PutBucketVersioningRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putBucketVersionAsync(PutBucketVersioningRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 获取指定Bucket的跨区域复制配置信息。
     * </p>
     *
     * @param request {@link GetBucketReplicationRequest}
     * @return GetBucketReplicationResult {@link GetBucketReplicationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    GetBucketReplicationResult getBucketReplication(GetBucketReplicationRequest request)throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#getBucketReplication(GetBucketReplicationRequest)} 的异步接口。
     *
     * @param request {@link GetBucketReplicationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getBucketReplicationAsync(GetBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 设置指定Bucket的跨区域复制配置信息。
     * </p>
     *
     * @param request {@link PutBucketReplicationRequest}
     * @return PutBucketReplicationResult {@link PutBucketReplicationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    PutBucketReplicationResult putBucketReplication(PutBucketReplicationRequest request)throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#putBucketReplication(PutBucketReplicationRequest)} 的异步接口。
     *
     * @param request {@link PutBucketReplicationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putBucketReplicationAsync(PutBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener);

    /**
     * <p>
     * 删除指定Bucket的跨区域复制配置信息。
     * </p>
     *
     * @param request {@link DeleteBucketReplicationRequest}
     * @return DeleteBucketReplicationResult {@link DeleteBucketReplicationResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     */
    DeleteBucketReplicationResult deleteBucketReplication(DeleteBucketReplicationRequest request)throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link CosXml#deleteBucketReplication(DeleteBucketReplicationRequest)} 的异步接口。
     *
     * @param request {@link DeleteBucketReplicationRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteBucketReplicationAsync(DeleteBucketReplicationRequest request, CosXmlResultListener cosXmlResultListener);
//    /**
//     * <p>
//     * 取消单个请求。
//     * </p>
//     *
//     * @param cosXmlRequest {@link CosXmlRequest}
//     * @return boolean : true, cancelled is success; or false, cancel is failed, because of has already send or finished etc.
//     */
    void cancel(CosXmlRequest cosXmlRequest);

    /**
     * <p>
     * 取消所有的请求。
     * </p>
     *
     */
    void cancelAll();

    /**
     * <p>
     * 释放资源，如关闭线程池等。
     * </p>
     */
    void release();

}

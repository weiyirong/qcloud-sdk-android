package com.tencent.cos.xml;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
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

/**
 * <p>
 * 提供访问腾讯云COS服务的SDK接口，注意这里封装的是COS XML接口，COS JSON接口相关的SDK请参见<a
 * href="https://cloud.tencent.com/document/product/436/6517">COS V4</a>，由于COS XML接口相比JSON接口
 * 有更丰富的特性，因此我们更推荐您使用XML SDK。
 * </p>
 *
 * <p>
 * {@link SimpleCosXml}为每个接口都提供了同步和异步操作，如同步接口为{@link SimpleCosXml#putObject(PutObjectRequest)}，
 * 则对应的异步接口为{@link SimpleCosXml#putObjectAsync(PutObjectRequest, CosXmlResultListener)}
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
 * @see CosXmlSimpleService
 *
 */

public interface SimpleCosXml {


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
     *
     */
    InitMultipartUploadResult initMultipartUpload(InitMultipartUploadRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link SimpleCosXml#initMultipartUpload(InitMultipartUploadRequest)}的异步接口
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
     * @return ListParts {@link ListPartsResult}
     * @throws CosXmlClientException {@link CosXmlClientException}
     * @throws CosXmlServiceException {@link CosXmlServiceException}
     *
     */
    ListPartsResult listParts(ListPartsRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link SimpleCosXml#listParts(ListPartsRequest)} 的异步方法
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
     *
     */
    UploadPartResult uploadPart(UploadPartRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     *
     * {@link SimpleCosXml#uploadPart(UploadPartRequest)} 异步接口
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
     *
     */
    AbortMultiUploadResult abortMultiUpload(AbortMultiUploadRequest abortMultiUploadRequest) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link SimpleCosXml#abortMultiUpload(AbortMultiUploadRequest)}的异步接口
     *
     * @param abortMultiUploadRequest {@link AbortMultiUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     * @see SimpleCosXml#abortMultiUpload(AbortMultiUploadRequest)
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
     *
     */
    CompleteMultiUploadResult completeMultiUpload(CompleteMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link SimpleCosXml#completeMultiUpload(CompleteMultiUploadRequest)}的异步接口
     *
     * @param request {@link CompleteMultiUploadRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void completeMultiUploadAsync(CompleteMultiUploadRequest request, final CosXmlResultListener cosXmlResultListener);

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
     * {@link SimpleCosXml#deleteObject(DeleteObjectRequest)}的异步接口
     * </p>
     *
     * @param request {@link DeleteObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void deleteObjectAsync(DeleteObjectRequest request, final CosXmlResultListener cosXmlResultListener);

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
     * {@link SimpleCosXml#getObject(GetObjectRequest)}的异步接口
     *
     * @param request {@link GetObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void getObjectAsync(GetObjectRequest request, final CosXmlResultListener cosXmlResultListener);

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
     *
     */
    PutObjectResult putObject(PutObjectRequest request) throws CosXmlClientException, CosXmlServiceException;

    /**
     * {@link SimpleCosXml#putObject(PutObjectRequest)} 的异步接口
     *
     * @param request {@link PutObjectRequest}
     * @param cosXmlResultListener {@link CosXmlResultListener}
     */
    void putObjectAsync(PutObjectRequest request, final CosXmlResultListener cosXmlResultListener);


    //    /**
//     * <p>
//     * 取消单个请求。
//     * </p>
//     *
//     * @param cosXmlRequest {@link CosXmlRequest}
//     * @return boolean : true, cancelled is success; or false, pause is failed, because of has already send or finished etc.
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

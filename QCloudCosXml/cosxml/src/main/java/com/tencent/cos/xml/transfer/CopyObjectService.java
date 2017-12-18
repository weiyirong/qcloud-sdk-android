package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.CopyObjectResult;
import com.tencent.cos.xml.model.object.HeadObjectRequest;
import com.tencent.cos.xml.model.object.HeadObjectResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.UploadPartCopyRequest;
import com.tencent.cos.xml.model.object.UploadPartCopyResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/11/16.
 */

public class CopyObjectService {

    private CosXmlService cosXmlService;

    private long maxSliceSize = 5 * 1024 * 1024;

    public CopyObjectService(CosXmlService cosXmlService){
        this.cosXmlService = cosXmlService;
    }

    public CosXmlResult copyObject(String bucket, String cosPath,
                                   CopyObjectRequest.CopySourceStruct copySourceStruct) throws CosXmlClientException, CosXmlServiceException {
        // head- get length for source

        long sourceLength = headObject(copySourceStruct.bucket, copySourceStruct.cosPath);

        //select which CopyObject by length
        if(sourceLength >= maxSliceSize){
            return copyObjectForLargeFile(bucket, cosPath, copySourceStruct, sourceLength);
        }else {
            return copyObjectForSmallFile(bucket, cosPath, copySourceStruct);
        }
    }

    private long headObject(String bucket, String cosPath) throws CosXmlServiceException, CosXmlClientException {
        HeadObjectRequest headObjectRequest = new HeadObjectRequest(bucket, cosPath);
        HeadObjectResult headObjectResult = cosXmlService.headObject(headObjectRequest);
        if(headObjectResult != null){
            String length = headObjectResult.getHeaders().get("Content-Length").get(0);
            return Long.valueOf(length);
        }
        return -1;
    }

    private CopyObjectResult copyObjectForSmallFile(String bucket, String cosPath, CopyObjectRequest.CopySourceStruct copySourceStruct) throws CosXmlServiceException, CosXmlClientException {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, cosPath, copySourceStruct);
        return cosXmlService.copyObject(copyObjectRequest);
    }

    private CompleteMultiUploadResult copyObjectForLargeFile(String bucket, String cosPath, CopyObjectRequest.CopySourceStruct copySourceStruct, long sourceLength) throws CosXmlClientException, CosXmlServiceException {
        String uploadId = initMultiUpload(bucket, cosPath);
        Map<Integer, String> partNumberAndEtag = new LinkedHashMap<>();
        int partNumber = 1;
        long start = 0;
        long end = -1;
        long sliceSize = maxSliceSize;
        UploadPartCopyResult copyObjectResult;
        while(true){
            if(end >= sourceLength-1)break;
            start = end + 1;
            end = (end + sliceSize >= sourceLength -1 ? sourceLength -1 : end + sliceSize);

            copyObjectResult = copyObjectForLargeFile(bucket, cosPath, partNumber, uploadId, copySourceStruct, start, end);
            partNumberAndEtag.put(partNumber, copyObjectResult.copyObject.eTag);
            partNumber ++;
        }

        //complete
        CompleteMultiUploadResult completeMultiUploadResult = completeMultipart(bucket, cosPath, uploadId, partNumberAndEtag);
        return completeMultiUploadResult;
    }

    private String initMultiUpload(String bucket, String cosPath) throws CosXmlServiceException, CosXmlClientException {
        InitMultipartUploadRequest initMultipartUploadRequest = new InitMultipartUploadRequest(bucket, cosPath);
        InitMultipartUploadResult initMultipartUploadResult = cosXmlService.initMultipartUpload(initMultipartUploadRequest);
        String uploadId = initMultipartUploadResult.initMultipartUpload.uploadId;
        return uploadId;
    }

    private UploadPartCopyResult copyObjectForLargeFile(String bucket, String cosPath, int partNumber, String uploadId,
                                                        CopyObjectRequest.CopySourceStruct copySourceStruct, long start, long end)
            throws CosXmlServiceException, CosXmlClientException {
        UploadPartCopyRequest uploadPartCopyRequest = new UploadPartCopyRequest(bucket, cosPath, partNumber, uploadId, copySourceStruct,
                start, end);
        return cosXmlService.copyObject(uploadPartCopyRequest);
    }

    private CompleteMultiUploadResult completeMultipart(String bucket, String cosPath, String uploadId,
                                                        Map<Integer, String> partNumberAndEtag)
            throws CosXmlServiceException, CosXmlClientException {
        CompleteMultiUploadRequest completeMultiUploadRequest = new CompleteMultiUploadRequest(bucket, cosPath, uploadId,
                partNumberAndEtag);
        CompleteMultiUploadResult completeMultiUploadResult = cosXmlService.completeMultiUpload(completeMultiUploadRequest);
        return completeMultiUploadResult;
    }

}

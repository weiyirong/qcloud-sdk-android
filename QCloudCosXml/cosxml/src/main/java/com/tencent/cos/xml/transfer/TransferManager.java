package com.tencent.cos.xml.transfer;



import android.content.Context;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.model.object.CopyObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectRequest;

/**
 * Created by bradyxiao on 2018/8/22.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public class TransferManager{

    private CosXmlSimpleService cosXmlService;
    private TransferConfig transferConfig;

    public TransferManager(CosXmlSimpleService cosXmlService, TransferConfig transferConfig){
        if(cosXmlService == null){
            throw new IllegalArgumentException("CosXmlService is null");
        }
        if(transferConfig == null){
            throw new IllegalArgumentException("TransferConfig is null");
        }
        this.cosXmlService = cosXmlService;
        this.transferConfig = transferConfig;
    }

    /**
     * 上传文件
     * @param bucket 存储桶
     * @param cosPath 文件存放于存储桶上的位置
     * @param srcPath 文件本地路径
     * @param uploadId 是否分片续传的uploadId
     * @return COSXMLUploadTask
     */
    public COSXMLUploadTask upload(String bucket, String cosPath, String srcPath, String uploadId){
        COSXMLUploadTask cosxmlUploadTask = new COSXMLUploadTask(cosXmlService, null, bucket, cosPath, srcPath, uploadId);
        cosxmlUploadTask.multiUploadSizeDivision = transferConfig.divisionForUpload; // 分片上传的界限
        cosxmlUploadTask.sliceSize = transferConfig.sliceSizeForUpload; // 分片上传的分片大小
        cosxmlUploadTask.upload();
        return cosxmlUploadTask;
    }

    /**
     * 上传文件
     * @param putObjectRequest 上传请求Request封装类
     * @param uploadId 是否分片续传的uploadId
     * @return COSXMLUploadTask
     */
    public COSXMLUploadTask upload(PutObjectRequest putObjectRequest, String uploadId){
        COSXMLUploadTask cosxmlUploadTask = new COSXMLUploadTask(cosXmlService, putObjectRequest, uploadId);
        cosxmlUploadTask.multiUploadSizeDivision = transferConfig.divisionForUpload; // 分片上传的界限
        cosxmlUploadTask.sliceSize = transferConfig.sliceSizeForUpload; // 分片上传的分片大小
        cosxmlUploadTask.upload();
        return cosxmlUploadTask;
    }

    /**
     * 下载文件
     * @param context app上下文
     * @param bucket 存储桶
     * @param cosPath 文件存放于存储桶上的位置
     * @param savedDirPath 文件下载到本地的路径
     * @return COSXMLDownloadTask
     */
    public COSXMLDownloadTask download(Context context, String bucket, String cosPath, String savedDirPath){
        return download(context, bucket, cosPath, savedDirPath, null);
    }

    /**
     * 下载文件
     * @param context app上下文
     * @param bucket 存储桶
     * @param cosPath 文件存放于存储桶上的位置
     * @param savedDirPath 文件下载到本地的路径
     * @param savedFileName 文件下载本地的别名
     * @return COSXMLDownloadTask
     */
    public COSXMLDownloadTask download(Context context, String bucket, String cosPath, String savedDirPath, String savedFileName){
        COSXMLDownloadTask cosxmlDownloadTask = new COSXMLDownloadTask(context, cosXmlService, null, bucket, cosPath, savedDirPath, savedFileName);
        cosxmlDownloadTask.download();
        return cosxmlDownloadTask;
    }

    /**
     * 下载文件
     * @param context app上下文
     * @param getObjectRequest 下载请求Request封装类
     * @return COSXMLDownloadTask
     */
    public COSXMLDownloadTask download(Context context, GetObjectRequest getObjectRequest){
        COSXMLDownloadTask cosxmlDownloadTask = new COSXMLDownloadTask(context, cosXmlService, getObjectRequest);
        cosxmlDownloadTask.download();
        return cosxmlDownloadTask;
    }

    /**
     * 复制文件
     * @param bucket 存储桶
     * @param cosPath 文件存放于存储桶上的位置
     * @param copySourceStruct 源文件存储于COS的位置
     * @return COSXMLCopyTask
     */
    public COSXMLCopyTask copy(String bucket, String cosPath, CopyObjectRequest.CopySourceStruct copySourceStruct){
        COSXMLCopyTask cosxmlCopyTask = new COSXMLCopyTask(cosXmlService, null, bucket, cosPath, copySourceStruct);
        cosxmlCopyTask.multiCopySizeDivision = transferConfig.divisionForCopy;
        cosxmlCopyTask.sliceSize = transferConfig.sliceSizeForCopy;
        cosxmlCopyTask.copy();
        return cosxmlCopyTask;
    }

    /**
     * 复制文件
     * @param copyObjectRequest 复制请求Request封装类
     * @return COSXMLCopyTask
     */
    public COSXMLCopyTask copy(CopyObjectRequest copyObjectRequest){
        COSXMLCopyTask cosxmlCopyTask = new COSXMLCopyTask(cosXmlService, copyObjectRequest);
        cosxmlCopyTask.multiCopySizeDivision = transferConfig.divisionForCopy;
        cosxmlCopyTask.sliceSize = transferConfig.sliceSizeForCopy;
        cosxmlCopyTask.copy();
        return cosxmlCopyTask;
    }

}

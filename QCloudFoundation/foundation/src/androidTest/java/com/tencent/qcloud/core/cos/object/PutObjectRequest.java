package com.tencent.qcloud.core.cos.object;


import android.support.annotation.NonNull;

import com.tencent.qcloud.core.cos.CosXmlRequest;

import java.io.File;
import java.io.InputStream;

/**
 * <p>
 * 将本地的文件（Object）上传至指定 COS 中。
 * </p>
 *
 */
final public class PutObjectRequest extends CosXmlRequest<PutObjectResult> {
    private String cosPath;
    private String srcPath;
    private byte[] data;
    private InputStream inputStream;
    private long fileLength;

    public PutObjectRequest(String bucket, String cosPath, String srcPath){
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.srcPath = srcPath;
    }

    public PutObjectRequest(String bucket, String cosPath, byte[] data){
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.data = data;
    }

    public PutObjectRequest(String bucket, String cosPath, InputStream inputStream, long sendLength){
        this.bucket = bucket;
        this.cosPath = cosPath;
        this.inputStream = inputStream;
        this.fileLength = sendLength;
    }

    /**
     * 设置上传Object在COS上的路径
     *
     * @param cosPath Object在COS上的路径
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    /**
     * 获取用户设置的COS路径
     *
     * @return Object上COS上的路径
     */
    public String getCosPath() {
        return cosPath;
    }

    /**
     * <p>
     * 设置上传的本地文件路径
     * </p>
     * <p>
     * 可以设置上传本地文件、字节数组或者输入流。每次只能上传一种类型，若同时设置，
     * 则优先级为 本地文件&gt;字节数组&gt;输入流
     * </p>
     *
     * @param srcPath 本地文件路径
     * @see PutObjectRequest#setData(byte[])
     * @see PutObjectRequest#setInputStream(InputStream, long)
     */
    public void setSrcPath(@NonNull String srcPath){
        this.srcPath = srcPath;
    }

    /**
     * 获取设置的本地文件路径
     *
     * @return
     */
    public String getSrcPath(){
       return srcPath;
    }

    /**
     * <p>
     * 设置上传的字节数组
     * </p>
     * <p>
     * 可以设置上传本地文件、字节数组或者输入流。每次只能上传一种类型，若同时设置，
     * 则优先级为 本地文件&gt;字节数组&gt;输入流
     * </p>
     *
     * @param data 需要上传的字节数组
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * 获取用户设置的字节数组
     *
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     * <p>
     * 设置上传的输入流
     * </p>
     * <p>
     * 可以设置上传本地文件、字节数组或者输入流。每次只能上传一种类型，若同时设置，
     * 则优先级为 本地文件&gt;字节数组&gt;输入流
     * </p>
     *
     * @param inputStream 输入流
     * @param fileLength 读取的字节长度
     */
    public void setInputStream(InputStream inputStream, long fileLength) {
        this.inputStream = inputStream;
        this.fileLength = fileLength;
    }

    /**
     * 获取用户设置的输入流
     *
     * @return
     */
    public InputStream getInputStream() {
        return inputStream;
    }


    /**
     * 获取用户设置的输入流读取的字节长度
     *
     * @return
     */
    public long getFileLength() {
        if(inputStream != null){
            return fileLength;
        }
        if(srcPath != null){
            return new File(srcPath).length();
        }
        if(data != null){
            return data.length;
        }
        return  -1;
    }


}

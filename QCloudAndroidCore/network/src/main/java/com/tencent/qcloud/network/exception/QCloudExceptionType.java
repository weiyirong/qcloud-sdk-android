package com.tencent.qcloud.network.exception;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public enum QCloudExceptionType {

    UNDEFINE("undefine", 0),

    /**
     *
     *  请求参数设置不正确
     */
    REQUEST_PARAMETER_INCORRECT("request parameter setting incorrect", 1),

    /**
     *
     * OkHttp网络任务执行失败
     *
     */
    REQUEST_EXECUTE_FAILED("request execute failed", 2),


    /**
     *
     *
     * 用户取消网络请求导致网络线程报中断异常
     */
    REQUEST_USER_CANCELLED("user cancelled request", 3),


    /**
     * HTTP回包的BODY转化为JSON字符串失败
     */
    HTTP_RESPONSE_BODY_PARSE_JSON_FAILED("http response body parse json failed", 4),


    /**
     * HTTP回包中的JSON字符串转化为Object失败
     *
     */
    HTTP_RESPONSE_JSON_PARSE_OBJECT_FAILED("http response json parse object failed", 5),


    /**
     *
     * 系统不支持必要的编码格式
     *
     */
    UNSUPPORTED_ENCODING("unsupported encoding", 6),


    /**
     * 读写本地文件异常：
     *
     * 包括：找不到文件、
     *
     */
    WRITE_READ_LOCAL_FILE_FAILED("write or read local file failed", 7),


    /**
     *
     * HTTP 返回包解析异常
     *
     *
     */
    HTTP_RESPONSE_PARSE_FAILED("http response parse failed", 8),


    /**
     *
     * 类型转化错误
     *
     *
     */
    CLASS_TRANSFORM_FAILED("class transform failed", 9),


    /**
     *
     * 获取签名失败
     *
     *
     *
     */
    CALCULATE_SIGNATURE_FAILED("calculate signature failed.", 10),


    /**
     *
     *
     * 由于后台返回的Http响应不标准导致的异常
     *
     */

    NON_STANDARD_HTTP_RESPONSE("non standard http response", 11),



    ACTION_EXECUTE_FAILED("action execute failed", 12),

    CANCELED("canceled", 13);

    /**
     * 详细信息
     */
    String message;
    int code;

    QCloudExceptionType(String message, int code) {

        this.message = message;
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode(){
        return code;
    }

    public static QCloudExceptionType getQCloudExceptionType(int code){
        switch (code){
            case 1:
                return REQUEST_PARAMETER_INCORRECT;
            case 2:
                return REQUEST_EXECUTE_FAILED;
            case 3:
                return REQUEST_USER_CANCELLED;
            case 4:
                return HTTP_RESPONSE_BODY_PARSE_JSON_FAILED;
            case 5:
                return HTTP_RESPONSE_JSON_PARSE_OBJECT_FAILED;
            case 6:
                return UNSUPPORTED_ENCODING;
            case 7:
                return WRITE_READ_LOCAL_FILE_FAILED;
            case 8:
                return HTTP_RESPONSE_PARSE_FAILED;
            case 9:
                return CLASS_TRANSFORM_FAILED;
            default:
                return UNDEFINE;
        }
    }
}

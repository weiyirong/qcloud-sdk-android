package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.qcloud.core.http.HttpResponse;

/**
 * 简单上传返回的结果.
 * 关于简单上传接口的描述，请查看 <a href="https://cloud.tencent.com/document/product/436/7749">
 * https://cloud.tencent.com/document/product/436/7749.</a><br>
 */
final public class PutObjectResult extends CosXmlResult {

    /**
     * 返回文件的 MD5 算法校验值.eTag 的值可以用于检查 Object 在上传过程中是否有损坏
     */
    public String eTag;

    /**
     *  @see CosXmlResult#parseResponseBody(HttpResponse)
     */
    @Override
    public void parseResponseBody(HttpResponse response) throws CosXmlServiceException, CosXmlClientException {
        super.parseResponseBody(response);
        eTag = response.header("ETag");
    }
}

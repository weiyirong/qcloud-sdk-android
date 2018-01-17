package com.tencent.qcloud.core.cos.object;

import com.tencent.qcloud.core.cos.CosXmlRequest;

/**
 * <p>
 * 删除一个Object
 * </p>
 *
 */
public class DeleteObjectRequest extends CosXmlRequest<DeleteObjectResult> {
    private String cosPath;

    /**
     *  set cosPath for object.
     * @param cosPath
     */
    public void setCosPath(String cosPath) {
        this.cosPath = cosPath;
    }

    public String getCosPath() {
        return cosPath;
    }
}

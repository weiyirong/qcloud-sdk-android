package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.ResponseXmlS3BodySerializer;
import com.tencent.cos.xml.model.tag.Delete;
import com.tencent.cos.xml.model.tag.DeleteObject;
import com.tencent.qcloud.core.network.QCloudNetWorkConstants;
import com.tencent.qcloud.core.network.QCloudRequestPriority;
import com.tencent.qcloud.core.network.action.QCloudBodyMd5Action;
import com.tencent.qcloud.core.network.request.serializer.RequestXmlBodySerializer;


import java.util.ArrayList;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 批量删除Object，单次请求最大支持批量删除 1000 个 Object。
 * </p>
 * <p>
 * 对于响应结果，COS 提供 Verbose 和 Quiet 两种模式：
 * Verbose 模式将返回每个 Object 的删除结果；Quiet 模式只返回报错的 Object 信息。
 * </p>
 *
 * @see com.tencent.cos.xml.CosXml#deleteMultiObject(DeleteMultiObjectRequest)
 * @see com.tencent.cos.xml.CosXml#deleteMultiObjectAsync(DeleteMultiObjectRequest, CosXmlResultListener)
 */
final public class DeleteMultiObjectRequest extends CosXmlRequest {
    private Delete delete;
    public DeleteMultiObjectRequest(String bucket, List<String> deleteObjectList){
        this.bucket = bucket;
        delete = new Delete();
        contentType = QCloudNetWorkConstants.ContentType.XML;
        requestHeaders.put(QCloudNetWorkConstants.HttpHeader.CONTENT_TYPE,contentType);
        delete.deleteObjectList = new ArrayList<DeleteObject>();
        setObjectList(deleteObjectList);
    }

    @Override
    protected void build() throws CosXmlClientException {
        super.build();

        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Object object : requestQueryParams.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
            }
        }

        if(requestHeaders.size() > 0){
            for(Object object : requestHeaders.entrySet()){
                Map.Entry<String,String> entry = (Map.Entry<String, String>) object;
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
            }
        }

        requestOriginBuilder.body(new RequestXmlBodySerializer(delete));

        requestActions.add(new QCloudBodyMd5Action());

        responseBodySerializer = new ResponseXmlS3BodySerializer(DeleteMultiObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("delete",null);
    }

    @Override
    protected void checkParameters() throws CosXmlClientException {
        if(bucket == null){
            throw new CosXmlClientException("bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = QCloudNetWorkConstants.RequestMethod.POST;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    /**
     * <p>
     * 设置是否为quiet模式。
     * </p>
     * <p>
     * Quiet 模式只返回报错的 Object 信息。否则返回每个 Object 的删除结果。
     * </p>
     * <p>默认false</p>
     *
     * @param quiet 设置是否为quiet模式
     */
    public void setQuiet(boolean quiet) {

        delete.quiet = quiet;
    }

    /**
     * 添加需要删除的Object
     *
     * @param object Object的路径
     */
    public void setObjectList(String object) {
        if(object != null){
            if(object.startsWith("/")){
                object = object.substring(1);
            }
            DeleteObject deleteObject = new DeleteObject();
            deleteObject.key = object;
            delete.deleteObjectList.add(deleteObject);
        }
    }

    /**
     * 添加多个需要删除的Objects
     *
     * @param objectList Objects的路径列表
     */
    public void setObjectList(List<String> objectList) {
        if(objectList != null){
            int size = objectList.size();
            DeleteObject deleteObject;
            for(int i = 0; i < size; ++ i){
                deleteObject = new DeleteObject();
                String object = objectList.get(i);
                if(object.startsWith("/")){
                    deleteObject.key = object.substring(1);
                }else{
                    deleteObject.key = object;
                }
                delete.deleteObjectList.add(deleteObject);
            }
        }
    }

    /**
     * 获取用户设置的需要批量删除的Objects
     *
     * @return
     */
    public Delete getDelete() {
        return delete;
    }

}

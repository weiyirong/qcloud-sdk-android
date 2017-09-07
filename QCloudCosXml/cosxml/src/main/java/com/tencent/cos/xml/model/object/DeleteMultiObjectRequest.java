package com.tencent.cos.xml.model.object;


import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.tag.Delete;
import com.tencent.cos.xml.model.tag.DeleteObject;
import com.tencent.qcloud.network.action.QCloudBodyMd5Action;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.request.serializer.body.RequestXmlBodySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/5/8.
 * author bradyxiao
 * Delete Multiple Object request is used for batch deletion of files. A maximum of 1000 files are
 * allowed to be deleted at a time. COS provides two modes for returned results: Verbose and Quiet.
 * Verbose mode will return the result of deletion of each Object, while Quiet mode only returns the
 * information of the Objects with an error.This request must be used with x-cos-sha1 to verify the
 * integrity of Body.
 */
public class DeleteMultiObjectRequest extends CosXmlRequest {
    private Delete delete;
    public DeleteMultiObjectRequest(){
        delete = new Delete();
        contentType = RequestContentType.APPLICATION_XML;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
        delete.deleteObjectList = new ArrayList<DeleteObject>();
    }

    @Override
    public void build() {
        priority = QCloudRequestPriority.Q_CLOUD_REQUEST_PRIORITY_NORMAL;

        setRequestMethod();
        requestOriginBuilder.method(requestMethod);

        setRequestPath();
        requestOriginBuilder.pathAddRear(requestPath);

        requestOriginBuilder.hostAddFront(bucket);

        setRequestQueryParams();
        if(requestQueryParams.size() > 0){
            for(Map.Entry<String,String> entry : requestQueryParams.entrySet())
                requestOriginBuilder.query(entry.getKey(),entry.getValue());
        }

        if(requestHeaders.size() > 0){
            for(Map.Entry<String,String> entry : requestHeaders.entrySet())
                requestOriginBuilder.header(entry.getKey(),entry.getValue());
        }

        requestBodySerializer = new RequestXmlBodySerializer(delete);

        requestActions.add(new QCloudBodyMd5Action(this));

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(DeleteMultiObjectResult.class);
    }

    @Override
    protected void setRequestQueryParams() {
        requestQueryParams.put("delete",null);
    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.POST;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    /**
     *  True means Quiet mode is enabled, and False means Verbose mode is enabled. The default is False.
     * @param quiet
     */
    public void setQuiet(boolean quiet) {
        delete.quiet = quiet;
    }

    /**
     * Target object, contain absolute path.
     * @param object
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
     * Target object, contain absolute path.
     * @param objectList
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

    public Delete getDelete() {
        return delete;
    }

}

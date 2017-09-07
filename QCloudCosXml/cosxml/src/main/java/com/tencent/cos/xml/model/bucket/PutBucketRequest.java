package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.request.serializer.body.RequestByteArraySerializer;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 * Put Bucket request is used to create a Bucket under specified account.
 */
public class PutBucketRequest extends CosXmlRequest {
    public PutBucketRequest(){
        contentType = RequestContentType.X_WWW_FORM_URLENCODE;
        requestHeaders.put(RequestHeader.CONTENT_TYPE,contentType);
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

        requestBodySerializer = new RequestByteArraySerializer(new byte[0],"text/plain");

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(PutBucketResult.class);
    }

    @Override
    protected void setRequestQueryParams() {

    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.PUT;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }


    public void setXCOSACL(String xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL);
        }
    }

    public void setXCOSACL(COSACL xCOSACL){
        if(xCOSACL != null){
            requestHeaders.put("x-cos-acl",xCOSACL.getACL());
        }
    }

    public void setXCOSGrantReadWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 0);
    }

    public void setXCOSGrantRead(List<String> idList){
        setXCOSGrant(idList, 0, 0);
    }

    public void setXCOSGrantWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 1);
    }

    public void setXCOSGrantWrite(List<String> idList){
        setXCOSGrant(idList, 0, 1);
    }

    public void setXCOSReadWriteWithUIN(List<String> uinList){
        setXCOSGrant(uinList, 1, 2);
    }

    public void setXCOSReadWrite(List<String> idList){
        setXCOSGrant(idList, 0, 2);
    }

    private String getXCOSGrantForId(List<String> idList){
        if(idList != null){
            int size = idList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size -1; ++ i){
                    stringBuilder.append("id=\"qcs::cam::")
                            .append(idList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("id=\"qcs::cam::")
                        .append(idList.get(size -1)).append("\"");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    private String getXCOSGrantForUIN(List<String> uinList){
        if(uinList != null){
            int size = uinList.size();
            if(size > 0){
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i < size - 1; ++ i){
                    stringBuilder.append("uin=")
                            .append("\"").append(uinList.get(i)).append("\"")
                            .append(",");
                }
                stringBuilder.append("uin=")
                        .append("\"").append(uinList.get(size - 1)).append("\"");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    /**
     *
     * @param list ==> account list
     * @param idType ==> uin (old, 0) or id (new, 1)
     * @param grantType ==> x-cos-grant-read(0), x-cos-grant-write(1), x-cos-grant-full-control(2)
     */
    private void setXCOSGrant(List<String> list, int idType, int grantType){
        if(list != null){
            String grantMsg = null;
            if(idType == 0){
                grantMsg = getXCOSGrantForUIN(list);
            }else if(idType == 1){
                grantMsg = getXCOSGrantForId(list);
            }
            switch (grantType){
                case 0:
                    requestHeaders.put("x-cos-grant-read", grantMsg);
                    break;
                case 1:
                    requestHeaders.put("x-cos-grant-write", grantMsg);
                    break;
                case 2:
                    requestHeaders.put("x-cos-grant-full-control", grantMsg);
                    break;
            }
        }
    }
}

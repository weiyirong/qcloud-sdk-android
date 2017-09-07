package com.tencent.cos.xml.model.bucket;

import com.tencent.cos.xml.common.RequestContentType;
import com.tencent.cos.xml.common.RequestHeader;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.exception.QCloudExceptionType;
import com.tencent.qcloud.network.response.serializer.body.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.network.response.serializer.http.HttpPassAllSerializer;

import java.util.Map;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 * Get Bucket request is identical to List Object request.
 * It is used to list partial or all of the Objects under the Bucket.
 * Read permission is required to initiate this request.
 */
public class GetBucketRequest extends CosXmlRequest {
    /** Prefix match, used to specify the prefix address of the returned file */
    private String prefix = null;

    /** Delimiter is a sign，
     *  If Prefix exists, the same paths between Prefix and delimiter will be grouped as the same
     *  type and defined Common Prefix, then all Common Prefixes will be listed.
     *  If Prefix doesn't exist, the listing process will start from the beginning of the path
     */
    private String delimiter = null;

    /** Specify the encoding method of the returned value */
    private String encodingType;

    /** Entries are listed using UTF-8 binary order by default, starting from the marker */
    private String marker = null;

    /** Max number of entries returned each time, default is 1000 */
    private String maxKeys = "1000";

    public GetBucketRequest(){
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

        responseSerializer = new HttpPassAllSerializer();
        responseBodySerializer = new ResponseXmlS3BodySerializer(GetBucketResult.class);

    }

    @Override
    protected void setRequestQueryParams() {
        if(prefix != null){
            requestQueryParams.put("prefix",prefix);
        }
        if(delimiter != null){
            requestQueryParams.put("delimiter",delimiter);
        }
        if(encodingType != null){
            requestQueryParams.put("encoding-type",encodingType);
        }
        if(marker != null){
            requestQueryParams.put("marker",marker);
        }
        if(maxKeys != null){
            requestQueryParams.put("max-keys",maxKeys);
        }
        if(prefix != null){
            requestQueryParams.put("prefix",prefix);
        }
    }

    @Override
    public void checkParameters() throws QCloudException {
        if(bucket == null){
            throw new QCloudException(QCloudExceptionType.REQUEST_PARAMETER_INCORRECT, "bucket must not be null");
        }
    }

    @Override
    protected void setRequestMethod() {
        requestMethod = RequestMethod.GET;
    }

    @Override
    protected void setRequestPath() {
        requestPath = "/";
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = String.valueOf(delimiter);
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return marker;
    }

    public void setMaxKeys(long maxKeys) {
        this.maxKeys = String.valueOf(maxKeys);
    }

    public long getMaxKeys() {
        return Long.parseLong(maxKeys);
    }
}

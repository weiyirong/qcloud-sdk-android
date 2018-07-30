package com.tencent.cos.xml.model.object;

import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.common.COSStorageClass;
import com.tencent.cos.xml.common.RequestMethod;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.transfer.ExRequestBodySerializer;
import com.tencent.cos.xml.utils.DateUtils;
import com.tencent.cos.xml.utils.DigestUtils;
import com.tencent.cos.xml.utils.GenerateGetObjectURLUtils;
import com.tencent.qcloud.core.auth.QCloudSignSourceProvider;
import com.tencent.qcloud.core.http.RequestBodySerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bradyxiao on 2018/6/11.
 */

public class PostObjectRequest extends ObjectRequest {

    private FormStruct formStruct = new FormStruct();
    private CosXmlProgressListener progressListener;
    private long offset = -1;
    private long contentLength = -1;

    private PostObjectRequest(String bucket, String cosPath) {
        super(bucket, "/");
        formStruct.key = cosPath;
    }

    public PostObjectRequest(String bucket, String cosPath, String srcPath){
        this(bucket, cosPath);
        formStruct.srcPath = srcPath;
    }

    public PostObjectRequest(String bucket, String cosPath, byte[] data){
        this(bucket, cosPath);
        formStruct.data = data;
    }

    public PostObjectRequest(String bucket, String cosPath, InputStream inputStream){
        this(bucket, cosPath);
        formStruct.inputStream = inputStream;
    }

    public void setRange(long offset, long contentSize){
        this.offset = offset;
        this.contentLength = contentSize;
    }

    @Override
    public String getMethod() {
        return RequestMethod.POST;
    }

    @Override
    public RequestBodySerializer getRequestBody() throws CosXmlClientException {
        if(formStruct.srcPath != null){
            return ExRequestBodySerializer.multipart(formStruct.getFormParameters(), new File(formStruct.srcPath), offset, contentLength);
        }else if(formStruct.data != null){
            return ExRequestBodySerializer.multipart(formStruct.getFormParameters(), null, formStruct.data, offset, contentLength);
        }else if(formStruct.inputStream != null){
            try {
                File tmpFile = new File(CosXmlSimpleService.appCachePath, String.valueOf(System.currentTimeMillis()));
                if(!tmpFile.exists()){
                    tmpFile.createNewFile();
                }
                return ExRequestBodySerializer.multipart(formStruct.getFormParameters(), tmpFile, formStruct.inputStream, offset, contentLength);
            } catch (IOException e) {
                throw new CosXmlClientException(e);
            }
        }
        return null;
    }


    @Override
    public void checkParameters() throws CosXmlClientException {
        super.checkParameters();
        if(formStruct.key == null){
            throw new CosXmlClientException("cosPath must not be null ");
        }
        formStruct.signature.checkParameters();
    }

    /**
     * 上传进度回调
     * @param progressListener 进度监听器 {@link CosXmlProgressListener}
     */
    public void setProgressListener(CosXmlProgressListener progressListener){
        this.progressListener = progressListener;
    }

    public CosXmlProgressListener getProgressListener(){
        return progressListener;
    }

    public void setSecretIdAndKey(String secretId, String signKey, String keyTime){
        formStruct.signature.secretId = secretId;
        formStruct.signature.signKey = signKey;
        formStruct.signature.keyTime = keyTime;
    }

    @Override
    public void setSign(long signDuration) {
       long startTime = System.currentTimeMillis() / 1000;
       setSign(startTime, startTime + signDuration);
    }

    @Override
    public void setSign(long startTime, long endTime) {
        formStruct.signature.signTime = startTime + ";" + endTime;
    }

    public void setSign(long signDuration, Map<String, String> parameters,  Map<String, String> headers) {
        long startTime = System.currentTimeMillis() / 1000;
        setSign(startTime, startTime + signDuration, parameters, headers);
    }

    public void setSign(long startTime, long endTime, Map<String, String> parameters,  Map<String, String> headers) {
       formStruct.signature.signTime = startTime + ";" + endTime;
       formStruct.signature.parameters = parameters;
       formStruct.signature.headers = headers;
    }

    @Override
    public void setSign(long signDuration, Set<String> parameters, Set<String> headers) {
       throw new IllegalArgumentException("need to invoke setSign(long, Map<String, String>,  Map<String, String>) or setSign(long)");
    }

    @Override
    public void setSign(long startTime, long endTime, Set<String> parameters, Set<String> headers) {
        throw new IllegalArgumentException("need to invoke setSign(long, long, Map<String, String>,  Map<String, String>)or setSign(long, long)");
    }

    public void setAcl(String acl){
        formStruct.acl = acl;
    }

    public void setCacheControl(String cacheControl){
        formStruct.headers.put("Cache-Control", cacheControl);
    }

    public void setContentType(String contentType){
        formStruct.headers.put("Content-Type", contentType);
    }

    public void setContentDisposition(String contentDisposition){
        formStruct.headers.put("Content-Disposition", contentDisposition);
    }

    public void setContentEncoding(String contentEncoding){
        formStruct.headers.put("Content-Encoding", contentEncoding);
    }

    public void setExpires(String expires){
        formStruct.headers.put("Expires", expires);
    }

    public void setHeader(String key, String value){
        if(key != null && value != null){
            formStruct.headers.put(key, value);
        }
    }

    public void setCustomerHeader(String key, String value){
        if(key != null && value != null){
            formStruct.customHeaders.put(key, value);
        }
    }

    public void setCosStorageClass(String cosStorageClass){
        formStruct.xCosStorageClass = cosStorageClass;
    }

    /**
     * the host you want to redirect
     * @param redirectHost
     */
    public void setSuccessActionRedirect(String redirectHost){
        formStruct.successActionRedirect = redirectHost;
    }

    /**
     * successHttpCode can be 200, 201, 204, default value 204
     * @param successHttpCode
     */
    public void setSuccessActionStatus(int successHttpCode){
        formStruct.successActionStatus = String.valueOf(successHttpCode);
    }

    public void setPolicy(Policy policy){
        formStruct.policy = policy;
    }


    private class FormStruct{
        String acl;
        Map<String, String> headers;
        String key;
        String successActionRedirect;
        String successActionStatus;
        Map<String, String> customHeaders;
        String xCosStorageClass;
        Policy policy;
        Signature signature;
        String srcPath;
        byte[] data;
        InputStream inputStream;

        public FormStruct(){
            headers = new LinkedHashMap<>();
            customHeaders = new LinkedHashMap<>();
            signature = new Signature();
        }

        public Map<String, String> getFormParameters() throws CosXmlClientException {
            Map<String, String> formParameters = new LinkedHashMap<>();
            if(acl != null){
                formParameters.put("Acl", acl);
            }
            for(Map.Entry<String, String> entry : headers.entrySet()){
                formParameters.put(entry.getKey(), entry.getValue());
            }
            formParameters.put("key", key);
            if(successActionRedirect != null){
                formParameters.put("success_action_redirect", successActionRedirect);
            }
            if(successActionStatus != null){
                formParameters.put("success_action_status", successActionStatus);
            }
            for(Map.Entry<String, String> entry : customHeaders.entrySet()){
                formParameters.put(entry.getKey(), entry.getValue());
            }
            if(xCosStorageClass != null){
                formParameters.put("x-cos-storage-class", xCosStorageClass);
            }
            formParameters.put("Signature", signature.getSign());
            if(policy != null){
                formParameters.put("policy", DigestUtils.getBase64(policy.content()));
            }
            return formParameters;
        }
    }

    /**
     * UT
     */
    public Map<String, String> testFormParameters() throws CosXmlClientException {
        return formStruct.getFormParameters();
    }

    private class Signature{
        public Map<String, String> parameters;
        public Map<String, String> headers;
        public String signTime;
        public String secretId, signKey;
        public String keyTime;

        public Signature(){
            long startTime = System.currentTimeMillis() / 1000;
            signTime = startTime + ";" + (startTime + 600);
        }

        public void checkParameters() throws CosXmlClientException {
            if(secretId == null || signKey== null){
                throw new CosXmlClientException("secretId or secretKey must not be null");
            }
        }

        public String getSign() throws CosXmlClientException {
            return GenerateGetObjectURLUtils.getSign(RequestMethod.POST, "/", headers, parameters, signTime, keyTime, secretId, signKey);
        }
    }

    public static class Policy{
        private String expiration;
        private JSONArray conditions = new JSONArray();

        public void setExpiration(long endTimeMills){
            this.expiration = DateUtils.getFormatTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", endTimeMills);
        }

        public void setExpiration(String formatEndTime){
            this.expiration = formatEndTime;
        }

        public void addConditions(String key, String value, boolean isPrefixMatch) throws CosXmlClientException {
            if(isPrefixMatch){
                JSONArray content = new JSONArray();
                content.put("starts-with");
                content.put(key);
                content.put(value);
                this.conditions.put(content);
            }else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(key, value);
                } catch (JSONException e) {
                    throw new CosXmlClientException(e);
                }
                this.conditions.put(jsonObject);
            }
        }

        public void addContentConditions(int start, int end){
            JSONArray content = new JSONArray();
            content.put("content-length-range");
            content.put(start);
            content.put(end);
            this.conditions.put(content);
        }

        public String content() throws CosXmlClientException {
            JSONObject jsonObject = new JSONObject();
            try {
                if(expiration != null){
                    jsonObject.put("expiration", expiration);
                }
                jsonObject.put("conditions", conditions);
                return jsonObject.toString();
            } catch (JSONException e) {
                throw new CosXmlClientException(e);
            }
        }
    }
}

package com.tencent.cos.xml.model.object;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.QService;
import com.tencent.cos.xml.common.COSACL;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.tag.ACLAccount;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bradyxiao on 2017/12/13.
 */

public class ObjectTest extends ApplicationTestCase {
    static final String TAG = "Unit_Test";

    String bucket;
    String cosPath;


    public ObjectTest() {
        super(Application.class);
    }

    public void putObjectTest() throws CosXmlServiceException, CosXmlClientException, IOException {
        QService.context = getContext();
        String srcPath = QService.createFile(1024 * 1024);
        cosPath = "putobject.txt";
        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
                srcPath);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target  + "|" + (long)(100.0 * complete/target));
            }
        });
        PutObjectResult result = QService.getCosXmlClient(getContext()).putObject(request);
        Log.d(TAG, result.printResult() + "\n" + result.accessUrl);

        QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);

        QService.delete(srcPath);
    }

    public void appendObjectTest() throws IOException, CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String srcPath = QService.createFile(1024 * 1024);
        cosPath = "append.txt";
        AppendObjectRequest request = new AppendObjectRequest(bucket, "append.txt",
                srcPath,0);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        AppendObjectResult result = QService.getCosXmlClient(getContext()).appendObject(request);
        Log.d(TAG, result.printResult() + "\n" + result.accessUrl);

        QService.delete(QService.getCosXmlClient(getContext()), bucket, "append.txt");

        QService.delete(srcPath);
    }

    public void headObjectTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "xml.txt";
        HeadObjectRequest request = new HeadObjectRequest(bucket, cosPath);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        request.setRequestHeaders("x-cos-version-id", "MTg0NDY3NDI1NTkyNjc0ODQ1OTA");
        HeadObjectResult result = QService.getCosXmlClient(getContext()).headObject(request);
        Log.d(TAG, result.printResult());
    }

    public void getObjectTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "xml.txt";
        String savePath = getContext().getCacheDir().getPath() + "/";
        GetObjectRequest request = new GetObjectRequest(bucket, cosPath, savePath);
        request.setRspContentDispositon("attachment;filename=xiaoyao.txt");
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        Set<String> sets = new HashSet<>();
        //sets.add("response-content-disposition");
        request.setSign(600, sets, null);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        GetObjectResult result = QService.getCosXmlClient(getContext()).getObject(request);
        Log.d(TAG, result.printResult());

        //QService.delete(request.getDownloadPath());
    }

    public void optionObjectTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "xml.txt";
        String origin = "cloud.tencent.com";
        String method = "GET";
        OptionObjectRequest request = new OptionObjectRequest(bucket, cosPath, origin, method);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        OptionObjectResult result = QService.getCosXmlClient(getContext()).optionObject(request);
        Log.d(TAG, result.printResult());
    }

    public void copyObjectTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "xml_copy.txt";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                QService.appid, bucket, QService.region, "xml.txt");
        CopyObjectRequest request = new CopyObjectRequest(bucket, cosPath, copySourceStruct);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        CopyObjectResult result = QService.getCosXmlClient(getContext()).copyObject(request);
        Log.d(TAG, result.printResult());
        QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
    }

    public void deleteObjectTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "AWS-S3.doc";
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, cosPath);
        request.setVersionId("versiodId");
        DeleteObjectResult result = QService.getCosXmlClient(getContext()).deleteObject(request);
        Log.d(TAG, result.printResult());
    }

    public void deleteMultiObject() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
//        List<String> listObject = new ArrayList<>();
//        listObject.add("/xml_test_copy.txt");
//        listObject.add("/1511858966419.txt");
        Map<String,String> listObject = new HashMap<>();
        listObject.put("/xml_test_copy.txt", "versionIdxxx");

        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest(bucket, null);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        request.setQuiet(false);
        request.setObjectList(listObject);
        request.setObjectList("/1511858966419.txt", "versiondIdxxxxx");
        DeleteMultiObjectResult result = QService.getCosXmlClient(getContext()).deleteMultiObject(request);
        Log.d(TAG, result.printResult());
    }

    public void putObjectACLTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "xml.txt";
        PutObjectACLRequest request = new PutObjectACLRequest(bucket, cosPath);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        request.setXCOSACL(COSACL.PRIVATE);
        ACLAccount aclAccount = new ACLAccount();
        aclAccount.addAccount("1131975903", "1131975903");
        request.setXCOSGrantRead(aclAccount);
        request.setXCOSGrantWrite(aclAccount);
        PutObjectACLResult result = QService.getCosXmlClient(getContext()).putObjectACL(request);
        Log.d(TAG, result.printResult());
    }

    public void getBucketACLTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "xml.txt";
        GetObjectACLRequest request = new GetObjectACLRequest(bucket, cosPath);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        GetObjectACLResult result = QService.getCosXmlClient(getContext()).getObjectACL(request);
        Log.d(TAG, result.printResult());
    }

    public void multiUploadPartObjectTest() throws CosXmlServiceException, CosXmlClientException, IOException {
        QService.context = getContext();
        String cosPath = "xml_multi.txt";
        InitMultipartUploadRequest request = new InitMultipartUploadRequest(bucket, cosPath);
        request.setRequestHeaders("Content-Type", "video/mpeg4");
        InitMultipartUploadResult result = QService.getCosXmlClient(getContext()).initMultipartUpload(request);
        Log.d(TAG, result.printResult());

        int partNumber = 1;
        String uploadId = result.initMultipartUpload.uploadId;
        String srcPath = QService.createFile(1024 * 1024);
        UploadPartRequest request2 = new UploadPartRequest(bucket, cosPath,partNumber, srcPath, uploadId);
        request2.setRequestHeaders("Content-Type", "video/mpeg4");
        request2.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, "complete: " + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        UploadPartResult result2 = QService.getCosXmlClient(getContext()).uploadPart(request2);
        Log.d(TAG, result2.printResult());

        ListPartsRequest request3 = new ListPartsRequest(bucket, cosPath, uploadId);
        request3.setRequestHeaders("Content-Type", "video/mpeg4");
        ListPartsResult result3 = QService.getCosXmlClient(getContext()).listParts(request3);
        Log.d(TAG, result3.printResult());

        CompleteMultiUploadRequest request4 = new CompleteMultiUploadRequest(bucket, cosPath, uploadId,
                null);
        String etag = result2.eTag;
        request4.setRequestHeaders("Content-Type", "video/mpeg4");
        request4.setPartNumberAndETag(partNumber, etag);
        CompleteMultiUploadResult result4 = QService.getCosXmlClient(getContext()).completeMultiUpload(request4);
        Log.d(TAG, result4.printResult());

        QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
        QService.delete(srcPath);

    }

    public void abortMultiUploadPartObjectTest() throws CosXmlServiceException, CosXmlClientException {
        QService.context = getContext();
        String cosPath = "xml_test_multi2.txt";
        InitMultipartUploadRequest request = new InitMultipartUploadRequest(bucket, cosPath);
        InitMultipartUploadResult result = QService.getCosXmlClient(getContext()).initMultipartUpload(request);
        Log.d(TAG, result.printResult());

        int partNumber = 1;
        String uploadId = result.initMultipartUpload.uploadId;
        byte[] data = new byte[1024 * 1024];
        UploadPartRequest request2 = new UploadPartRequest(bucket, cosPath,partNumber, data, uploadId);
        request2.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, "complete: " + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        UploadPartResult result2 = QService.getCosXmlClient(getContext()).uploadPart(request2);
        Log.d(TAG, result2.printResult());

        ListPartsRequest request3 = new ListPartsRequest(bucket, cosPath, uploadId);
        ListPartsResult result3 = QService.getCosXmlClient(getContext()).listParts(request3);
        Log.d(TAG, result3.printResult());

        AbortMultiUploadRequest request4 = new AbortMultiUploadRequest(bucket, cosPath, uploadId);
        request4.setRequestHeaders("Content-Type", "video/mpeg4");
        AbortMultiUploadResult result4 = QService.getCosXmlClient(getContext()).abortMultiUpload(request4);
        Log.d(TAG, result4.printResult());
    }



    @Test
    public void test() throws CosXmlServiceException, CosXmlClientException, IOException {
        bucket = "androidtest";
//        putObjectTest();
//        appendObjectTest();
//        headObjectTest();
//        getObjectTest();
//        optionObjectTest();
//        copyObjectTest();
//        deleteObjectTest();
        deleteMultiObject();
//        putObjectACLTest();
//        getBucketACLTest();
//        multiUploadPartObjectTest();
//        abortMultiUploadPartObjectTest();

        getContext().getCacheDir().length();
    }

}

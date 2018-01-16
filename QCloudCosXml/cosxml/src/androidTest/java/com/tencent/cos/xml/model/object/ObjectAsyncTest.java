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
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.ACLAccount;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by bradyxiao on 2017/12/13.
 */

public class ObjectAsyncTest extends ApplicationTestCase {
    static final String TAG = "Unit_Test";

    volatile boolean isOver = false;
    String bucket;
    public ObjectAsyncTest() {
        super(Application.class);
    }

    @Test
    public void test() throws IOException, CosXmlClientException {
        bucket = "androidtest";
        putObjectTest();
        putObjectTest2();
        appendObjectTest();
        appendObjectTest2();
        headObjectTest();
        getObjectTest();
        optionObjectTest();
        copyObjectTest();
        deleteObjectTest();
        deleteMultiObject();
        putObjectACLTest();
        getBucketACLTest();
        multiUploadPartObjectTest();
        abortMultiUploadPartObjectTest();
    }

    private void abortMultiUploadPartObjectTest() throws IOException {
        isOver = false;
        initMultipart();
        uploadPart(1);
        uploadPart2(2);
        listUploadPart();
        abrotMultiPart();
    }

    private void abrotMultiPart(){
        isOver = false;
        String cosPath = "xml_multi.txt";
        AbortMultiUploadRequest request = new AbortMultiUploadRequest(bucket, cosPath, uploadId);
        QService.getCosXmlClient(getContext()).abortMultiUploadAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    String uploadId;
    Map<Integer, String> partAndEtag = new LinkedHashMap<>();

    private void multiUploadPartObjectTest() throws IOException, CosXmlClientException {
        isOver = false;
        initMultipart();
        uploadPart(1);
        uploadPart2(2);
        uploadPart3(3);
        uploadPart4(4);
        listUploadPart();
        completeMultiUpload();
    }

    private void completeMultiUpload(){
        isOver = false;
        final String cosPath = "xml_multi.txt";
        CompleteMultiUploadRequest request = new CompleteMultiUploadRequest(bucket, cosPath, uploadId,
                partAndEtag);
        QService.getCosXmlClient(getContext()).completeMultiUploadAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
                try {
                    QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        partAndEtag.clear();
    }

    private void listUploadPart(){
        isOver = false;
        final String cosPath = "xml_multi.txt";
        ListPartsRequest request = new ListPartsRequest(bucket, cosPath, uploadId);
        QService.getCosXmlClient(getContext()).listPartsAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPart4(final int partNumber) throws IOException {
        isOver = false;
        final String cosPath = "xml_multi.txt";
        String srcPath = QService.createFile(1024 * 1024*2);
        UploadPartRequest request = new UploadPartRequest(bucket,cosPath ,partNumber, srcPath, 100, 1024*1025, uploadId);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, "complete: " + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).uploadPartAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                partAndEtag.put(partNumber, ((UploadPartResult)result).eTag);
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadPart3(final int partNumber) throws IOException, CosXmlClientException {
        isOver = false;
        final String cosPath = "xml_multi.txt";
        String srcPath = QService.createFile(1024 * 1024);
        UploadPartRequest request = new UploadPartRequest(bucket,cosPath ,partNumber, new FileInputStream(srcPath), uploadId);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, "complete: " + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).uploadPartAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                partAndEtag.put(partNumber, ((UploadPartResult)result).eTag);
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadPart2(final int partNumber) throws IOException {
        isOver = false;
        final String cosPath = "xml_multi.txt";
        byte[] data = new byte[1024 * 1024];
        UploadPartRequest request = new UploadPartRequest(bucket,cosPath ,partNumber, data, uploadId);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, "complete: " + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).uploadPartAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                partAndEtag.put(partNumber, ((UploadPartResult)result).eTag);
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadPart(final int partNumber) throws IOException {
        isOver = false;
        final String cosPath = "xml_multi.txt";
        String srcPath = QService.createFile(1024 * 1024);
        UploadPartRequest request = new UploadPartRequest(bucket,cosPath ,partNumber, srcPath, uploadId);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, "complete: " + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).uploadPartAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                partAndEtag.put(partNumber, ((UploadPartResult)result).eTag);
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        QService.delete(srcPath);

    }

    private void initMultipart(){
        isOver = false;
        String cosPath = "xml_multi.txt";
        InitMultipartUploadRequest request = new InitMultipartUploadRequest(bucket, cosPath);
        QService.getCosXmlClient(getContext()).initMultipartUploadAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                uploadId = ((InitMultipartUploadResult)result).initMultipartUpload.uploadId;
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void getBucketACLTest() {
        isOver = false;
        String cosPath = "xml.txt";
        GetObjectACLRequest request = new GetObjectACLRequest(bucket, cosPath);
        QService.getCosXmlClient(getContext()).getObjectACLAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void putObjectACLTest() {
        isOver = false;
        String cosPath = "xml.txt";
        PutObjectACLRequest request = new PutObjectACLRequest(bucket, cosPath);
        request.setXCOSACL(COSACL.PRIVATE);
        ACLAccount aclAccount = new ACLAccount();
        aclAccount.addAccount("1131975903", "1131975903");
        request.setXCOSGrantRead(aclAccount);
        request.setXCOSGrantWrite(aclAccount);
        QService.getCosXmlClient(getContext()).putObjectACLAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteMultiObject() {
        isOver = false;
        List<String> listObject = new ArrayList<>();
        listObject.add("/xml_test_copy.txt");
        listObject.add("/1511858966419.txt");
        DeleteMultiObjectRequest request = new DeleteMultiObjectRequest(bucket, null);
        request.setQuiet(false);
        request.setObjectList(listObject);
        QService.getCosXmlClient(getContext()).deleteMultiObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void deleteObjectTest() {
        isOver = false;
        String cosPath = "xml_test.txt";
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, cosPath);
        QService.getCosXmlClient(getContext()).deleteObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyObjectTest() throws CosXmlClientException {
        isOver = false;
        final String cosPath = "xml_copy_中国putobject.txt";
        CopyObjectRequest.CopySourceStruct copySourceStruct = new CopyObjectRequest.CopySourceStruct(
                QService.appid, bucket, QService.region, "中国putobject.txt");
        CopyObjectRequest request = new CopyObjectRequest(bucket, cosPath, copySourceStruct);
        QService.getCosXmlClient(getContext()).copyObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
//                try {
//                    QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
//                } catch (CosXmlServiceException e) {
//                    e.printStackTrace();
//                } catch (CosXmlClientException e) {
//                    e.printStackTrace();
//                }
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void optionObjectTest() {
        isOver =false;
        String cosPath =  "xml.txt";
        String origin = "cloud.tencent.com";
        String method = "GET";
        OptionObjectRequest request = new OptionObjectRequest(bucket, cosPath, origin, method);
        QService.getCosXmlClient(getContext()).optionObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getObjectTest() {
        isOver =false;
        String cosPath = "xml.txt";
        String savePath = Environment.getExternalStorageDirectory().getPath() + "/";
        GetObjectRequest request = new GetObjectRequest(bucket, cosPath, savePath);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).getObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                QService.delete(((GetObjectRequest)request).getDownloadPath());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void headObjectTest() {
        isOver =false;
        String cosPath = "xml.txt";
        HeadObjectRequest request = new HeadObjectRequest(bucket, cosPath);
        QService.getCosXmlClient(getContext()).headObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendObjectTest2() {
        isOver = false;
        byte[] data = new byte[1024 * 1024];
        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucket, "append.txt",
                data,0);
        appendObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target);
            }
        });
        QService.getCosXmlClient(getContext()).appendObjectAsync(appendObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                try {
                    QService.delete(QService.getCosXmlClient(getContext()), bucket, "append.txt");
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendObjectTest() throws IOException {
        isOver = false;
        String srcPath = QService.createFile(1024 * 1024);
        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucket, "append.txt",
                srcPath,0);
        appendObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).appendObjectAsync(appendObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                try {
                    QService.delete(QService.getCosXmlClient(getContext()), bucket, "append.txt");
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        QService.delete(srcPath);
    }

    private void putObjectTest2() {
        isOver = false;
        byte[] data = new byte[1024 * 1024];
        final String cosPath = "putobject.txt";
        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
                data);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).putObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                try {
                    QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void putObjectTest() throws IOException {
        isOver = false;
        String srcPath = QService.createFile(1024 * 1024);
        final String cosPath = "putobject.txt";
        PutObjectRequest request = new PutObjectRequest(bucket, cosPath,
                srcPath);
        request.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Log.d(TAG, " complete：" + complete + "| target: " + target + "|" + (long)(100.0 * complete/target));
            }
        });
        QService.getCosXmlClient(getContext()).putObjectAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printResult());
                try {
                    QService.delete(QService.getCosXmlClient(getContext()), bucket, cosPath);
                } catch (CosXmlServiceException e) {
                    e.printStackTrace();
                } catch (CosXmlClientException e) {
                    e.printStackTrace();
                }
                isOver = true;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d(TAG, exception == null ? serviceException.getMessage() : exception.toString());
                isOver = true;
            }
        });

        while (!isOver){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        QService.delete(srcPath);
    }
}

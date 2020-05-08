package com.tencent.qcloud.quic;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nullable;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class QuicTest {

    QuicManager quicManager;


    protected synchronized void init(){

        Context appContext = InstrumentationRegistry.getTargetContext();
        if(quicManager == null){

            quicManager = new QuicManager();

            quicManager.init(true, null, null, null);
        }

    }

    @Test
    public void testGet() throws Exception{

        init();

        String host = "stgwhttp2.kof.qq.com";
        String ip = "";
        int port = 443;
        int tcpPort = 80;
        try {
            InetAddress[] inetAddresses = InetAddress.getAllByName(host);
            if(inetAddresses != null && inetAddresses.length > 0){
                ip = inetAddresses[0].getHostAddress();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        QuicRequest quicRequest = new QuicRequest(host, ip, port, tcpPort);
        quicRequest.addHeader(":scheme", "https");
        quicRequest.addHeader(":path",  "/1.jpg");
        quicRequest.addHeader(":method", "GET");
        quicRequest.setRequestBody(new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(new byte[0]);
               // sink.close();
            }
        });

        try {
            QuicImpl quic = quicManager.newQuicImpl(quicRequest);
            QuicResponse quicResponse = quic.call();
            if(quicResponse.buffer != null){
                String fileName = "response-" + System.currentTimeMillis() + ".jpg";
                FileOutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + fileName);
                byte[] data = new byte[4 * 1024];
                int len = quicResponse.buffer.read(data, 0, data.length);
                while (len != -1){
                    outputStream.write(data, 0, len);
                    len = quicResponse.buffer.read(data, 0, data.length);
                }
                outputStream.flush();
                outputStream.close();

            }


            for(Map.Entry<String, String> header : quicResponse.headers.entrySet()){
                Log.d("XIAO", "(" + header.getKey() + "|" + header.getValue() + ")");
            }
        } catch (QuicException e) {
            throw e;
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    @Test
    public void testOutputStream() throws Exception{

        MyOutputStream myOutputStream =new MyOutputStream();
        myOutputStream.write(200);



    }

    class MyOutputStream extends OutputStream{
        FileOutputStream fileOutputStream;
        public MyOutputStream() throws FileNotFoundException {
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Test/outputstream1.txt");

        }


        @Override
        public void write(int b) throws IOException {
            fileOutputStream.write(b);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }
}

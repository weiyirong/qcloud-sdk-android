package com.tencent.qcloud.quic;

import android.os.*;
import android.support.annotation.Nullable;
import com.tencent.qcloud.core.http.CallMetricsListener;
import okio.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.ProtocolException;
import java.util.Map;

import static com.tencent.qcloud.quic.QuicNative.*;

public class QuicImpl implements NetworkCallback, java.util.concurrent.Callable<QuicResponse>{

    private QuicRequest quicRequest;
    private QuicResponse quicResponse;
    private QuicException exception;

    private ConnectPool connectPool;
    private QuicNative realQuicCall;

    private Looper looper;
    private @Nullable Handler handler;

    private boolean isHeader;

    //真正取消：response 接收完 + onRequestCompleted 或者 onClose
    //需要排除: response 未接收，onRequestCompleted 就发生了的情况
    //一旦 response 获取了，则任务可表示结束了
    private volatile boolean isOver = false;
    private boolean isCompleted = false;
    private boolean hasReceivedResponse = false;
    private boolean isClosed = false;

    private CallMetricsListener callMetricsListener;

    public QuicImpl(QuicRequest quicRequest, ConnectPool connectPool){
        this.quicRequest = quicRequest;
        this.connectPool = connectPool;
        this.quicResponse = new QuicResponse();
    }

    public void setCallMetricsListener(CallMetricsListener callMetricsListener){
        this.callMetricsListener = callMetricsListener;
    }

    public void setOutputDestination(OutputStream outputStream){
        this.quicResponse.setOutputStream(outputStream);
    }

    public void setProgressCallback(ProgressCallback progress){
        this.quicResponse.setProgressCallback(progress);
    }

    /**
     * 成功建立链接
     * @param handleId
     * @param code
     */
    @Override
    public void onConnect(int handleId, int code) {
        handler.sendEmptyMessage(CONNECTED);
    }

    /**
     * 接收响应包
     * @param handleId
     * @param data
     * @param len
     */
    @Override
    public void onDataReceive(int handleId, byte[] data, int len) {
        Message message = handler.obtainMessage();
        message.what = RECEIVING;
        Bundle bundle = new Bundle();
        bundle.putByteArray("DATA", data);
        bundle.putInt("LEN", len);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    /**
     * 响应包已接收完
     * @param handleId
     */
    @Override
    public void onCompleted(int handleId) {
        handler.sendEmptyMessage(COMPLETED);
    }

    /**
     * 底层链接 close
     * @param handleId
     * @param code
     * @param desc
     */
    @Override
    public void onClose(int handleId, int code, String desc) {
        QLog.d("has been completed : %s", isOver);
        if(isOver)return; //cancel by user
        Message message = handler.obtainMessage();
        message.what = SERVER_FAILED;
        QuicException quicException = new QuicException(String.format("Closed(%d, %s)", code, desc));
        message.obj = quicException;
        handler.sendMessage(message);
    }

    /**
     * 建立链接
     */
    private void startConnect(){
        callMetricsListener.connectStart(null, null, null);
        realQuicCall.connect(quicRequest.host, quicRequest.ip, quicRequest.port, quicRequest.tcpPort);
    }

    /**
     * 发送数据
     */
    private void sendData(){
        callMetricsListener.connectEnd(null, null, null, null);
        try {
            callMetricsListener.requestHeadersStart(null);
            //发送 header
            for(Map.Entry<String, String> header : quicRequest.headers.entrySet()){
                realQuicCall.addHeader(header.getKey(), header.getValue());
            }
            callMetricsListener.requestHeadersEnd(null, null);
            callMetricsListener.requestBodyStart(null);
            //发送data
            if(quicRequest.requestBody != null){
                QuicOutputStream quicOutputStream = new QuicOutputStream(realQuicCall);
                BufferedSink bufferedSink = Okio.buffer(Okio.sink(quicOutputStream));
                quicRequest.requestBody.writeTo(bufferedSink);
                try {
                    bufferedSink.flush();
                    bufferedSink.close();
                }catch (Exception e){}
                realQuicCall.sendRequest(new byte[0], 0, true);
            }else {
                realQuicCall.sendRequest(new byte[0], 0, true);
            }
            callMetricsListener.requestBodyEnd(null, -1L);
            callMetricsListener.responseHeadersStart(null);
        }catch (IOException e){
            //失败
            Message message = handler.obtainMessage();
            message.what = CLIENT_FAILED;
            message.obj = new QuicException(e);
            handler.sendMessage(message);
        }
    }

    private void receiveResponse(byte[] data, int len){
        if(isHeader){
            parseResponseHeader(data, len);
            callMetricsListener.responseHeadersEnd(null, null);
            isHeader = false;
            callMetricsListener.responseBodyStart(null);
        }else {
            parseBody(data, len);
            callMetricsListener.responseBodyEnd(null, -1L);
        }
        hasReceivedResponse = true;
    }

    /**
     * 解析头部字段
     * @param data
     * @param len
     * @return
     */
    private void parseResponseHeader(byte[] data, int len){
        try {
            // from simbachen tests
            String firtPakcet = new String(data, "ISO-8859-1");
            QLog.d("headers==>%s", firtPakcet);
            //QLog.d("headers==>%s", new String(data, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        if(quicResponse != null){
            ByteArrayInputStream byteArrayInputStream = null;
            BufferedReader bufferedReader = null;
            try {
                byteArrayInputStream = new ByteArrayInputStream(data, 0, len);
                bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
                String line = bufferedReader.readLine();
                while (line != null){
                    if(line.startsWith("HTTP/1.")){ // HTTP/1.1 200 OK
                        parseStateLine(line);
                    }else if(line.contains(":")){ // date: Wed, 13 Mar 2019 13:14:51 GMT
                        int index = line.indexOf(":");
                        String key = line.substring(0, index).trim();
                        String value = line.substring(index + 1).trim();
                        quicResponse.headers.put(key, value);
                        if(key.equalsIgnoreCase("content-length")){
                            quicResponse.setContentLength(Long.valueOf(value));
                        }else if(key.equalsIgnoreCase("content-type")){
                            quicResponse.contentType = value;
                        }

                    }else if(line.length() == 0){ // "\r\n"
                        //from simbachen tests
                        String bodyString = bufferedReader.readLine();
                        if( bodyString != null){
                            QLog.d("parseResponseHeader we reach the body data");
                            byte[] bodyByte = bodyString.getBytes("ISO-8859-1");
                            parseBody(bodyByte, bodyByte.length);
                        }else {
                            break;
                        }
                    }
                    line = bufferedReader.readLine();
                }
            }catch (IOException e){
                // error
                Message message = handler.obtainMessage();
                message.what = CLIENT_FAILED;
                message.obj = new QuicException(e);
                handler.sendMessage(message);
            }
            finally {
                if(bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(byteArrayInputStream != null){
                    try {
                        byteArrayInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // H T T P / 1 . 1   2 0 0   T e m p o r a r y   R e d i r e c t
    // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0
    private void parseStateLine(String statusLine) throws ProtocolException {

        int codeStart;
        if (statusLine.startsWith("HTTP/1.")) {
            if (statusLine.length() < 9 || statusLine.charAt(8) != ' ') {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
            int httpMinorVersion = statusLine.charAt(7) - '0';
            codeStart = 9;

            if (httpMinorVersion == 0) {
               QLog.d("HTTP/1.0");
            } else if (httpMinorVersion == 1) {
                QLog.d("HTTP/1.1");
            } else {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
        } else if (statusLine.startsWith("ICY ")) {
            // Shoutcast uses ICY instead of "HTTP/1.0".
            QLog.d("HTTP/1.0");
            codeStart = 4;
        } else {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }

        // Parse response code like "200". Always 3 digits.
        if (statusLine.length() < codeStart + 3) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }
        int code;
        try {
            code = Integer.parseInt(statusLine.substring(codeStart, codeStart + 3));
        } catch (NumberFormatException e) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }

        // Parse an optional response message like "OK" or "Not Modified". If it
        // exists, it is separated from the response code by a space.
        String message = "";
        if (statusLine.length() > codeStart + 3) {
            if (statusLine.charAt(codeStart + 3) != ' ') {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
            message = statusLine.substring(codeStart + 4);
        }
        quicResponse.code = code;
        quicResponse.message = message;
    }

    /**
     * 接收body
     * @param data
     * @param len
     */
    private void parseBody(byte[] data, int len){
        try {
            if(quicResponse.code >= 200 && quicResponse.code < 300 && quicResponse.fileSink != null){
                quicResponse.fileSink.write(data, 0, len);
                quicResponse.updateProgress(len);
            }else {
                quicResponse.buffer.write(data, 0, len);
                quicResponse.currentLength += len;
            }
        } catch (Exception e) {
            //失败
            Message message = handler.obtainMessage();
            message.what = CLIENT_FAILED;
            message.obj = new QuicException(e);
            handler.sendMessage(message);
        }

    }

    /**
     * 请求-响应 结束
     */
    private void finish(){
        try {
            if(quicResponse.code >= 200 && quicResponse.code < 300){
                if(quicResponse.fileSink != null){
                    quicResponse.fileSink.flush();
                    quicResponse.fileSink.close();
                }
            }else {
                quicResponse.buffer.flush();
            }
        } catch (Exception e) {
            //失败
            Message message = handler.obtainMessage();
            message.what = CLIENT_FAILED;
            message.obj = new QuicException(e);
            handler.sendMessage(message);
        }

        QLog.d("quic net info: %s", realQuicCall.getState());
    }

    /**
     * 取消链接
     */
    public void cancelConnect(){
        realQuicCall.cancelRequest();
    }

    /**
     * 清除资源
     */
    public void clear(){
        realQuicCall.clear();
    }

    @Override
    public QuicResponse call() throws QuicException {

        QLog.d("start get a connect: ");

        //获取connect： quicNative
        realQuicCall = connectPool.getQuicNative(quicRequest.host, quicRequest.ip, quicRequest.port, quicRequest.tcpPort);
        realQuicCall.setCallback(this);

        QLog.d("has get a connect: ");

        synchronized (this){
            looper = Looper.myLooper(); //表示当前线程已存在
            if(looper != null){
                notifyAll();
            }
        }
        if(looper == null){
            Looper.prepare(); //开始准备
            QLog.d("looper prepare");
            synchronized (this){
                looper = Looper.myLooper();
                notifyAll();
            }
        }

        //设置消息队列
        try {
            setMessageQueue();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        handler = new Handler(getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case INIT:
                        startConnect();
                        break;
                    case CONNECTED:
                        connectPool.updateQuicNativeState(realQuicCall, CONNECTED);
                        isHeader = true;
                        isOver = false;
                        sendData();
                        break;
                    case RECEIVING:
                        Bundle bundle = (Bundle) msg.obj;
                        receiveResponse(bundle.getByteArray("DATA"), bundle.getInt("LEN"));
                        quitSafely();
                        break;
                    case COMPLETED:
                        isCompleted = true;
                        quitSafely();
                        break;
                    case SERVER_FAILED:
                        connectPool.updateQuicNativeState(realQuicCall, SERVER_FAILED);
                        isClosed = true;
                        exception = (QuicException) msg.obj;
                        quitSafely();
                        break;
                    case CLIENT_FAILED:
                        connectPool.updateQuicNativeState(realQuicCall, CLIENT_FAILED);
                        isClosed = true;
                        exception = (QuicException) msg.obj;
                        quitSafely();
                        break;
                }
            }
        };
        QLog.d("looper loop start");
        if(realQuicCall.currentState == CONNECTED){
            handler.sendEmptyMessage(CONNECTED);
        }else {
            handler.sendEmptyMessage(INIT);
        }
        Looper.loop();//开始循环
        //循环结束
        QLog.d("looper loop end");
        if(exception != null){
            throw exception;
        }
        return quicResponse;
    }

    public QuicException getException(){
        return exception;
    }

    private Looper getLooper() {
        if (!Thread.currentThread().isAlive()) {
            return null;
        }

        // If the thread has been started, wait until the looper has been created.
        synchronized (this) {
            while (Thread.currentThread().isAlive() && looper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return looper;
    }

    private void quitSafely() {
        QLog.d("isClose: %s; isCompleted: %s; hasReceiveResponse: %s", isClosed, isCompleted, hasReceivedResponse);
        if(isClosed || (isCompleted && hasReceivedResponse)){
            isOver = true;
        }else {
            return;
        }
        if(isCompleted){
            finish();
        }
        handler.removeCallbacksAndMessages(null);
        connectPool.updateQuicNativeState(realQuicCall, COMPLETED);
        Looper looper = getLooper();
        if (looper != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                looper.quitSafely();
            }else {
                looper.quit();
            }
        }
    }

    private void setMessageQueue() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, InstantiationException {
        Field messageQueueField = Looper.class.getDeclaredField("mQueue");
        messageQueueField.setAccessible(true);
        Class<MessageQueue> messageQueueClass = (Class<MessageQueue>) Class.forName("android.os.MessageQueue");
        Constructor<MessageQueue>[] messageQueueConstructor = (Constructor<MessageQueue>[]) messageQueueClass.getDeclaredConstructors();
        for(Constructor<MessageQueue> constructor : messageQueueConstructor){
            constructor.setAccessible(true);
            Class[] types = constructor.getParameterTypes();
            for(Class clazz : types){
                if(clazz.getName().equalsIgnoreCase("boolean")){
                    messageQueueField.set(looper, constructor.newInstance(true));
                    break;
                }
            }
        }
    }

}

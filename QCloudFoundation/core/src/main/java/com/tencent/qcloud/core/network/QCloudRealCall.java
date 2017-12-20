package com.tencent.qcloud.core.network;

import com.tencent.qcloud.core.logger.QCloudLogger;
import com.tencent.qcloud.core.network.action.QCloudRequestAction;
import com.tencent.qcloud.core.network.auth.QCloudSignSourceProvider;
import com.tencent.qcloud.core.network.exception.QCloudClientException;
import com.tencent.qcloud.core.network.request.serializer.RequestBodySerializer;
import com.tencent.qcloud.core.network.response.serializer.ResponseBodySerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wjielai on 2017/8/17.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public final class QCloudRealCall implements QCloudCall {

    private static long requestIdCounter = 1;

    final long requestId;

    final List<QCloudRequestAction> requestActions;

    final RequestBodySerializer requestBodySerializer;

    final ResponseBodySerializer responseBodySerializer;

    final QCloudSignSourceProvider signSourceProvider;

    final QCloudRequestPriority priority;

    private final QCloudHttpRequest cloudRequest;

    private Request httpRequest;

    final QCloudRequestManager requestManager;

    volatile private Call call;

    /**
     * 该请求是否已经调用了取消
     *
     * Guard by this
     */
    private boolean canceled;

    QCloudRealCall(QCloudHttpRequest request, QCloudRequestManager manager) {
        requestActions = new ArrayList<>(request.requestActions);
        requestBodySerializer = request.requestOriginBuilder.requestBodySerializer;
        responseBodySerializer = request.responseBodySerializer;
        signSourceProvider = request.signSourceProvider;
        priority = request.priority;
        cloudRequest = request;
        requestManager = manager;

        httpRequest = request.requestOriginBuilder.build();

        synchronized (QCloudRealCall.class) {
            requestId = requestIdCounter++;
        }
    }

    @Override
    public String toString() {
        return "QCloudRealCall#" + requestId;
    }

    public Request getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(Request request) {
        httpRequest = request;
    }

    public QCloudSignSourceProvider getSignSourceProvider() {
        return signSourceProvider;
    }

    @Override
    public QCloudRequest request() {
        return cloudRequest;
    }

    <T extends QCloudResult> T execute(Call call) throws QCloudClientException {
        setCall(call);
        try {
            Response response = call.execute();
            return deSerialize(response);
        } catch (IOException e) {
            QCloudClientException clientException = getClientException(e);
            throw clientException != null ? clientException : new QCloudClientException(e);
        } finally {
            requestManager.clearCall(this);
        }
    }

    void enqueue(Call call) {
        setCall(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QCloudLogger.d(QCloudLogger.TAG_CORE, "[Call] %s onFailed with %s",
                        QCloudRealCall.this, e.getMessage());
                if (!canceled) {
                    requestManager.clearCall(QCloudRealCall.this);
                    onFailed(getClientException(e));
                }
                requestManager.promoteSend();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!canceled) {
                    try {
                        QCloudResult result = deSerialize(response);
                        QCloudLogger.d(QCloudLogger.TAG_CORE, "[Call] %s onSuccess ", QCloudRealCall.this);
                        onSuccess(result);
                    } catch (QCloudClientException e) {
                        onFailed(e);
                    } finally {
                        requestManager.clearCall(QCloudRealCall.this);
                    }
                } else {
                    QCloudLogger.d(QCloudLogger.TAG_CORE, "[Call] %s onResponse but canceled",
                            QCloudRealCall.this);
                }
                requestManager.promoteSend();
            }
        });
    }

    QCloudClientException getClientException(IOException e) {
        if (e.getCause() instanceof QCloudClientException) {
            return (QCloudClientException) e.getCause();
        }

        return new QCloudClientException(e);
    }

    private <T extends QCloudResult> void onSuccess(T r) {
        if (cloudRequest != null && cloudRequest.getResultListener() != null) {
            cloudRequest.getResultListener().onSuccess(cloudRequest, r);
        } else {
            QCloudLogger.d(QCloudLogger.TAG_CORE, "[Call] %s missing callback", this);
        }
    }

    private void onFailed(QCloudClientException e) {
        if (cloudRequest != null && cloudRequest.getResultListener() != null) {
            cloudRequest.getResultListener().onFailed(cloudRequest, e, null);
        } else {
            QCloudLogger.d(QCloudLogger.TAG_CORE, "[Call] %s missing callback", this);
        }
    }

    private <T extends QCloudResult> T deSerialize(Response response) throws QCloudClientException {
        try {
            QCloudResult result = responseBodySerializer.serialize(response);

            if (result != null) {
                result.setHttpCode(response.code());
                result.setHttpMessage(response.message());
                result.setHeaders(response.headers().toMultimap());
            }
            return (T) result;
        } finally {
            if (response.body() != null) {
                response.close();
            }
        }
    }

    public Request serializeBody(Request originRequest) throws QCloudClientException {
        if (requestBodySerializer != null) {
            Request newRequest = originRequest.newBuilder().method(originRequest.method(),
                    requestBodySerializer.serialize()).build();
            setHttpRequest(newRequest);
            return newRequest;
        }
        return originRequest;
    }

    public Request signRequest(Request originRequest) throws QCloudClientException {
        if (requestActions.size() > 0) {
            for (QCloudRequestAction action : requestActions) {
                Request newRequest = action.execute(this);
                setHttpRequest(newRequest);
            }

            return httpRequest;
        }

        return originRequest;
    }

    @Override
    synchronized public boolean isCanceled() {
        return canceled;
    }

    @Override
    synchronized public boolean isExecuted() {
        return call != null;
    }

    @Override
    synchronized public void cancel() {
        if (isCanceled()) {
            return;
        }

        QCloudLogger.d(QCloudLogger.TAG_CORE, "[Call] %s cancel", this);
        canceled = true;

        if (cloudRequest != null) {
            cloudRequest.setResultListener(null);
        }

        if (call != null) {
            call.cancel();
        }

        requestManager.clearCall(this);
    }

    // 只会在启动任务后调用一次
    private void setCall(Call call) {
        this.call = call;
    }
}

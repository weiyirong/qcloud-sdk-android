package com.tencent.cos.xml.transfer.request;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.Nullable;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;

import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.transfer.BackgroundConfig;
import com.tencent.cos.xml.transfer.CosXmlBackgroundService;
import com.tencent.cos.xml.transfer.cos.ParcelableCredentialProvider;
import com.tencent.cos.xml.transfer.TransferConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by rickenwang on 2019-11-20.
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class COSWorkerRequest {

    protected static final String KEY_SERVICE_EGG = "serviceEgg";
    protected static final String KEY_HEADERS     = "headers";

    @Nullable private Map<String, String> headers;

    /**
     * 用于生成 {@link CosXmlBackgroundService} 对象
     */
    @Nullable protected byte[] cosServiceEgg;

    /**
     * 是否为周期任务
     */
    private boolean isPeriod = false;

    private long interval = 0;

    private TimeUnit intervalTimeUnit;

    /**
     * 任务约束
     */
    private Constraints constraints;

    /**
     * 任务延迟时间
     */
    private long delay;

    private TimeUnit delayTimeUnit;

    /**
     * 背压策略
     */
    private BackoffPolicy backoffPolicy;

    /**
     * 背压延迟时间
     */
    private long backOffDelay;

    private TimeUnit backOffDelayTimeUnit;


    /**
     * 请求标签
     */
    private String tag;


    public void setCosServiceEgg(byte[] cosServiceEgg) {
        this.cosServiceEgg = cosServiceEgg;
    }

    public byte[] getCosServiceEgg() {
        return cosServiceEgg;
    }

    protected Data.Builder buildWorkerData(Data.Builder builder) {

        if (cosServiceEgg != null) {
            builder.putByteArray(KEY_SERVICE_EGG, cosServiceEgg);
        }

        if (headers != null) {

            JSONObject jsonObject = new JSONObject();

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            builder.putString(KEY_HEADERS, jsonObject.toString());
        }


        return builder;
    }

    @Nullable public static CosXmlBackgroundService fromWorkerData(Context context, Data workData) {

        byte[] bytes = workData.getByteArray(KEY_SERVICE_EGG);

        if (bytes != null) {

            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);

            CosXmlServiceConfig cosXmlServiceConfig = parcel.readParcelable(COSWorkerRequest.class.getClassLoader());
            BackgroundConfig transferConfig = parcel.readParcelable(COSWorkerRequest.class.getClassLoader());
            ParcelableCredentialProvider credentialProvider = parcel.readParcelable(COSWorkerRequest.class.getClassLoader());

            parcel.recycle();
            return new CosXmlBackgroundService(context, cosXmlServiceConfig, transferConfig, credentialProvider);
        }
        return null;
    }


    public void BackoffCriteria(BackoffPolicy backoffPolicy, long backOffDelay, TimeUnit timeUnit) {
        this.backoffPolicy = backoffPolicy;
        this.backOffDelay = backOffDelay;
        this.backOffDelayTimeUnit = timeUnit;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    public void enablePeriod(long interval, TimeUnit timeUnit) {
        isPeriod = true;
        this.interval = interval;
        this.intervalTimeUnit = timeUnit;
    }

    public void setDelay(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.delayTimeUnit = timeUnit;
    }

    public boolean isPeriod() {
        return isPeriod;
    }

    public Constraints getConstraints() {
        return constraints;
    }

    public long getDelay() {
        return delay;
    }

    public TimeUnit getDelayTimeUnit() {
        return delayTimeUnit;
    }

    public BackoffPolicy getBackoffPolicy() {
        return backoffPolicy;
    }

    public long getBackOffDelay() {
        return backOffDelay;
    }

    public TimeUnit getBackOffDelayTimeUnit() {
        return backOffDelayTimeUnit;
    }

    public String getTag() {
        return tag;
    }

    public long getInterval() {
        return interval;
    }

    public TimeUnit getIntervalTimeUnit() {
        return intervalTimeUnit;
    }
}

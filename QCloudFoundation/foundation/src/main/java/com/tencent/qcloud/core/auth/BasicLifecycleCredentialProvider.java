package com.tencent.qcloud.core.auth;

import android.text.TextUtils;

import com.tencent.qcloud.core.common.QCloudClientException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 *
 * &lt;a herf https://www.qcloud.com/document/product/436/7778&gt;&lt;/a&gt;
 */

public abstract class BasicLifecycleCredentialProvider implements QCloudCredentialProvider {

    private QCloudLifecycleCredentials credentials;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public final QCloudCredentials getCredentials()  throws QCloudClientException {
        if (needUpdateSignaturePair()) {
            refresh();
        }
        return credentials;
    }

    @Override
    public final void refresh() throws QCloudClientException {
        boolean locked;
        try {
            locked = lock.tryLock(20, TimeUnit.SECONDS);

            if (!locked) {
                throw new QCloudClientException("lock timeout, no credential for sign");
            }

            credentials = fetchNewCredentials();

        } catch (InterruptedException e) {
            throw new QCloudClientException("interrupt when try to get credential", e);
        } finally {
            lock.unlock();
        }
    }

    protected abstract QCloudLifecycleCredentials fetchNewCredentials() throws QCloudClientException;

    private boolean needUpdateSignaturePair() {
        if (credentials == null) {
            return true;
        }

        String keyTime = credentials.getKeyTime(); // timestamp:expireTime;
        if (TextUtils.isEmpty(keyTime)) {
            return true;
        }
        String[] times = keyTime.split(";");
        if (times.length != 2) {
            return true;
        }
        String expire = times[1];
        long expireTime = Long.valueOf(expire);
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime > expireTime - 60) {
            return true;
        }
        return false;
    }
}

package com.tencent.qcloud.core.network;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wjielai on 2017/8/21.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public final class QCloudMetadata {

    public static final String APP_ID_KEY = "com.tencent.qcloud.appid";

    public static final String REGION_KEY = "com.tencent.qcloud.region";

    public static final String SECRET_ID_KEY = "com.tencent.qcloud.secretid";

    public static final String SECRET_KEY = "com.tencent.qcloud.secretkey";

    private static final String QCLOUD_KEY_PREFIX = "com.tencent.qcloud.";

    private final Map<String, String> metadata;

    private static QCloudMetadata instance;

    private QCloudMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String value(String key) {
        return metadata.get(key);
    }

    public String appId() {
        return value(APP_ID_KEY);
    }

    public String secretId() {
        return value(SECRET_ID_KEY);
    }

    public String secretKey() {
        return value(SECRET_KEY);
    }

    public String region() {
        return value(REGION_KEY);
    }

    public static final QCloudMetadata with(Context context) {
        if (instance == null) {
            instance = read(context);
        }

        return instance;
    }

    /**
     * 从AndroidManifest.xml读取key是以com.tencent.qcloud开头的元数据
     *
     * @param context
     * @return
     */
    private static final QCloudMetadata read(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            Map<String, String> metadata = new HashMap<>(10);

            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    if (key.startsWith(QCLOUD_KEY_PREFIX)) {
                        metadata.put(key, bundle.get(key).toString());
                    }
                }
            }

            return new QCloudMetadata(Collections.unmodifiableMap(metadata));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}

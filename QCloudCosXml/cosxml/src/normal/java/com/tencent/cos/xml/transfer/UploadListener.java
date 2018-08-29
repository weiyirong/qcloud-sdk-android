package com.tencent.cos.xml.transfer;

import com.tencent.cos.xml.model.CosXmlResult;

/**
 * Created by bradyxiao on 2018/8/23.
 * Copyright 2010-2018 Tencent Cloud. All Rights Reserved.
 */

public interface UploadListener extends TransferListener {
    public void onGetUploadId(String id, String uploadId);

    public void onSuccess(String id, CosXmlResult result);
}

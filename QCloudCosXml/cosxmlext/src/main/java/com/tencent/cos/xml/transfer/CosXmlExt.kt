package com.tencent.cos.xml.transfer

import androidx.lifecycle.LiveData

import com.tencent.cos.xml.common.COSACL
import com.tencent.cos.xml.transfer.api.COSResponse

/**
 * Created by rickenwang on 2019-11-21.
 *
 *
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
interface CosXmlExt {


    fun listBuckets(headers: Map<String, List<String>>? = null): LiveData<COSResponse<Boolean>>

    fun getBucketAcl(region: String, bucket: String, headers: Map<String, List<String>>? = null): LiveData<COSResponse<COSACL>>

    fun putBucketAcl(region: String, bucket: String, cosacl: COSACL, headers: Map<String, List<String>>? = null): LiveData<COSResponse<Boolean>>

    fun isObjectPublicDownload(region: String, bucket: String, key: String, headers: Map<String, List<String>>? = null)

}

package com.tencent.cos.xml.model;

/**
 * Created by bradyxiao on 2017/7/26.
 * author bradyxiao
 *  result callback for request.
 */
public interface CosXmlResultListener {
    /**
     * success for request. only http code >= 200 && http code < 300.
     * @param request {@link CosXmlRequest}
     * @param result {@link CosXmlResult}
     */
    void onSuccess(CosXmlRequest request, CosXmlResult result);

    /**
     * fail for request. such as http code >= 300 or http code <200 or catch exception.
     * @param request {@link CosXmlRequest}
     * @param result {@link CosXmlResult}
     */
    void onFail(CosXmlRequest request, CosXmlResult result);
}

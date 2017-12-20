package com.tencent.qcloud.core.network.auth;

/**
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class CredentialProviderConst {

    public static String Q_SIGN_ALGORITHM = "q-sign-algorithm";

    public static String Q_AK = "q-ak";

    public static String Q_SIGN_TIME = "q-sign-time";

    public static String Q_KEY_TIME = "q-key-time";

    public static String Q_HEADER_LIST = "q-header-list";

    public static String Q_URL_PARAM_LIST = "q-url-param-list";

    public static String Q_SIGNATURE = "q-signature";


    public static String SHA1 = "sha1";

    private CredentialProviderConst(){
        throw new AssertionError("CredentialProviderConst is static class");
    }
}

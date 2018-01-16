package com.tencent.cos.xml.utils;

/**
 * Created by bradyxiao on 2017/2/28.
 *
 */
public class StringUtils {

    private static final char HEX_DIGITS[] =
            {'0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'};
    public static String toHexString(byte[] data){
        StringBuilder result = new StringBuilder(data.length * 2);
        for(byte b : data){
            result.append(StringUtils.HEX_DIGITS[(b & 0xf0) >>> 4]);
            result.append(StringUtils.HEX_DIGITS[(b & 0x0f)]);
        }
        return result.toString();
    }

    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

}

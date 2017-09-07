package com.tencent.qcloud.network.tools;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by bradyxiao on 2017/7/21.
 * author bradyxiao
 */
public class StringUtils {
    public StringUtils() {
    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        return cs1 == cs2?true:
                (cs1 != null && cs2 != null?
                        (cs1 instanceof String && cs2 instanceof String?
                                cs1.equals(cs2):regionMatches(cs1, false, 0, cs2,
                                0, Math.max(cs1.length(), cs2.length())))
                        :false);
    }

    private static byte[] getBytes(String string, Charset charset) {
        return string == null?null:string.getBytes(charset);
    }

    public static byte[] getBytesIso8859_1(String string) {
        return getBytes(string, Charset.forName("ISO-8859-1"));
    }

    public static byte[] getBytesUnchecked(String string, String charsetName) {
        if(string == null) {
            return null;
        } else {
            try {
                return string.getBytes(charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    public static byte[] getBytesUsAscii(String string) {
        return getBytes(string, Charset.forName("US-ASCII"));
    }

    public static byte[] getBytesUtf16(String string) {
        return getBytes(string, Charset.forName("UTF-16"));
    }

    public static byte[] getBytesUtf16Be(String string) {
        return getBytes(string, Charset.forName("UTF-16BE"));
    }

    public static byte[] getBytesUtf16Le(String string) {
        return getBytes(string, Charset.forName("UTF-16LE"));
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, Charset.forName("UTF-8"));
    }

    private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    private static String newString(byte[] bytes, Charset charset) {
        return bytes == null?null:new String(bytes, charset);
    }

    public static String newString(byte[] bytes, String charsetName) {
        if(bytes == null) {
            return null;
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    public static String newStringIso8859_1(byte[] bytes) {
        return new String(bytes, Charset.forName("ISO-8859-1"));
    }

    public static String newStringUsAscii(byte[] bytes) {
        return new String(bytes, Charset.forName("US-ASCII"));
    }

    public static String newStringUtf16(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-16"));
    }

    public static String newStringUtf16Be(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-16BE"));
    }

    public static String newStringUtf16Le(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-16LE"));
    }

    public static String newStringUtf8(byte[] bytes) {
        return newString(bytes, Charset.forName("UTF-8"));
    }

    public static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if(cs instanceof String && substring instanceof String) {
            return ((String)cs).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;

            while(tmpLen-- > 0) {
                char c1 = cs.charAt(index1++);
                char c2 = substring.charAt(index2++);
                if(c1 != c2) {
                    if(!ignoreCase) {
                        return false;
                    }

                    if(Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}

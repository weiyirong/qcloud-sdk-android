package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 */
@XStreamAlias( "Error")
public class Error {
    /**
     * Name of Object
     */
    @XStreamAlias("Key")
    public String key;

    /**
     * Error code for failed deletion
     */
    @XStreamAlias("Code")
    public String code;

    /**
     * Message indicating the deletion error
     */
    @XStreamAlias("Message")
    public String message;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[key:").append(key).append("\n")
                .append("Code:").append(code).append("\n")
                .append("Message:").append(message).append("]");
        return stringBuilder.toString();
    }
}

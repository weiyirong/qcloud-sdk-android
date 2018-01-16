package com.tencent.qcloud.core.cos.tag;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/5/25.
 * author bradyxiao
 */
@XStreamAlias("Rule")
public class Rule {
    @XStreamAlias("ID")
    public String id;
    @XStreamAlias("Prefix")
    public String prefix;
    @XStreamAlias("Status")
    public String status;
    @XStreamAlias("Transition")
    public Transition transition;
    @XStreamAlias("Expiration")
    public Expiration expiration;
    @XStreamAlias("AbortIncompleteMultipartUpload")
    public AbortIncompleteMultiUpload abortIncompleteMultiUpload;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("ID:").append(id).append("\n")
                .append("Prefix:").append(prefix).append("\n")
                .append("Status:").append(status).append("\n")
                .append("Transition:").append(transition == null ? "null" : transition.toString()).append("\n")
                .append("Expiration:").append(expiration == null ? "null" : expiration.toString()).append("\n")
                .append("AbortIncompleteMultipartUpload:")
                .append(abortIncompleteMultiUpload == null ? "null" : abortIncompleteMultiUpload.toString()).append("\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}

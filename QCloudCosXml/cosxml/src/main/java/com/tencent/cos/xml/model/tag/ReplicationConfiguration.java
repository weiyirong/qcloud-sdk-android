package com.tencent.cos.xml.model.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/11/6.
 */

@XStreamAlias("ReplicationConfiguration")
public class ReplicationConfiguration {

    @XStreamAlias("Role")
    public String role;

    @XStreamAlias("Rule")
    public Rule rule;

    @XStreamAlias("Rule")
    public static class Rule{
        @XStreamAlias("Status")
        public String status;

        @XStreamAlias("ID")
        public String id;

        @XStreamAlias("Prefix")
        public String prefix;

        @XStreamAlias("Destination")
        public Destination destination;
    }

    @XStreamAlias("Destination")
    public static class Destination{
        @XStreamAlias("Bucket")
        public String bucket;

        @XStreamAlias("StorageClass")
        public String storageClass;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("Role:").append(role).append("\n");
        if(rule != null){
            stringBuilder.append("Rule:{\n");
            stringBuilder.append("Status:").append(rule.status).append("\n")
                    .append("Id:").append(rule.id).append("\n")
                    .append("Prefix:").append(rule.prefix).append("\n");
            if(rule.destination != null){
                stringBuilder.append("Destination:{\n")
                        .append("Bucket:").append(rule.destination.bucket).append("\n")
                        .append("StorageClass:").append(rule.destination.storageClass).append("\n");
                stringBuilder.append("}");
            }else {
                stringBuilder.append("Destination:").append("null").append("\n");
            }
        }else {
            stringBuilder.append("Rule:").append("null").append("\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}

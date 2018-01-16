package com.tencent.cos.xml.model.tag;

import java.util.List;

/**
 * Created by bradyxiao on 2017/11/24.
 */

public class ListParts {
    public String bucket;
    public String encodingType;
    public String key;
    public String uploadId;
    public Owner owner;
    public String partNumberMarker;
    public Initiator initiator;
    public String storageClass;
    public String nextPartNumberMarker;
    public String maxParts;
    public boolean isTruncated;
    public List<Part> parts;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder("{ListParts:\n");
        stringBuilder.append("Bucket:").append(bucket).append("\n");
        stringBuilder.append("Encoding-Type:").append(encodingType).append("\n");
        stringBuilder.append("Key:").append(key).append("\n");
        stringBuilder.append("UploadId:").append(uploadId).append("\n");
        if(owner != null)stringBuilder.append(owner.toString()).append("\n");
        stringBuilder.append("PartNumberMarker:").append(partNumberMarker).append("\n");
        if(initiator != null) stringBuilder.append(initiator.toString()).append("\n");
        stringBuilder.append("StorageClass:").append(storageClass).append("\n");
        stringBuilder.append("NextPartNumberMarker:").append(nextPartNumberMarker).append("\n");
        stringBuilder.append("MaxParts:").append(maxParts).append("\n");
        stringBuilder.append("IsTruncated:").append(isTruncated).append("\n");
        if(parts != null){
            for(Part part : parts){
                if(part != null)stringBuilder.append(part.toString()).append("\n");
            }
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static class Owner{
        public String id;
        public String disPlayName;

        @Override
        public String toString(){
            StringBuilder stringBuilder = new StringBuilder("{Owner:\n");
            stringBuilder.append("Id:").append(id).append("\n");
            stringBuilder.append("DisPlayName:").append(disPlayName).append("\n");
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

    public static class Initiator{
        public String id;
        public String disPlayName;

        @Override
        public String toString(){
            StringBuilder stringBuilder = new StringBuilder("{Initiator:\n");
            stringBuilder.append("Id:").append(id).append("\n");
            stringBuilder.append("DisPlayName:").append(disPlayName).append("\n");
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

    public static class Part{
        public String partNumber;
        public String lastModified;
        public String eTag;
        public String size;

        @Override
        public String toString(){
            StringBuilder stringBuilder = new StringBuilder("{Part:\n");
            stringBuilder.append("PartNumber:").append(partNumber).append("\n");
            stringBuilder.append("LastModified:").append(lastModified).append("\n");
            stringBuilder.append("ETag:").append(eTag).append("\n");
            stringBuilder.append("Size:").append(size).append("\n");
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

}

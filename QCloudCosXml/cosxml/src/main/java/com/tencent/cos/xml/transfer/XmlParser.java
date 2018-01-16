package com.tencent.cos.xml.transfer;

import android.util.Xml;


import com.tencent.cos.xml.model.tag.AccessControlPolicy;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.tencent.cos.xml.model.tag.CompleteMultipartUploadResult;
import com.tencent.cos.xml.model.tag.CopyObject;
import com.tencent.cos.xml.model.tag.CopyPart;
import com.tencent.cos.xml.model.tag.CosError;
import com.tencent.cos.xml.model.tag.DeleteResult;
import com.tencent.cos.xml.model.tag.InitiateMultipartUpload;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.tencent.cos.xml.model.tag.ListAllMyBuckets;
import com.tencent.cos.xml.model.tag.ListBucket;
import com.tencent.cos.xml.model.tag.ListBucketVersions;
import com.tencent.cos.xml.model.tag.ListMultipartUploads;
import com.tencent.cos.xml.model.tag.ListParts;
import com.tencent.cos.xml.model.tag.LocationConstraint;
import com.tencent.cos.xml.model.tag.ReplicationConfiguration;
import com.tencent.cos.xml.model.tag.VersioningConfiguration;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by bradyxiao on 2017/11/24.
 */

public class XmlParser {
    //service
    public static void parseListAllMyBucketsResult(InputStream inputStream, ListAllMyBuckets result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        ListAllMyBuckets.Bucket bucket = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                /**xml document start*/
                case XmlPullParser.START_DOCUMENT:
                    break;
                /**xml tag start*/
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Owner")){
                        result.owner = new ListAllMyBuckets.Owner();
                    }else if(tagName.equalsIgnoreCase("ID") ){
                        xmlPullParser.next();
                        result.owner.id = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("DisplayName")){
                        xmlPullParser.next();
                        result.owner.disPlayName = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Buckets")){
                        result.buckets = new ArrayList<ListAllMyBuckets.Bucket>();
                    }else if(tagName.equalsIgnoreCase("Bucket")){
                        bucket = new ListAllMyBuckets.Bucket();
                    }else if(tagName.equalsIgnoreCase("Name")){
                        xmlPullParser.next();
                        bucket.name = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Location")){
                        xmlPullParser.next();
                        bucket.location = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("CreateDate")){
                        xmlPullParser.next();
                        bucket.createDate = xmlPullParser.getText();
                    }
                    break;
                /**xml tag end*/
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Bucket")){
                        result.buckets.add(bucket);
                        bucket = null;
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    //bucket
    public static void parseListBucketResult(InputStream inputStream, ListBucket result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        ListBucket.Contents contents = null;
        ListBucket.CommonPrefixes commonPrefixes = null;
        ListBucket.Owner owner = null;
        result.contentsList = new ArrayList<ListBucket.Contents>();
        result.commonPrefixesList = new ArrayList<ListBucket.CommonPrefixes>();
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch(eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Name")){
                        xmlPullParser.next();
                        result.name = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Encoding-Type")){
                        xmlPullParser.next();
                        result.encodingType = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Marker")){
                        xmlPullParser.next();
                        result.marker = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("MaxKeys")){
                        xmlPullParser.next();
                        result.maxKeys = Integer.parseInt(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("Delimiter")){
                        xmlPullParser.next();
                        result.delimiter = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("NextMarker")){
                        xmlPullParser.next();
                        result.nextMarker = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("IsTruncated")){
                        xmlPullParser.next();
                        result.isTruncated = Boolean.parseBoolean(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("Prefix")){
                        xmlPullParser.next();
                        if(commonPrefixes == null){
                            result.prefix = xmlPullParser.getText();
                        }else {
                            commonPrefixes.prefix =  xmlPullParser.getText();
                        }
                    }else if(tagName.equalsIgnoreCase("Contents")){
                        contents = new ListBucket.Contents();
                    }else if(tagName.equalsIgnoreCase("Key")){
                        xmlPullParser.next();
                        contents.key = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("LastModified")){
                        xmlPullParser.next();
                        contents.lastModified = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("ETag")){
                        xmlPullParser.next();
                        contents.eTag = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Size")){
                        xmlPullParser.next();
                        contents.size = Long.parseLong(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("StorageClass")){
                        xmlPullParser.next();
                        contents.storageClass = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Owner")){
                       owner = new ListBucket.Owner();
                    }else if(tagName.equalsIgnoreCase("ID")){
                        xmlPullParser.next();
                        owner.id = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("CommonPrefixes")){
                        commonPrefixes = new ListBucket.CommonPrefixes();
                    }
                    break;
                    case XmlPullParser.END_TAG:
                        tagName = xmlPullParser.getName();
                        if(tagName.equalsIgnoreCase("Contents")){
                            result.contentsList.add(contents);
                            contents = null;
                        }else if(tagName.equalsIgnoreCase("Owner")){
                            contents.owner = owner;
                            owner = null;
                        }else if(tagName.equalsIgnoreCase("CommonPrefixes")){
                            result.commonPrefixesList.add(commonPrefixes);
                            commonPrefixes = null;
                        }
                        break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseAccessControlPolicy(InputStream inputStream, AccessControlPolicy result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        AccessControlPolicy.Owner owner = null;
        result.accessControlList = new AccessControlPolicy.AccessControlList();
        result.accessControlList.grants = new ArrayList<AccessControlPolicy.Grant>();
        AccessControlPolicy.Grant grant = null;
        AccessControlPolicy.Grantee grantee = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if (tagName.equalsIgnoreCase("Owner")){
                        owner = new AccessControlPolicy.Owner();
                    }else if (tagName.equalsIgnoreCase("ID")){
                        xmlPullParser.next();
                        if(owner != null){
                            owner.id = xmlPullParser.getText();
                        }else if(grantee != null){
                            grantee.id = xmlPullParser.getText();
                        }
                    }else if (tagName.equalsIgnoreCase("DisplayName")){
                        xmlPullParser.next();
                        if(owner != null){
                            owner.displayName = xmlPullParser.getText();
                        }else if(grantee != null){
                            grantee.displayName = xmlPullParser.getText();
                        }
                    }else if (tagName.equalsIgnoreCase("Grant")){
                        grant = new AccessControlPolicy.Grant();
                    }else if (tagName.equalsIgnoreCase("Grantee")){
                        grantee = new AccessControlPolicy.Grantee();
                    }else if (tagName.equalsIgnoreCase("Permission")){
                        xmlPullParser.next();
                        grant.permission = xmlPullParser.getText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Owner")){
                        result.owner = owner;
                        owner = null;
                    }else if(tagName.equalsIgnoreCase("Grant")){
                        result.accessControlList.grants.add(grant);
                        grant = null;
                    }else if(tagName.equalsIgnoreCase("Grantee")){
                        grant.grantee =grantee;
                        grantee = null;
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseCORSConfiguration(InputStream inputStream, CORSConfiguration result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        result.corsRules = new ArrayList<CORSConfiguration.CORSRule>();
        CORSConfiguration.CORSRule corsRule = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("CORSRule")){
                        corsRule = new CORSConfiguration.CORSRule();
                    }else if(tagName.equalsIgnoreCase("ID")){
                        xmlPullParser.next();
                        corsRule.id = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("AllowedOrigin")){
                        xmlPullParser.next();
                        corsRule.allowedOrigin = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("AllowedMethod")){
                        xmlPullParser.next();
                        if(corsRule.allowedMethod == null){
                            corsRule.allowedMethod = new ArrayList<String>();
                        }
                        corsRule.allowedMethod.add(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("AllowedHeader")){
                        xmlPullParser.next();
                        if(corsRule.allowedHeader == null){
                            corsRule.allowedHeader = new ArrayList<String>();
                        }
                        corsRule.allowedHeader.add(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("ExposeHeader")){
                        xmlPullParser.next();
                        if(corsRule.exposeHeader == null){
                            corsRule.exposeHeader = new ArrayList<String>();
                        }
                        corsRule.exposeHeader.add(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("MaxAgeSeconds")){
                        xmlPullParser.next();
                        corsRule.maxAgeSeconds = Integer.parseInt(xmlPullParser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("CORSRule")){
                        result.corsRules.add(corsRule);
                        corsRule = null;
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseReplicationConfiguration(InputStream inputStream, ReplicationConfiguration result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        result.rules = new ArrayList<ReplicationConfiguration.Rule>();
        ReplicationConfiguration.Rule rule = null;
        ReplicationConfiguration.Destination destination = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Role")){
                        xmlPullParser.next();
                        result.role = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Rule")){
                        rule = new ReplicationConfiguration.Rule();
                    }else if(tagName.equalsIgnoreCase("Status")){
                        xmlPullParser.next();
                        rule.status = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("ID")){
                        xmlPullParser.next();
                        rule.id = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Prefix")){
                        xmlPullParser.next();
                        rule.prefix = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Destination")){
                        destination = new ReplicationConfiguration.Destination();
                    }else if(tagName.equalsIgnoreCase("Bucket")){
                        xmlPullParser.next();
                        destination.bucket = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("StorageClass")){
                        xmlPullParser.next();
                        destination.storageClass = xmlPullParser.getText();
                    }
                    break;
                    case XmlPullParser.END_TAG:
                        tagName = xmlPullParser.getName();
                        if(tagName.equalsIgnoreCase("Rule")){
                            result.rules.add(rule);
                            rule = null;
                        }else if(tagName.equalsIgnoreCase("Destination")){
                            rule.destination = destination;
                            destination = null;
                        }
                        break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseVersioningConfiguration(InputStream inputStream, VersioningConfiguration result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Status")){
                        xmlPullParser.next();
                        result.status = xmlPullParser.getText();
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseLifecycleConfiguration(InputStream inputStream, LifecycleConfiguration result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        result.rules = new ArrayList<LifecycleConfiguration.Rule>();
        LifecycleConfiguration.Rule rule = null;
        LifecycleConfiguration.Filter filter = null;
        LifecycleConfiguration.Transition transition = null;
        LifecycleConfiguration.Expiration expiration = null;
        LifecycleConfiguration.AbortIncompleteMultiUpload abortIncompleteMultiUpload = null;
        LifecycleConfiguration.NoncurrentVersionExpiration noncurrentVersionExpiration = null;
        LifecycleConfiguration.NoncurrentVersionTransition noncurrentVersionTransition = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Rule")){
                        rule = new LifecycleConfiguration.Rule();
                    }else if(tagName.equalsIgnoreCase("ID")){
                       xmlPullParser.next();
                       rule.id = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Filter")){
                        filter = new LifecycleConfiguration.Filter();
                    }else if(tagName.equalsIgnoreCase("Prefix")){
                        xmlPullParser.next();
                        filter.prefix= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Status")){
                        xmlPullParser.next();
                        rule.status = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Transition")){
                        transition = new LifecycleConfiguration.Transition();
                    }else if(tagName.equalsIgnoreCase("Expiration")){
                        expiration = new LifecycleConfiguration.Expiration();
                    }else if(tagName.equalsIgnoreCase("Days")){
                        xmlPullParser.next();
                        if(transition != null){
                            transition.days = Integer.parseInt(xmlPullParser.getText());
                        }else if(rule.expiration != null){
                            expiration.days = Integer.parseInt(xmlPullParser.getText());
                        }
                    }else if(tagName.equalsIgnoreCase("Date")){
                        xmlPullParser.next();
                        if(transition != null){
                            transition.date = xmlPullParser.getText();
                        }else if(expiration != null){
                            expiration.date = xmlPullParser.getText();
                        }
                    }else if(tagName.equalsIgnoreCase("ExpiredObjectDeleteMarker")){
                        xmlPullParser.next();
                        expiration.expiredObjectDeleteMarker = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("AbortIncompleteMultipartUpload")){
                        abortIncompleteMultiUpload = new LifecycleConfiguration.AbortIncompleteMultiUpload();
                    }else if(tagName.equalsIgnoreCase("DaysAfterInitiation")){
                        xmlPullParser.next();
                        abortIncompleteMultiUpload.daysAfterInitiation = Integer.parseInt(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("NoncurrentVersionExpiration")){
                        noncurrentVersionExpiration = new LifecycleConfiguration.NoncurrentVersionExpiration();
                    }else if(tagName.equalsIgnoreCase("NoncurrentVersionTransition")){
                        noncurrentVersionTransition = new LifecycleConfiguration.NoncurrentVersionTransition();
                    }else if(tagName.equalsIgnoreCase("NoncurrentDays")){
                        xmlPullParser.next();
                        if(noncurrentVersionExpiration != null){
                            noncurrentVersionExpiration.noncurrentDays = Integer.parseInt(xmlPullParser.getText());
                        }else if(noncurrentVersionTransition != null){
                            noncurrentVersionTransition.noncurrentDays = Integer.parseInt(xmlPullParser.getText());
                        }
                    }else if(tagName.equalsIgnoreCase("StorageClass")){
                        xmlPullParser.next();
                        if(transition != null){
                            transition.storageClass = xmlPullParser.getText();
                        }else if(noncurrentVersionTransition != null){
                            noncurrentVersionTransition.storageClass = xmlPullParser.getText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if (tagName.equalsIgnoreCase("Rule")){
                        result.rules.add(rule);
                        rule = null;
                    }else if (tagName.equalsIgnoreCase("Filter")){
                        rule.filter = filter;
                        filter = null;
                    }else if (tagName.equalsIgnoreCase("Transition")){
                        rule.transition = transition;
                        transition = null;
                    }else if (tagName.equalsIgnoreCase("NoncurrentVersionExpiration")){
                        rule.noncurrentVersionExpiration = noncurrentVersionExpiration;
                        noncurrentVersionExpiration = null;
                    }else if (tagName.equalsIgnoreCase("NoncurrentVersionTransition")){
                        rule.noncurrentVersionTransition = noncurrentVersionTransition;
                        noncurrentVersionTransition = null;
                    }else if (tagName.equalsIgnoreCase("Expiration")){
                        rule.expiration = expiration;
                        expiration = null;
                    }else if (tagName.equalsIgnoreCase("AbortIncompleteMultipartUpload")){
                        rule.abortIncompleteMultiUpload = abortIncompleteMultiUpload;
                        abortIncompleteMultiUpload = null;
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseLocationConstraint(InputStream inputStream, LocationConstraint result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("LocationConstraint")){
                        xmlPullParser.next();
                        result.location = xmlPullParser.getText();
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseListMultipartUploadsResult(InputStream inputStream, ListMultipartUploads result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        ListMultipartUploads.CommonPrefixes commonPrefixes = null;
        ListMultipartUploads.Upload upload = null;
        result.uploads = new ArrayList<ListMultipartUploads.Upload>();
        result.commonPrefixes = new ArrayList<ListMultipartUploads.CommonPrefixes>();
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if (tagName.equalsIgnoreCase("Bucket")){
                        xmlPullParser.next();
                        result.bucket = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("Encoding-Type")){
                        xmlPullParser.next();
                        result.encodingType = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("KeyMarker")){
                        xmlPullParser.next();
                        result.keyMarker = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("UploadIdMarker")){
                        xmlPullParser.next();
                        result.uploadIdMarker = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("NextKeyMarker")){
                        xmlPullParser.next();
                        result.nextKeyMarker = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("NextUploadIdMarker")){
                        xmlPullParser.next();
                        result.nextUploadIdMarker = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("MaxUploads")){
                        xmlPullParser.next();
                        result.maxUploads = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("IsTruncated")){
                        xmlPullParser.next();
                        result.isTruncated = Boolean.parseBoolean(xmlPullParser.getText());
                    }else if (tagName.equalsIgnoreCase("Prefix")){
                        xmlPullParser.next();
                        if(commonPrefixes == null){
                            result.prefix = xmlPullParser.getText();
                        }else {
                            commonPrefixes.prefix = xmlPullParser.getText();
                        }
                    }else if (tagName.equalsIgnoreCase("Delimiter")){
                        xmlPullParser.next();
                        result.delimiter = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Upload")){
                        upload = new ListMultipartUploads.Upload();
                    }else if (tagName.equalsIgnoreCase("Key")){
                        xmlPullParser.next();
                        upload.key = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("UploadID")){
                        xmlPullParser.next();
                        upload.uploadID = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("StorageClass")){
                        xmlPullParser.next();
                        upload.storageClass = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("Initiator")){
                        upload.initiator = new ListMultipartUploads.Initiator();
                    }else if (tagName.equalsIgnoreCase("UIN")){
                        xmlPullParser.next();
                        upload.initiator.uin = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("Owner")){
                        upload.owner = new ListMultipartUploads.Owner();
                    }else if (tagName.equalsIgnoreCase("UID")){
                        xmlPullParser.next();
                        upload.owner.uid = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("Initiated")){
                        xmlPullParser.next();
                        upload.initiated = xmlPullParser.getText();
                    }else if (tagName.equalsIgnoreCase("CommonPrefixs")){
                        commonPrefixes = new ListMultipartUploads.CommonPrefixes();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Upload")){
                        result.uploads.add(upload);
                        upload = null;
                    }else if(tagName.equalsIgnoreCase("CommonPrefixs")){
                        result.commonPrefixes.add(commonPrefixes);
                        commonPrefixes = null;
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseCompleteMultipartUploadResult(InputStream inputStream, CompleteMultipartUploadResult result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Location")){
                        xmlPullParser.next();
                        result.location = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Bucket")){
                        xmlPullParser.next();
                        result.bucket = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Key")){
                        xmlPullParser.next();
                        result.key = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("ETag")){
                        xmlPullParser.next();
                        result.eTag = xmlPullParser.getText();
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseDeleteResult(InputStream inputStream, DeleteResult result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        result.errorList = new ArrayList<DeleteResult.Error>();
        result.deletedList = new ArrayList<DeleteResult.Deleted>();
        DeleteResult.Deleted deleted = null;
        DeleteResult.Error error = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Deleted")){
                        deleted = new DeleteResult.Deleted();
                    }else if(tagName.equalsIgnoreCase("CosError")){
                        error= new DeleteResult.Error();
                    }else if(tagName.equalsIgnoreCase("Key")){
                        xmlPullParser.next();
                        if(deleted != null){
                            deleted.key = xmlPullParser.getText();
                        }else if(error != null){
                            error.key = xmlPullParser.getText();
                        }
                    }else if(tagName.equalsIgnoreCase("Code")){
                        xmlPullParser.next();
                        error.code = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Message")){
                        xmlPullParser.next();
                        error.message = xmlPullParser.getText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Deleted")){
                        result.deletedList.add(deleted);
                        deleted = null;
                    }else if(tagName.equalsIgnoreCase("CosError")){
                    result.errorList.add(error);
                    error = null;
                }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseInitiateMultipartUploadResult(InputStream inputStream, InitiateMultipartUpload result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Bucket")){
                        xmlPullParser.next();
                        result.bucket = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Key")){
                        xmlPullParser.next();
                        result.key = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("UploadId")){
                        xmlPullParser.next();
                        result.uploadId = xmlPullParser.getText();
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseListPartsResult(InputStream inputStream, ListParts result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        result.parts = new ArrayList<ListParts.Part>();
        ListParts.Owner owner = null;
        ListParts.Initiator initiator = null;
        ListParts.Part part = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Bucket")){
                        xmlPullParser.next();
                        result.bucket = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Encoding-type")){
                        xmlPullParser.next();
                        result.encodingType = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Key")){
                        xmlPullParser.next();
                        result.key = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("UploadId")){
                        xmlPullParser.next();
                        result.uploadId = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Owner")){
                        owner = new ListParts.Owner();
                    }else if(tagName.equalsIgnoreCase("Initiator")){
                        initiator = new ListParts.Initiator();
                    }else if(tagName.equalsIgnoreCase("ID")){
                        xmlPullParser.next();
                        if(owner != null){
                            owner.id = xmlPullParser.getText();
                        }else if(initiator != null){
                            initiator.id = xmlPullParser.getText();
                        }
                    }else if(tagName.equalsIgnoreCase("DisplayName")){
                        xmlPullParser.next();
                        if(owner != null){
                            owner.disPlayName = xmlPullParser.getText();
                        }else if(initiator != null){
                            initiator.disPlayName= xmlPullParser.getText();
                        }
                    }else if(tagName.equalsIgnoreCase("PartNumberMarker")){
                        xmlPullParser.next();
                        result.partNumberMarker = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("StorageClass")){
                        xmlPullParser.next();
                        result.storageClass = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("NextPartNumberMarker")){
                        xmlPullParser.next();
                        result.nextPartNumberMarker = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("MaxParts")){
                        xmlPullParser.next();
                        result.maxParts = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("IsTruncated")){
                        xmlPullParser.next();
                        result.isTruncated = Boolean.parseBoolean(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("Part")){
                        part = new ListParts.Part();
                    }else if(tagName.equalsIgnoreCase("PartNumber")){
                        xmlPullParser.next();
                        part.partNumber = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("LastModified")){
                        xmlPullParser.next();
                        part.lastModified = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("ETag")){
                        xmlPullParser.next();
                        part.eTag = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Size")){
                        xmlPullParser.next();
                        part.size = xmlPullParser.getText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Owner")){
                        result.owner = owner;
                        owner = null;
                    }else if(tagName.equalsIgnoreCase("Initiator")){
                        result.initiator = initiator;
                        initiator = null;
                    }else if(tagName.equalsIgnoreCase("Part")){
                        result.parts.add(part);
                        part = null;
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseCopyObjectResult(InputStream inputStream, CopyObject result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("ETag")){
                        xmlPullParser.next();
                        result.eTag = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("LastModified")){
                        xmlPullParser.next();
                        result.lastModified = xmlPullParser.getText();
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseCopyPartResult(InputStream inputStream, CopyPart result) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("ETag")){
                        xmlPullParser.next();
                        result.eTag = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("LastModified")){
                        xmlPullParser.next();
                        result.lastModified = xmlPullParser.getText();
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseListBucketVersions(InputStream inputStream, ListBucketVersions result) throws XmlPullParserException, IOException{
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        result.objectVersionList = new ArrayList<>();
        ListBucketVersions.ObjectVersion objectVersion = null;
        ListBucketVersions.Owner owner = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Name")){
                        xmlPullParser.next();
                        result.name = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Prefix")){
                        xmlPullParser.next();
                        result.prefix= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("KeyMarker")){
                        xmlPullParser.next();
                        result.keyMarker= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("VersionIdMarker")){
                        xmlPullParser.next();
                        result.versionIdMarker= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("MaxKeys")){
                        xmlPullParser.next();
                        result.maxKeys= Long.parseLong(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("IsTruncated")){
                        xmlPullParser.next();
                        result.isTruncated= Boolean.parseBoolean(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("NextKeyMarker")){
                        xmlPullParser.next();
                        result.nextKeyMarker= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("NextVersionIdMarker")){
                        xmlPullParser.next();
                        result.nextVersionIdMarker= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("DeleteMarker")){
                        objectVersion = new ListBucketVersions.DeleteMarker();
                    }else if(tagName.equalsIgnoreCase("Version")){
                        objectVersion = new ListBucketVersions.Version();
                    }else if(tagName.equalsIgnoreCase("Key")){
                        xmlPullParser.next();
                        objectVersion.key= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("VersionId")){
                        xmlPullParser.next();
                        objectVersion.versionId= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("IsLatest")){
                        xmlPullParser.next();
                        objectVersion.isLatest= Boolean.parseBoolean(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("LastModified")){
                        xmlPullParser.next();
                        objectVersion.lastModified= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Owner")){
                        owner = new ListBucketVersions.Owner();
                    }else if(tagName.equalsIgnoreCase("UID")){
                        xmlPullParser.next();
                        owner.uid = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("ETag")){
                        xmlPullParser.next();
                        ((ListBucketVersions.Version)objectVersion).eTag= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Size")){
                        xmlPullParser.next();
                        ((ListBucketVersions.Version)objectVersion).size= Long.parseLong(xmlPullParser.getText());
                    }else if(tagName.equalsIgnoreCase("StorageClass")){
                        xmlPullParser.next();
                        ((ListBucketVersions.Version)objectVersion).storageClass= xmlPullParser.getText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Owner")){
                        objectVersion.owner = owner;
                        owner = null;
                    }else if(tagName.equalsIgnoreCase("DeleteMarker")){
                        result.objectVersionList.add(objectVersion);
                        objectVersion = null;
                    }else if(tagName.equalsIgnoreCase("Version")){
                        result.objectVersionList.add(objectVersion);
                        objectVersion = null;
                    }
            }
            eventType = xmlPullParser.next();
        }
    }

    public static void parseError(InputStream inputStream, CosError error) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser =  Xml.newPullParser();
        xmlPullParser.setInput(inputStream, "UTF-8");
        int eventType = xmlPullParser.getEventType();
        String tagName;
        while (eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    tagName = xmlPullParser.getName();
                    if(tagName.equalsIgnoreCase("Code")){
                        xmlPullParser.next();
                        error.code = xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Message")){
                        xmlPullParser.next();
                        error.message= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("Resource")){
                        xmlPullParser.next();
                        error.resource= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("RequestId")){
                    xmlPullParser.next();
                    error.requestId= xmlPullParser.getText();
                    }else if(tagName.equalsIgnoreCase("TraceId")){
                        xmlPullParser.next();
                        error.traceId= xmlPullParser.getText();
                    }
                    break;
            }
            eventType = xmlPullParser.next();
        }
    }
}

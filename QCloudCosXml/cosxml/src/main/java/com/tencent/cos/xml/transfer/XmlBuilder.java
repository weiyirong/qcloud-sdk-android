package com.tencent.cos.xml.transfer;


import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.tencent.cos.xml.model.tag.CompleteMultipartUpload;
import com.tencent.cos.xml.model.tag.Delete;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.tencent.cos.xml.model.tag.ReplicationConfiguration;
import com.tencent.cos.xml.model.tag.RestoreConfigure;
import com.tencent.cos.xml.model.tag.VersioningConfiguration;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by bradyxiao on 2017/11/26.
 */

public class XmlBuilder {

    public static String buildCORSConfigurationXML(CORSConfiguration corsConfiguration) throws XmlPullParserException, IOException {

        if(corsConfiguration == null)return null;

        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "CORSConfiguration");

        if(corsConfiguration.corsRules != null){
            for(CORSConfiguration.CORSRule corsRule : corsConfiguration.corsRules){
                if(corsRule == null) continue;

                xmlSerializer.startTag("", "CORSRule");

                addElement(xmlSerializer, "ID", corsRule.id);
                addElement(xmlSerializer, "AllowedOrigin", corsRule.allowedOrigin);
                if(corsRule.allowedMethod != null){
                    for(String allowedMethod : corsRule.allowedMethod){
                        addElement(xmlSerializer, "AllowedMethod", allowedMethod);
                    }
                }
                if(corsRule.allowedHeader != null){
                    for(String allowedHeader : corsRule.allowedHeader){
                        addElement(xmlSerializer, "AllowedHeader", allowedHeader);
                    }
                }
                if(corsRule.exposeHeader != null){
                    for(String exposeHeader : corsRule.exposeHeader){
                        addElement(xmlSerializer, "ExposeHeader", exposeHeader);
                    }
                }
                addElement(xmlSerializer, "MaxAgeSeconds", String.valueOf(corsRule.maxAgeSeconds));

                xmlSerializer.endTag("", "CORSRule");
            }
        }

        xmlSerializer.endTag("", "CORSConfiguration");

        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildLifecycleConfigurationXML(LifecycleConfiguration lifecycleConfiguration) throws XmlPullParserException, IOException {
        if (lifecycleConfiguration == null)return null;

        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "LifecycleConfiguration");

        if(lifecycleConfiguration.rules != null){
            for(LifecycleConfiguration.Rule rule : lifecycleConfiguration.rules){
                if(rule == null)continue;

                xmlSerializer.startTag("", "Rule");

                addElement(xmlSerializer, "ID", rule.id);
                if(rule.filter != null){
                    xmlSerializer.startTag("", "Filter");
                    addElement(xmlSerializer, "Prefix", rule.filter.prefix);
                    xmlSerializer.endTag("", "Filter");
                }
                addElement(xmlSerializer, "Status", rule.status);

                if(rule.transition != null){
                    xmlSerializer.startTag("", "Transition");
                    addElement(xmlSerializer, "Days", String.valueOf(rule.transition.days));
                    addElement(xmlSerializer, "StorageClass", rule.transition.storageClass);
                    addElement(xmlSerializer, "Date", rule.transition.date);
                    xmlSerializer.endTag("", "Transition");
                }
                if(rule.expiration != null){
                    xmlSerializer.startTag("", "Expiration");
                    addElement(xmlSerializer, "Days", String.valueOf(rule.expiration.days));
                    addElement(xmlSerializer, "ExpiredObjectDeleteMarker", rule.expiration.expiredObjectDeleteMarker);
                    addElement(xmlSerializer, "Date", rule.expiration.date);
                    xmlSerializer.endTag("", "Expiration");
                }
                if(rule.noncurrentVersionTransition != null){
                    xmlSerializer.startTag("", "NoncurrentVersionTransition");
                    addElement(xmlSerializer, "NoncurrentDays", String.valueOf(rule.noncurrentVersionTransition.noncurrentDays));
                    addElement(xmlSerializer, "StorageClass", rule.noncurrentVersionTransition.storageClass);
                    xmlSerializer.endTag("", "NoncurrentVersionTransition");
                }
                if(rule.noncurrentVersionExpiration != null){
                    xmlSerializer.startTag("", "NoncurrentVersionExpiration");
                    addElement(xmlSerializer, "NoncurrentDays", String.valueOf(rule.noncurrentVersionExpiration.noncurrentDays));
                    xmlSerializer.endTag("", "NoncurrentVersionExpiration");
                }
                if(rule.abortIncompleteMultiUpload != null){
                    xmlSerializer.startTag("", "AbortIncompleteMultipartUpload");
                    addElement(xmlSerializer, "DaysAfterInitiation", String.valueOf(rule.abortIncompleteMultiUpload.daysAfterInitiation));
                    xmlSerializer.endTag("", "AbortIncompleteMultipartUpload");
                }

                xmlSerializer.endTag("", "Rule");
            }
        }

        xmlSerializer.endTag("", "LifecycleConfiguration");

        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildReplicationConfiguration(ReplicationConfiguration replicationConfiguration) throws XmlPullParserException, IOException {
        if (replicationConfiguration == null)return null;

        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "ReplicationConfiguration");

        addElement(xmlSerializer, "Role", replicationConfiguration.role);
        if(replicationConfiguration.rules != null){
            for(ReplicationConfiguration.Rule rule : replicationConfiguration.rules){
                if(rule == null)continue;
                xmlSerializer.startTag("", "Rule");

                addElement(xmlSerializer, "Status", rule.status);
                addElement(xmlSerializer, "ID", rule.id);
                addElement(xmlSerializer, "Prefix", rule.prefix);
                if(rule.destination != null){
                    xmlSerializer.startTag("", "Destination");
                    addElement(xmlSerializer, "Bucket", rule.destination.bucket);
                    addElement(xmlSerializer, "StorageClass", rule.destination.storageClass);
                    xmlSerializer.endTag("", "Destination");
                }
                xmlSerializer.endTag("", "Rule");
            }
        }

        xmlSerializer.endTag("", "ReplicationConfiguration");

        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildVersioningConfiguration(VersioningConfiguration versioningConfiguration) throws XmlPullParserException, IOException {
        if (versioningConfiguration == null)return null;

        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "VersioningConfiguration");
        addElement(xmlSerializer, "Status", versioningConfiguration.status);
        xmlSerializer.endTag("", "VersioningConfiguration");

        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildCompleteMultipartUpload(CompleteMultipartUpload completeMultipartUpload) throws IOException, XmlPullParserException {
        if (completeMultipartUpload == null)return null;

        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "CompleteMultipartUpload");
        if(completeMultipartUpload.parts != null){
            for(CompleteMultipartUpload.Part part : completeMultipartUpload.parts){
                if(part == null)continue;
                xmlSerializer.startTag("", "Part");
                addElement(xmlSerializer, "PartNumber", String.valueOf(part.partNumber));
                addElement(xmlSerializer, "ETag", part.eTag);
                xmlSerializer.endTag("", "Part");
            }
        }
        xmlSerializer.endTag("", "CompleteMultipartUpload");

        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildDelete(Delete delete) throws XmlPullParserException, IOException {
        if (delete == null)return null;

        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "Delete");
        addElement(xmlSerializer, "Quiet", String.valueOf(delete.quiet));
        if(delete.deleteObjects != null){
            for(Delete.DeleteObject deleteObject : delete.deleteObjects){
                if(deleteObject == null)continue;
                xmlSerializer.startTag("", "Object");
                addElement(xmlSerializer, "Key", deleteObject.key);
                xmlSerializer.endTag("", "Object");
            }
        }

        xmlSerializer.endTag("", "Delete");

        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildRestore(RestoreConfigure restoreConfigure) throws XmlPullParserException, IOException {
        if(restoreConfigure == null)return  null;

        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "RestoreRequest");
        addElement(xmlSerializer, "Days", String.valueOf(restoreConfigure.days));
        if(restoreConfigure.casJobParameters != null){
            xmlSerializer.startTag("", "CASJobParameters");
            addElement(xmlSerializer, "Tier", restoreConfigure.casJobParameters.tier);
            xmlSerializer.endTag("", "CASJobParameters");
        }
        xmlSerializer.endTag("", "RestoreRequest");

        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    private static void addElement(XmlSerializer xmlSerializer, String tag, String value) throws IOException {
        if(value != null){
            xmlSerializer.startTag("", tag);
            xmlSerializer.text(value);
            xmlSerializer.endTag("", tag);
        }
    }

    private static String removeXMLHeader(String xmlContent){
        if(xmlContent != null){
            if(xmlContent.startsWith("<?xml")){
                int index = xmlContent.indexOf("?>");
                xmlContent = xmlContent.substring(index + 2);
            }
        }
        return xmlContent;
    }

}

package com.tencent.cos.xml.transfer;


import com.tencent.cos.xml.model.tag.BucketLoggingStatus;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.tencent.cos.xml.model.tag.Delete;
import com.tencent.cos.xml.model.tag.InventoryConfiguration;
import com.tencent.cos.xml.model.tag.LifecycleConfiguration;
import com.tencent.cos.xml.model.tag.ReplicationConfiguration;
import com.tencent.cos.xml.model.tag.RestoreConfigure;
import com.tencent.cos.xml.model.tag.VersioningConfiguration;
import com.tencent.cos.xml.model.tag.WebsiteConfiguration;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by bradyxiao on 2017/11/26.
 */

public class XmlBuilder extends XmlSlimBuilder {

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
                addElement(xmlSerializer, "VersionId", deleteObject.versionId);
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

    public static String buildBucketLogging(BucketLoggingStatus bucketLoggingStatus) throws XmlPullParserException, IOException {
        if(bucketLoggingStatus == null) return null;
        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startDocument("UTF-8", null);

        xmlSerializer.startTag("", "BucketLoggingStatus");
        if(bucketLoggingStatus.loggingEnabled != null){
            xmlSerializer.startTag("", "LoggingEnabled");
            addElement(xmlSerializer, "TargetBucket", bucketLoggingStatus.loggingEnabled.targetBucket);
            addElement(xmlSerializer, "TargetPrefix", bucketLoggingStatus.loggingEnabled.targetPrefix);
            xmlSerializer.endTag("", "LoggingEnabled");
        }
        xmlSerializer.endTag("", "BucketLoggingStatus");
        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildWebsiteConfiguration(WebsiteConfiguration websiteConfiguration) throws XmlPullParserException, IOException {
        if(websiteConfiguration == null)return null;
        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        xmlSerializer.startDocument("UTF-8", null);
        xmlSerializer.startTag("", "WebsiteConfiguration");

        if(websiteConfiguration.indexDocument != null){
            xmlSerializer.startTag("", "IndexDocument");
            addElement(xmlSerializer, "Suffix", websiteConfiguration.indexDocument.suffix);
            xmlSerializer.endTag("", "IndexDocument");
        }

        if(websiteConfiguration.errorDocument != null){
            xmlSerializer.startTag("", "ErrorDocument");
            addElement(xmlSerializer, "Key", websiteConfiguration.errorDocument.key);
            xmlSerializer.endTag("", "ErrorDocument");
        }

        if(websiteConfiguration.redirectAllRequestTo != null){
            xmlSerializer.startTag("", "RedirectAllRequestTo");
            addElement(xmlSerializer, "Protocol", websiteConfiguration.redirectAllRequestTo.protocol);
            xmlSerializer.endTag("", "RedirectAllRequestTo");
        }

        if(websiteConfiguration.routingRules != null && websiteConfiguration.routingRules.size() > 0){
            xmlSerializer.startTag("", "RoutingRules");
            for(WebsiteConfiguration.RoutingRule routingRule : websiteConfiguration.routingRules){
                xmlSerializer.startTag("", "RoutingRule");
                if(routingRule.contidion != null){
                    xmlSerializer.startTag("", "Condition");
                    addElement(xmlSerializer, "HttpErrorCodeReturnedEquals", String.valueOf(routingRule.contidion.httpErrorCodeReturnedEquals));
                    addElement(xmlSerializer, "KeyPrefixEquals", routingRule.contidion.keyPrefixEquals);
                    xmlSerializer.endTag("", "Condition");
                }
                if(routingRule.redirect != null){
                    xmlSerializer.startTag("", "Redirect");
                    addElement(xmlSerializer, "Protocol", routingRule.redirect.protocol);
                    addElement(xmlSerializer, "ReplaceKeyPrefixWith", routingRule.redirect.replaceKeyPrefixWith);
                    addElement(xmlSerializer, "ReplaceKeyWith", routingRule.redirect.replaceKeyWith);
                    xmlSerializer.endTag("", "Redirect");
                }
                xmlSerializer.endTag("", "RoutingRule");
            }
            xmlSerializer.endTag("", "RoutingRules");
        }

        xmlSerializer.endTag("", "WebsiteConfiguration");
        xmlSerializer.endDocument();
        return removeXMLHeader(xmlContent.toString());
    }

    public static String buildInventoryConfiguration(InventoryConfiguration inventoryConfiguration) throws IOException, XmlPullParserException {
        if(inventoryConfiguration != null)return null;
        StringWriter xmlContent = new StringWriter();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
        xmlSerializer.setOutput(xmlContent);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        xmlSerializer.startDocument("UTF-8", null);
        xmlSerializer.startTag("", "InventoryConfiguration");
        if(inventoryConfiguration.id != null)addElement(xmlSerializer, "Id", inventoryConfiguration.id);
        addElement(xmlSerializer, "IsEnabled", inventoryConfiguration.isEnabled ? "True" : "False");
        if(inventoryConfiguration.destination != null){
            xmlSerializer.startTag("", "Destination");
            if(inventoryConfiguration.destination.cosBucketDestination != null){
                xmlSerializer.startTag("", "COSBucketDestination");
                if(inventoryConfiguration.destination.cosBucketDestination.format != null)
                    addElement(xmlSerializer, "Format", inventoryConfiguration.destination.cosBucketDestination.format);
                if(inventoryConfiguration.destination.cosBucketDestination.accountId != null)
                    addElement(xmlSerializer, "AccountId", inventoryConfiguration.destination.cosBucketDestination.accountId);
                if(inventoryConfiguration.destination.cosBucketDestination.bucket != null)
                    addElement(xmlSerializer, "Bucket", inventoryConfiguration.destination.cosBucketDestination.bucket);
                if(inventoryConfiguration.destination.cosBucketDestination.prefix != null){
                    addElement(xmlSerializer, "Prefix", inventoryConfiguration.destination.cosBucketDestination.prefix);
                }
                if(inventoryConfiguration.destination.cosBucketDestination.encryption != null){
                    xmlSerializer.startTag("", "Encryption");
                    addElement(xmlSerializer, "SSE-COS", inventoryConfiguration.destination.cosBucketDestination.encryption.sSECOS);
                    xmlSerializer.endTag("", "Encryption");
                }
                xmlSerializer.endTag("", "COSBucketDestination");
            }
            xmlSerializer.endTag("", "Destination");
        }
        if(inventoryConfiguration.schedule != null && inventoryConfiguration.schedule.frequency != null){
            xmlSerializer.startTag("", "Schedule");
            addElement(xmlSerializer, "Frequency", inventoryConfiguration.schedule.frequency);
            xmlSerializer.endTag("", "Schedule");
        }
        if(inventoryConfiguration.filter != null && inventoryConfiguration.filter.prefix != null){
            xmlSerializer.startTag("", "Filter");
            addElement(xmlSerializer, "Prefix", inventoryConfiguration.filter.prefix);
            xmlSerializer.endTag("", "Filter");
        }
        if(inventoryConfiguration.includedObjectVersions != null){
            addElement(xmlSerializer, "IncludeObjectVersions", inventoryConfiguration.includedObjectVersions);
        }
        if(inventoryConfiguration.optionalFields != null && inventoryConfiguration.optionalFields.fields != null){
            xmlSerializer.startTag("", "OptionalFields");
            for(String field : inventoryConfiguration.optionalFields.fields){
                addElement(xmlSerializer, "Field", field);
            }
            xmlSerializer.endTag("", "OptionalFields");
        }
        xmlSerializer.endTag("", "InventoryConfiguration");
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

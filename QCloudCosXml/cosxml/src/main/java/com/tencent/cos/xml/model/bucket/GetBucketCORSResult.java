package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * 获取Bucket的跨域规则CORS结果类
 * </p>
 */
final public class GetBucketCORSResult extends CosXmlResult {

   @XStreamAlias( "CORSConfiguration")
   public CORSConfiguration corsConfiguration;

   @Override
   public String printBody() {
      return corsConfiguration != null ? corsConfiguration.toString() : super.printBody();
   }
}

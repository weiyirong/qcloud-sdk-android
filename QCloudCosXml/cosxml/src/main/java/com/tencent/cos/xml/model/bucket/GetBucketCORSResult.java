package com.tencent.cos.xml.model.bucket;


import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.tag.CORSConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bradyxiao on 2017/4/6.
 * author bradyxiao
 */
public class GetBucketCORSResult extends CosXmlResult {
   @XStreamAlias( "CORSConfiguration")
   public CORSConfiguration corsConfiguration;
   @Override
   public String printBody() {
      return corsConfiguration != null ? corsConfiguration.toString() : super.printBody();
   }
}

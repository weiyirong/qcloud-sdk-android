package com.tencent.cos.xml.model.tag;

import java.util.List;

public class InventoryConfiguration {

    /** 清单的名称，与请求参数中的 id 对应 */
    public String id;

    /** 清单是否启用的标识。如果设置为 True，清单功能将生效；如果设置为 False，将不生成任何清单 */
    public boolean isEnabled;

    /** 是否在清单中包含对象版本 */
    public IncludedObjectVersions includedObjectVersions;

    /** 筛选待分析对象。清单功能将分析符合 Filter 中设置的前缀的对象 */
    public Filter filter;

    /** 设置清单结果中应包含的分析项目 */
    public OptionalFields optionalFields;

    /** 配置清单任务周期 */
    public Schedule schedule;

    /** 描述存放清单结果的信息 */
    public Destination destination;

    public enum IncludedObjectVersions{
        /** 清单中将会包含所有对象版本, 并在清单中增加 VersionId，IsLatest，DeleteMarker 这几个字段 */
        ALL("ALL"),
        /** 清单中不包含对象版本信息 */
        CURRENT("Current");

        private String desc;

        IncludedObjectVersions(String desc){
            this.desc = desc;
        }

        public String getDesc(){
            return this.desc;
        }
    }

    public static class Filter{
        public String prefix;
    }

    public static class OptionalFields{
        public List<String> field;
    }

    public static class Schedule{
        public String frequency;
    }

    public static class Destination{
        public COSBucketDestination cosBucketDestination;
    }

    public static class COSBucketDestination{
        public String format;
        public String accountId;
        public String bucket;
        public String prefix;
        public Encryption encryption;
    }

    public static class Encryption{
        public String sSECOS;
    }





}

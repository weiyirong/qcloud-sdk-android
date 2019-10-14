package com.tencent.cos.xml.model.tag;

import java.util.List;

/**
 * Created by bradyxiao on 2019/9/3.
 * Copyright (c) 2016-2020 Tencent QCloud All rights reserved.
 */
public class DomainConfiguration {
    public List<DomainRule> domainRules;
    public static class DomainRule
    {
        public String status;
        public String name;
        public String type;
        public String forcedReplacement;
    }
}

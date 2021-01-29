package com.jimei.glow.common.core.sql;

import java.util.Map;

/**
 * @Author yudm
 * @Date 2021/1/12 14:33
 * @Desc
 */
public class PackageGroupMatcher {
    private Map<String, String> packGroup;

    public PackageGroupMatcher(Map<String, String> packGroup) {
        this.packGroup = packGroup;
    }

    public String getGroup(String pack) {
        for (Map.Entry<String, String> entry : packGroup.entrySet()) {
            if (pack.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}

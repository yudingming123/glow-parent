package com.jimei.silence.core.sql;

import java.util.Map;

/**
 * @Author yudm
 * @Date 2021/1/12 14:33
 * @Desc
 */
public class SilenceConfig {
    private Map<String, String> packGroup;

    public SilenceConfig(Map<String, String> packGroup) {
        this.packGroup = packGroup;
    }

    public String getGroup(String pack) {
        for (Map.Entry<String, String> entry : packGroup.entrySet()) {
            if (pack.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}

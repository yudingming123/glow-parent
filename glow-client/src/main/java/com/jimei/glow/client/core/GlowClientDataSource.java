package com.jimei.glow.client.core;

import lombok.Data;

import java.util.Map;

/**
 * @Author yudm
 * @Date 2021/1/12 11:13
 * @Desc
 */
@Data
public class GlowClientDataSource {
    private Map<String, GlowHttpClient> groupGlowHttpClient;

    public GlowClientDataSource(Map<String, GlowHttpClient> gGhc) {
        this.groupGlowHttpClient = gGhc;
    }

    public GlowHttpClient getGlowHttpClient(String group) {
        return groupGlowHttpClient.get(group);
    }
}

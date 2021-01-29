package com.jimei.glow.client.config;

import lombok.Data;


/**
 * @Author yudm
 * @Date 2020/12/12 14:38
 * @Desc glow-client的配置信息类
 */
@Data
public class GlowSecurityProperty {
    private boolean enable = false;
    private String secretKey;
    private String license;
}

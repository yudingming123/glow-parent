package com.tm.silence.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author yudm
 * @Date 2021/6/1 14:22
 * @Desc
 */
@Data
@ConfigurationProperties(prefix = "silence")
public class SilenceProperty {
    private boolean enable;
}

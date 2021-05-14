package com.jimei.silence.config;

import com.jimei.glow.common.property.GlowDataSourceProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author yudm
 * @Date 2021/1/27 14:03
 * @Desc
 */
@ConfigurationProperties(prefix = "spring.datasource")
public class SilenceDataSourceProperty extends GlowDataSourceProperty {
}

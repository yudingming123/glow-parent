package com.jimei.glow.server.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2020/12/12 11:00
 * @Desc glow-server的配置信息类
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class GlowDataSourceProperty {

    private Map<String, List<Property>> group;
}

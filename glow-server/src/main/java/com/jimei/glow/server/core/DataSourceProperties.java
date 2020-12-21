package com.jimei.glow.server.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author yudm
 * @Date 2020/12/12 11:00
 * @Desc glow-server的配置信息类
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {
    private List<Properties> dbs;

    public List<Properties> getPropertiesList() {
        return dbs;
    }

    public void setPropertiesList(List<Properties> propertiesList) {
        this.dbs = propertiesList;
    }
}

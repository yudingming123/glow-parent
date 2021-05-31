package com.jimei.silence.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


/**
 * @Author yudm
 * @Date 2020/12/12 11:00
 * @Desc
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperty {
    /**
     * hikari连接池的配置
     */
    private HikariConfig hikari;

    /**
     * 分组数据源配置
     */
    private Map<String, Property> group;

}

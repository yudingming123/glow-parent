package com.jimei.silence.config;

import com.jimei.glow.common.core.datasource.SilenceRoutingDataSource;
import com.jimei.glow.common.property.MetaProperty;
import com.jimei.glow.common.property.GroupProperty;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2021/1/27 11:25
 * @Desc
 */
@Configuration
@EnableConfigurationProperties(SilenceDataSourceProperty.class)
public class SilenceAutoConfig {
    @Resource
    private SilenceDataSourceProperty silenceDataSourceProperty;

    @Bean
    public SilenceRoutingDataSource glowRoutingDataSource() {
        //DataSource组
        Map<String, List<DataSource>> groupDataSources = new HashMap<>();
        for (Map.Entry<String, GroupProperty> entry : silenceDataSourceProperty.getGroup().entrySet()) {
            List<DataSource> dataSources = new ArrayList<>();
            for (MetaProperty property : entry.getValue().getMetas()) {
                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(property.getUrl());
                dataSource.setUsername(property.getUsername());
                dataSource.setPassword(property.getPassword());
                dataSource.setDriverClassName(property.getDriverClassName());
                dataSources.add(dataSource);
            }
            groupDataSources.put(entry.getKey(), dataSources);
        }
        return new SilenceRoutingDataSource(groupDataSources);
    }
}

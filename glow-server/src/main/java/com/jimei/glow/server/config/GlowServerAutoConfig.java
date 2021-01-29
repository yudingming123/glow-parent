package com.jimei.glow.server.config;

import com.jimei.glow.server.core.GlowRoutingDataSource;
import com.jimei.glow.server.core.GlowTransactionManager;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2020/12/12 14:05
 * @Desc
 */
@Configuration
@EnableConfigurationProperties(GlowServerDataSourceProperty.class)
public class GlowServerAutoConfig {
    @Resource
    private GlowServerDataSourceProperty glowServerDataSourceProperty;

    @Bean
    public GlowRoutingDataSource glowRoutingDataSource() throws SQLException {
        //DataSource组
        Map<String, List<DataSource>> dataSourcesGroup = new HashMap<>();
        for (Map.Entry<String, List<Property>> entry : glowServerDataSourceProperty.getGroup().entrySet()) {
            List<DataSource> dataSources = new ArrayList<>();
            for (Property property : entry.getValue()) {
                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(property.getUrl());
                dataSource.setUsername(property.getUsername());
                dataSource.setPassword(property.getPassword());
                dataSource.setDriverClassName(property.getDriverClassName());
                dataSources.add(dataSource);
            }
            dataSourcesGroup.put(entry.getKey(), dataSources);
        }
        return new GlowRoutingDataSource(dataSourcesGroup);
    }

    @Bean
    public GlowTransactionManager glowTransactionManager() {
        return new GlowTransactionManager(glowServerDataSourceProperty.getTransactionPeriod());
    }

}

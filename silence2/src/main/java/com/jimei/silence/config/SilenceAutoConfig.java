package com.jimei.silence.config;

import com.jimei.silence.core.datasource.DataSourceManager;
import com.jimei.silence.core.sql.SqlExecutor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@EnableConfigurationProperties(DataSourceProperty.class)
@ComponentScan({"com.jimei.silence"})
public class SilenceAutoConfig {
    @Resource
    private DataSourceProperty dataSourceProperty;

    @Bean
    public DataSourceManager dataSourceManager() {
        //DataSource组
        Map<String, List<DataSource>> groupDataSources = new HashMap<>();
        Map<String, String> packGroup = new HashMap<>();
        HikariConfig config = dataSourceProperty.getHikari();
        for (Map.Entry<String, Property> entry : dataSourceProperty.getGroup().entrySet()) {
            Property property = entry.getValue();
            List<DataSource> dataSources = new ArrayList<>();
            config.setUsername(property.getUsername());
            config.setPassword(property.getPassword());
            config.setDriverClassName(property.getDriverClassName());
            for (String url : property.getJdbcUrls()) {
                config.setJdbcUrl(url);
                HikariDataSource dataSource = new HikariDataSource(config);
                dataSources.add(dataSource);
            }
            groupDataSources.put(entry.getKey(), dataSources);
            packGroup.put(property.getMapperScan(), entry.getKey());
        }
        return new DataSourceManager(groupDataSources, packGroup);
    }
}

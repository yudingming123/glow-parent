package com.jimei.glow.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
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
    /**
     * 连接池类型
     */
    private Class<? extends DataSource> type;
    /**
     * JDBC driver
     */
    private String driverClassName;
    /**
     * JDBC url 地址
     */
    private String url;
    /**
     * JDBC 用户名
     */
    private String username;
    /**
     * JDBC 密码
     */
    private String password;
    private int transactionPeriod;
    /**
     * 分组数据源配置
     */
    private Map<String, List<Property>> group;
}

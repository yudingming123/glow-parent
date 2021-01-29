package com.jimei.glow.server.config;

import com.zaxxer.hikari.HikariConfig;
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
public class GlowServerDataSourceProperty {
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 认证信息，所有group公用
     */
    private String license;
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
    private boolean autoCommit = false;
    private int connectionTimeout = 30 * 1000;
    private int idleTimeout = 600 * 1000;
    private int maxLifetime = 1800 * 1000;
    private int minimumIdle = 10;
    private int maximumPoolSize = 10;
    private Object metricRegistry = null;
    private Object healthCheckRegistry = null;
    private String poolName = "HikariPool-1";
    private int initializationFailTimeout = 1;
    private boolean isolateInternalQueries = false;
    private boolean allowPoolSuspension = false;
    private boolean readOnly = false;
    private boolean registerMbeans = false;
    private Object catalog = null;
    private String connectionInitSql = null;
    private String transactionIsolation = null;
    private int validationTimeout = 5 * 1000;
    private int leakDetectionThreshold = 0;
    private Object dataSource = null;
    private String schema = null;
    private Object threadFactory = null;
    private Object scheduledExecutor = null;

    private int transactionPeriod;
    /**
     * 分组数据源配置
     */
    private Map<String, List<Property>> group;
}

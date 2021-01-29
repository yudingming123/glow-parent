package com.jimei.glow.common.property;

import lombok.Data;

/**
 * @Author yudm
 * @Date 2021/1/27 13:52
 * @Desc
 */
@Data
public class HikariProperty {
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
}

package com.jimei.glow.common.core.datasource;

import com.jimei.glow.common.core.exception.GlowSqlException;
import com.jimei.glow.common.core.transaction.GlowTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2020/12/29 16:23
 * @Desc 获取和释放连接相关工具类
 */
public class GlowDataSourceUtil {
    private static GlowRoutingDataSource ds;
    private static GlowTransactionManager tm;

    @Resource
    public void setGlowRoutingDataSource(GlowRoutingDataSource glowRoutingDataSource) {
        GlowDataSourceUtil.ds = glowRoutingDataSource;
    }

    @Resource
    public void setGlowTransactionManager(GlowTransactionManager glowTransactionManager) {
        GlowDataSourceUtil.tm = glowTransactionManager;
    }

    public static Map<Connection, DataSource> getCnDsMap(String trsId, String group) {
        Map<Connection, DataSource> cnDsMap;
        cnDsMap = tm.getCnDsMap(trsId, group);
        if (null == cnDsMap) {
            cnDsMap = getCnDsMap(group);
            //加入到事务管理中
            tm.addTransaction(trsId, group, cnDsMap);
        }
        return cnDsMap;
    }

    public static Map<Connection, DataSource> getCnDsMap(String group) {
        List<DataSource> dataSources = ds.getGroupDataSource(group);
        Map<Connection, DataSource> cnDsMap = new HashMap<>();
        for (DataSource dataSource : dataSources) {
            try {
                Connection cn = dataSource.getConnection();
                cn.setAutoCommit(false);
                cnDsMap.put(cn, dataSource);
            } catch (SQLException t) {
                throw new GlowSqlException(t);
            }
        }
        return cnDsMap;
    }

    public static Connection getConnection(String group) {
        try {
            return ds.getDataSource(group).getConnection();
        } catch (SQLException t) {
            throw new GlowSqlException(t);
        }
    }


    public static void release(Map<Connection, DataSource> cnDsMap) {
        for (Map.Entry<Connection, DataSource> entry : cnDsMap.entrySet()) {
            try {
                entry.getKey().close();
            } catch (SQLException t) {
                throw new GlowSqlException(t);
            }
        }
    }
}

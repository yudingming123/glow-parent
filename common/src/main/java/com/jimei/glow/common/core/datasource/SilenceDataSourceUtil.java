package com.jimei.glow.common.core.datasource;

import com.jimei.glow.common.core.exception.SqlException;
import com.jimei.glow.common.core.transaction.SilenceTransactionManager;

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
public class SilenceDataSourceUtil {
    private static SilenceRoutingDataSource ds;
    private static SilenceTransactionManager tm;

    @Resource
    public void setSilenceRoutingDataSource(SilenceRoutingDataSource silenceRoutingDataSource) {
        SilenceDataSourceUtil.ds = silenceRoutingDataSource;
    }

    @Resource
    public void setSilenceTransactionManager(SilenceTransactionManager silenceTransactionManager) {
        SilenceDataSourceUtil.tm = silenceTransactionManager;
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
                throw new SqlException(t);
            }
        }
        return cnDsMap;
    }

    public static Connection getConnection(String group) {
        try {
            return ds.getDataSource(group).getConnection();
        } catch (SQLException t) {
            throw new SqlException(t);
        }
    }


    public static void release(Map<Connection, DataSource> cnDsMap) {
        for (Map.Entry<Connection, DataSource> entry : cnDsMap.entrySet()) {
            try {
                entry.getKey().close();
            } catch (SQLException t) {
                throw new SqlException(t);
            }
        }
    }
}

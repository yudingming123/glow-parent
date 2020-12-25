package com.jimei.glow.server.core;

import com.google.common.base.CaseFormat;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2020/12/7 15:54
 * @Desc Sql语句执行者
 */
//@Component
public class SqlExecutor {
    @Resource
    private GlowRoutingDataSource glowRoutingDataSource;

    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [sql, values]
     * @Desc 执行update操作
     */
    public int executeUpdate(String group, String sql, List<Object> params) {
        List<Connection> cns = getConnections(group);
        PreparedStatement pst = null;
        try {
            int res = 0;
            //向每一个数据库都执行写操作
            for (Connection cn : cns) {
                pst = cn.prepareStatement(sql);
                //将属性值设置到sql中的占位符中
                fillPlaceholder(pst, params);
                res = pst.executeUpdate();
            }
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //此处连接肯定要由事务管理，因此不用手动释放，只需要释放pst即可
            close(pst, null);
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [clazz, sql, values]
     * @Desc 执行query操作
     */
    public List<Map<String, Object>> executeQuery(String group, String sql, List<Object> params) {
        DataSource ds = glowRoutingDataSource.getDataSource(group);
        Connection cn = DataSourceUtils.getConnection(ds);
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = cn.prepareStatement(sql);
            //将属性值设置到sql中的占位符中
            if (null != params && params.size() > 0) {
                fillPlaceholder(pst, params);
            }
            rs = pst.executeQuery();
            return parsResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //不由事务管理需要手动释放连接，使之在连接池中处于空闲状态
            DataSourceUtils.releaseConnection(cn, ds);
            close(pst, rs);
        }
    }

    /**
     * @Author yudm
     * @Date 2020/12/25 15:55
     * @Param [group]
     * @Desc 获取group对应的连接池组对应的连接组
     **/
    private List<Connection> getConnections(String group) {
        List<DataSource> dataSources = glowRoutingDataSource.getGroupDataSource(group);
        List<Connection> connections = new ArrayList<>();
        for (DataSource dataSource : dataSources) {
            connections.add(DataSourceUtils.getConnection(dataSource));
        }
        return connections;
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:48
     * @Param [statement, values]
     * @Desc 向sql的占位符中填充值
     */
    private static void fillPlaceholder(PreparedStatement pst, List<Object> values) throws SQLException {
        if (null == values || values.size() < 1) {
            return;
        }
        for (int i = 0; i < values.size(); ++i) {
            pst.setObject(i + 1, values.get(i));
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:33
     * @Param [clazz, resultSet]
     * @Desc 将ResultSet转化成对应的实体类集合
     */
    private List<Map<String, Object>> parsResultSet(ResultSet rs) {
        if (null == rs) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        try {

            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            //跳过表头
            while (rs.next()) {
                for (int i = 1; i <= count; ++i) {
                    //获取表中字段的名字并转为小写
                    String colName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, md.getColumnName(i));
                    map.put(colName, rs.getObject(i));
                }
                list.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:47
     * @Param [connection, statement]
     * @Desc 关闭资源, 当Connection是从连接池中来的时候，必须要关闭，传null。
     */
    private void close(PreparedStatement st, ResultSet rs) {
        try {
            if (null != st) {
                st.close();
            }
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

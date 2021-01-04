package com.jimei.glow.server.core;

import com.google.common.base.CaseFormat;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @Author yudm
 * @Date 2020/12/7 15:54
 * @Desc Sql语句执行者
 */
@Component
public class GlowSqlExecutor {
    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [sql, values]
     * @Desc 执行update操作
     */
    public int update(String group, String sql, List<Object> params) {
        Map<Connection, DataSource> cnDsMap = GlowDataSourceUtil.getCnDsMap(group);
        try {
            int res = update(cnDsMap.keySet(), sql, params);
            commit(cnDsMap.keySet());
            return res;
        } catch (SQLException e) {
            rollback(cnDsMap.keySet());
            throw new GlowSqlException(e);
        } finally {
            //不由事务管理，需要手动释放cn
            GlowDataSourceUtil.release(cnDsMap);
        }
    }

    public int update(String trsId, String group, String sql, List<Object> params) {
        Map<Connection, DataSource> cnDsMap = GlowDataSourceUtil.getCnDsMap(trsId, group);
        try {
            return update(cnDsMap.keySet(), sql, params);
        } catch (SQLException e) {
            rollback(cnDsMap.keySet());
            throw new GlowSqlException(e);
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [clazz, sql, values]
     * @Desc 执行query操作
     */
    public List<Map<String, Object>> query(String group, String sql, List<Object> params) {
        Connection cn = GlowDataSourceUtil.getConnection(group);
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = cn.prepareStatement(sql);
            //将属性值设置到sql中的占位符中
            if (null != params && params.size() > 0) {
                fillPst(pst, params);
            }
            rs = pst.executeQuery();
            return parsRs(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            //不由事务管理需要手动释放连接，使之在连接池中处于空闲状态
            close(cn, pst, rs);
        }
    }

    private int update(Collection<Connection> cns, String sql, List<Object> params) throws SQLException {
        PreparedStatement pst = null;
        try {
            int res = 0;
            for (Connection cn : cns) {
                pst = cn.prepareStatement(sql);
                //将属性值设置到sql中的占位符中
                fillPst(pst, params);
                res = pst.executeUpdate();
            }
            return res;
        } finally {
            //释放pst
            close(null, pst, null);
        }
    }

    private void commit(Collection<Connection> cns) {
        for (Connection cn : cns) {
            try {
                cn.commit();
            } catch (SQLException e) {
                throw new GlowSqlException(e);
            }
        }
    }

    private void rollback(Collection<Connection> cns) {
        for (Connection cn : cns) {
            try {
                cn.rollback();
            } catch (SQLException e) {
                throw new GlowSqlException(e);
            }
        }
    }


    /**
     * @Author yudm
     * @Date 2020/9/25 15:48
     * @Param [statement, values]
     * @Desc 向sql的占位符中填充值
     */
    private static void fillPst(PreparedStatement pst, List<Object> params) throws SQLException {
        if (null != params && params.size() > 0) {
            for (int i = 0; i < params.size(); ++i) {
                pst.setObject(i + 1, params.get(i));
            }
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:33
     * @Param [clazz, resultSet]
     * @Desc 将ResultSet转化成对应的实体类集合
     */
    private List<Map<String, Object>> parsRs(ResultSet rs) {
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
    private void close(Connection cn, Statement st, ResultSet rs) {
        try {
            if (null != cn) {
                cn.close();
            }
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

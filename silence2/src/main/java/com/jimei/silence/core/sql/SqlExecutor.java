package com.jimei.silence.core.sql;

import com.google.common.base.CaseFormat;
import com.jimei.silence.config.DataSourceProperty;
import com.jimei.silence.core.datasource.DataSourceManager;
import com.jimei.silence.core.exception.SqlException;
import com.jimei.silence.util.SpringContextUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;

/**
 * @Author yudm
 * @Date 2021/5/13 17:40
 * @Desc
 */
public class SqlExecutor {
    private final DataSourceProperty dataSourceProperty = SpringContextUtil.getBean(DataSourceProperty.class);
    private final DataSourceManager dataSourceManager = SpringContextUtil.getBean(DataSourceManager.class);

    public int update(String pack, String sql, List<Object> values) {
        Collection<Connection> cns = dataSourceManager.getConnections(pack);
        PreparedStatement pst = null;
        int res = 0;
        try {
            for (Connection cn : cns) {
                pst = cn.prepareStatement(sql);
                fillPst(pst, values);
                res = pst.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SqlException(e);
        } finally {
            release(pst, null);
        }
        return res;
    }

    public int[] saveBatch(String pack, String sql, List<List<Object>> values) {
        Collection<Connection> cns = dataSourceManager.getConnections(pack);
        PreparedStatement pst = null;
        int[] res = {};
        try {
            for (Connection cn : cns) {
                pst = cn.prepareStatement(sql);
                for (List<Object> value : values) {
                    fillPst(pst, value);
                    pst.addBatch();
                }
                res = pst.executeBatch();
            }
        } catch (SQLException e) {
            throw new SqlException(e);
        } finally {
            release(pst, null);
        }
        return res;
    }

    public <T> List<T> query(String pack, String sql, List<Object> values, Class<T> clazz) {
        Connection cn = dataSourceManager.getConnection(pack);
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<T> list;
        try {
            pst = cn.prepareStatement(sql);
            fillPst(pst, values);
            rs = pst.executeQuery();
            list = parsRs(rs, clazz);
        } catch (Exception e) {
            throw new SqlException(e);
        } finally {
            release(pst, rs);
        }
        return list;
    }

    public List<Map<String, Object>> query(String pack, String sql, List<Integer> types, List<Object> values) {
        Connection cn = dataSourceManager.getConnection(pack);
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = null;
        try {
            pst = cn.prepareStatement(sql);
            fillPst(pst, values);
            rs = pst.executeQuery();
            list = parsRs(rs);
        } catch (Exception e) {
            throw new SqlException(e);
        } finally {
            release(pst, rs);
        }
        return list;
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:48
     * @Param [statement, values]
     * @Desc 向sql的占位符中填充值
     */
    private static void fillPst(PreparedStatement pst, List<Object> values) throws SQLException {
        if (null != values && values.size() > 0) {
            for (int i = 0; i < values.size(); ++i) {
                pst.setObject(i + 1, values.get(i));
            }
        }
    }

    private <T> List<T> parsRs(ResultSet rs, Class<T> clazz) throws Exception {
        if (null == rs) {
            return null;
        }
        List<T> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> fMap = new HashMap<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fMap.put(field.getName(), field);
            }
        }
        T t = clazz.newInstance();
        ResultSetMetaData md = rs.getMetaData();
        int count = md.getColumnCount();
        //跳过表头
        while (rs.next()) {
            for (int i = 1; i <= count; ++i) {
                String colName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, md.getColumnName(i));
                Field field = fMap.get(colName);
                if (null != field) {
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    //从rs中获取值不要勇字段名获取，性能会很低
                    field.set(t, rs.getObject(i));
                    field.setAccessible(flag);
                }
            }
            list.add(t);
        }
        return list;
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:33
     * @Param [clazz, resultSet]
     * @Desc 将ResultSet转化成对应的实体类集合
     */
    private List<Map<String, Object>> parsRs(ResultSet rs) throws SQLException {
        if (null == rs) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

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

        return list;
    }

    private void release(Statement st, ResultSet rs) {
        try {
            if (null != st) {
                st.close();
            }
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }

}

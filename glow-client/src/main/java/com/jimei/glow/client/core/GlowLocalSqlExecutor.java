package com.jimei.glow.client.core;

import com.google.common.base.CaseFormat;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;

/**
 * @Author yudm
 * @Date 2020/12/7 15:54
 * @Desc Sql语句执行者
 */
@Component
public class GlowLocalSqlExecutor {
    @Resource
    private DataSource ds;
    @Resource
    private SqlSessionTemplate st;

    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [sql, values]
     * @Desc 执行update操作
     */
    public int update(String sql, List<Object> values) {
        Connection cn = DataSourceUtils.getConnection(ds);
        PreparedStatement pst = null;
        try {
            pst = cn.prepareStatement(sql);
            //将属性值设置到sql中的占位符中
            fillPst(pst, values);
            return pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(cn, ds);
            close(pst, null);
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [sql, value]
     * @Desc 执行update操作
     */
    public int update(String sql, Object value) {
        Connection cn = DataSourceUtils.getConnection(ds);
        PreparedStatement pst = null;
        try {
            pst = cn.prepareStatement(sql);
            //将属性值设置到sql中的占位符中
            pst.setObject(1, value);
            return pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(pst, null);
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [clazz, sql, values]
     * @Desc 执行query操作
     */
    public <T> List<T> query(Class<T> clazz, String sql, List<Object> values) {
        Connection cn = DataSourceUtils.getConnection(ds);
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = cn.prepareStatement(sql);
            //将属性值设置到sql中的占位符中
            if (null != values && values.size() > 0) {
                fillPst(pst, values);
            }
            rs = pst.executeQuery();
            if (null == rs) {
                return null;
            }
            return parsRs(clazz, rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(cn, ds);
            close(pst, rs);
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:33
     * @Param [clazz, resultSet]
     * @Desc 将ResultSet转化成对应的实体类集合
     */
    private <T> List<T> parsRs(Class<T> clazz, ResultSet rs) {
        List<T> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> fMap = new HashMap<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fMap.put(field.getName(), field);
            }
        }
        try {
            T t = clazz.newInstance();
            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            //跳过表头
            while (rs.next()) {
                for (int i = 1; i <= count; ++i) {
                    //获取表中字段的名字并转为小写
                    String colName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, md.getColumnName(i));
                    Field field = fMap.get(colName);
                    if (null != field) {
                        boolean flag = field.isAccessible();
                        field.setAccessible(true);
                        field.set(t, rs.getObject(i));
                        field.setAccessible(flag);
                    }
                }
                list.add(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:48
     * @Param [statement, values]
     * @Desc 向sql的占位符中填充值
     */
    private void fillPst(PreparedStatement pst, List<Object> values) throws SQLException {
        if (null == values || values.size() < 1) {
            return;
        }
        for (int i = 0; i < values.size(); ++i) {
            pst.setObject(i + 1, values.get(i));
        }
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:47
     * @Param [connection, statement]
     * @Desc 关闭资源, 当Connection是从连接池中来的时候，必须要关闭，传null。
     */
    private void close(Statement st, ResultSet rs) {
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

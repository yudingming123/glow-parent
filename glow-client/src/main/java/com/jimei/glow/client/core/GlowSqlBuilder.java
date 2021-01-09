package com.jimei.glow.client.core;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @Author yudm
 * @Date 2021/1/1 13:16
 * @Desc
 */
public class GlowSqlBuilder {
    /**
     * @Author yudm
     * @Date 2020/9/25 15:50
     * @Param [entity, columns, values]
     * @Desc 将实体类中的所有非静态非null的字段名和值解析到columns和values
     */
    public <T> void parsToCVSelective(T entity, List<String> columns, List<Object> values) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Object obj;
            try {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                obj = field.get(entity);
                field.setAccessible(flag);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (null != obj) {
                columns.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
                values.add(obj);
            }
        }
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:49
     * @Param [tableName, columns]
     * @Desc 构建插入sql
     */
    public String buildInsertSql(String tableName, List<String> columns) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        for (String column : columns) {
            sql.append(column).append(",");
        }
        //去掉最后一个“,”号
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") VALUES (");
        for (int i = 0; i < columns.size(); ++i) {
            sql.append("?,");
        }
        //去掉最后一个“,”号
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return sql.toString();
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:49
     * @Param [tableName, columns]
     * @Desc 构建更新sql
     */
    public String buildUpdateSql(String tableName, List<String> columns) {
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (String column : columns) {
            sql.append(column).append("=?,");
        }
        //去掉最后一个“,”号
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" where ").append(columns.get(0)).append("=?");
        return sql.toString();
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:49
     * @Param [tableName, columns]
     * @Desc 构建删除sql
     */
    public String buildDeleteSql(String tableName, String keyName) {
        return "DELETE FROM " + tableName + " WHERE " + keyName + "=?";
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:49
     * @Param [tableName, columns]
     * @Desc 构建查询sql
     */
    public String buildSelectSql(String tableName, List<String> columns) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);
        if (null == columns || columns.size() < 1) {
            return sql.toString();
        }
        sql.append(" WHERE ");
        for (String column : columns) {
            sql.append(column).append("=? AND ");
        }
        //去掉最后一个空格和AND号
        sql.delete(sql.length() - 5, sql.length() - 1);
        return sql.toString();
    }

}

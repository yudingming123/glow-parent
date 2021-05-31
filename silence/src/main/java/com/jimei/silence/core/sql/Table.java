package com.jimei.silence.core.sql;


import com.google.common.base.CaseFormat;
import com.jimei.silence.core.exception.SqlException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2020/9/19 17:32
 * @Desc 用于和数据库表进行映射的类，提供一些简单通用的CURD操作，同时也可以执行xml中的自定义SQL语句。
 */
public class Table {
    private static final SqlExecutor sqlExecutor = new SqlExecutor();
    private static final SqlBuilder sqlBuilder = new SqlBuilder();


    /**
     * @Author yudm
     * @Date 2020/9/20 15:24
     * @Param [entity 实体类对象，用于关联某张表同时也是入参]
     * @Desc 通用添加，null值也会写入。
     */
    public static <T> int insert(T entity) {
        return doInsert(getPackagePath(), entity, false);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 15:25
     * @Param [entity 实体类对象，用于关联某张表同时也是入参]
     * @Desc 通用添加，null值不写入。
     */
    public static <T> int insertSelective(T entity) {
        return doInsert(getPackagePath(), entity, true);
    }

    public static <T> int[] insertList(List<T> entities) {
        return doInsertList(getPackagePath(), entities, false);
    }

    public static <T> int[] insertListSelective(List<T> entities) {
        return doInsertList(getPackagePath(), entities, true);
    }

    public static <T> int updateById(T entity) {
        return doUpdate(getPackagePath(), entity, false);
    }

    public static <T> int updateByIdSelective(T entity) {
        return doUpdate(getPackagePath(), entity, true);
    }

    public static <T> int deleteById(Class<T> clazz, Object id) {
        return sqlExecutor.update(getPackagePath(), sqlBuilder.buildDeleteSql(clazz.getSimpleName(), sqlBuilder.getColumns(clazz).get(0)), new ArrayList<Object>() {{add(id);}});
    }


    public static int execute(String pSql, Object data) {
        if (null == data) {
            return sqlExecutor.update(getPackagePath(), sqlBuilder.build(pSql, null, null), null);
        }
        List<Object> values = new ArrayList<>();
        return sqlExecutor.update(getPackagePath(), sqlBuilder.build(pSql, sqlBuilder.toMap(data), values), values);
    }


    public static <T> T selectOne(String pSql, Object data, Class<T> clazz) {
        List<T> list;
        if (null == data) {
            list = sqlExecutor.query(getPackagePath(), sqlBuilder.build(pSql, null, null), null, clazz);
        } else {
            List<Object> values = new ArrayList<>();
            list = sqlExecutor.query(getPackagePath(), sqlBuilder.build(pSql, sqlBuilder.toMap(data), values), values, clazz);
        }
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public static <T> List<T> selectAll(Class<T> clazz) {
        return sqlExecutor.query(getPackagePath(), "select * from " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()), null, clazz);
    }

    public static <T> List<T> selectList(String pSql, Object data, Class<T> clazz) {
        if (null == data) {
            return sqlExecutor.query(getPackagePath(), sqlBuilder.build(pSql, null, null), null, clazz);
        }
        List<Object> values = new ArrayList<>();
        return sqlExecutor.query(getPackagePath(), sqlBuilder.build(pSql, sqlBuilder.toMap(data), values), values, clazz);
    }

    public static <T> Page<T> selectPage(int pageNum, int pageSize, boolean total, Class<T> clazz) {
        Page<T> page = new Page<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
        if (total) {
            page.setTotal(sqlExecutor.query(getPackagePath(), "select count(*) from " + tableName, null, Integer.class).get(0));
        }
        page.setList(sqlExecutor.query(getPackagePath(), "select * from " + tableName + " limit " + (pageNum - 1) + "," + pageSize, null, clazz));
        return page;
    }

    public static <T> Page<T> selectPage(int pageNum, int pageSize, boolean total, String pSql, Object data, Class<T> clazz) {
        Page<T> page = new Page<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        String sql = "";
        List<Object> values = null;
        if (null == data) {
            sql = sqlBuilder.build(pSql, null, null);
        } else {
            values = new ArrayList<>();
            sql = sqlBuilder.build(pSql, sqlBuilder.toMap(data), values);
        }
        if (total) {
            page.setTotal(sqlExecutor.query(getPackagePath(), "select count(*) from (" + sql + ")", null, Integer.class).get(0));
        }
        page.setList(sqlExecutor.query(getPackagePath(), sql + " limit " + (pageNum - 1) + "," + pageSize, values, clazz));
        return page;
    }

    public static int selectCount(Class<?> clazz) {
        List<Integer> list = sqlExecutor.query(getPackagePath(), "select count(*) from " + clazz.getSimpleName(), null, Integer.class);
        return list != null && list.size() > 0 ? list.get(0) : 0;
    }


    private static String getPackagePath() {
        return Thread.currentThread().getStackTrace()[3].getClassName();
    }

    private static <T> int doInsert(String pack, T entity, boolean selective) {
        Map<String, Object> kv = sqlBuilder.toMap(entity);
        String sql = sqlBuilder.buildInsertSql(entity.getClass().getSimpleName(), sqlBuilder.getColumns(kv, selective));
        return sqlExecutor.update(pack, sql, sqlBuilder.getValues(kv, selective));
    }

    private static <T> int[] doInsertList(String pack, List<T> entities, boolean selective) {
        if (null == entities || entities.isEmpty()) {
            throw new SqlException("entities can not be null");
        }
        T t = entities.get(0);
        List<List<Object>> values = new ArrayList<>();
        Map<String, Object> kv = sqlBuilder.toMap(t);
        values.add(sqlBuilder.getValues(kv, selective));
        for (int i = 1; i < entities.size() - 1; ++i) {
            values.add(sqlBuilder.getValues(sqlBuilder.toMap(entities.get(i)), selective));
        }
        String sql = sqlBuilder.buildInsertSql(t.getClass().getSimpleName(), sqlBuilder.getColumns(kv, selective));
        return sqlExecutor.saveBatch(pack, sql, values);
    }

    private static <T> int doUpdate(String pack, T entity, boolean selective) {
        Map<String, Object> kv = sqlBuilder.toMap(entity);
        String sql = sqlBuilder.buildUpdateSql(entity.getClass().getSimpleName(), sqlBuilder.getColumns(kv, selective));
        return sqlExecutor.update(pack, sql, sqlBuilder.getValues(kv, selective));
    }

}

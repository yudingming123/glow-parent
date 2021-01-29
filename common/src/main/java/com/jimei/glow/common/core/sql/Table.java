package com.jimei.glow.common.core.sql;

import com.google.common.base.CaseFormat;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author yudm
 * @Date 2020/9/19 17:32
 * @Desc 用于和数据库表进行映射的类，提供一些简单通用的CURD操作，同时也可以执行xml中的自定义SQL语句。
 */
public class Table {
    @Resource
    private GlowSqlExecutor gle;
    @Resource
    private GlowSqlBuilder gsb;

    /**
     * @Author yudm
     * @Date 2020/9/20 15:24
     * @Param [entity 实体类对象，用于关联某张表同时也是入参]
     * @Desc 通用添加，null值也会写入。
     */
    public <T> int insert(T entity) {
        if (null == entity) {
            return 0;
        }
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<String> columns = parsColumns(fields);
        List<Object> values = parsValues(fields, entity);
        if (columns.size() < 1 || values.size() < 1) {
            return 0;
        }
        String sql = gsb.buildInsertSql(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()), columns);
        return gle.update(sql, values);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 15:25
     * @Param [entity 实体类对象，用于关联某张表同时也是入参]
     * @Desc 通用添加，null值不写入。
     */
    public <T> int insertSelective(T entity) {
        if (null == entity) {
            return 0;
        }
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parsToCVSelective(entity, columns, values);
        if (columns.size() < 1 || values.size() < 1) {
            return 0;
        }
        String sql = gsb.buildInsertSql(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getClass().getSimpleName()), columns);
        return gle.update(sql, values);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 15:27
     * @Param [statementId xml中某条SQL语句的id, entity 实体类对象，用于关联某个xml同时也是入参]
     * @Desc 自定义添加，对应xml中某条SQL语句
     */
    public int insert(String statement, Object obj) {
        if (null == statement || null == obj) {
            return 0;
        }
        return st.insert(statement, obj);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 15:27
     * @Param [clazz 用于关联某个xml, statement 用于关联某条SQL语句]
     * @Desc 自定义添加没有入参，对应xml中某条SQL语句。
     */
    public int insert(String statement) {
        if (null == statement) {
            return 0;
        }
        return st.insert(statement);
    }


    /**
     * @Author yudm
     * @Date 2020/9/20 15:36
     * @Param [entity 实体类对象，用于关联某张表同时也是入参]
     * @Desc 通用更新，默认实体类第一个字段为主键，null值也会写入。
     */
    public <T> int updateByKey(T entity) {
        if (null == entity) {
            return 0;
        }
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<String> columns = parsColumns(fields);
        List<Object> values = parsValues(fields, entity);
        if (columns.size() < 1 || values.size() < 1) {
            return 0;
        }
        String sql = gsb.buildUpdateSql(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()), columns);
        return gle.update(sql, values);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 15:40
     * @Param [entity 实体类对象，用于关联某张表同时也是入参]
     * @Desc 通用更新，默认实体类第一个字段为主键，null值不会写入。
     */
    public <T> int updateByKeySelective(T entity) {
        if (null == entity) {
            return 0;
        }
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parsToCVSelective(entity, columns, values);
        if (columns.size() < 1 || values.size() < 1) {
            return 0;
        }
        String sql = gsb.buildUpdateSql(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getClass().getSimpleName()), columns);
        return gle.update(sql, values);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 15:27
     * @Param [statement 对应xml中某条SQL语句, obj 入参]
     * @Desc 自定义更新，对应xml中某条SQL语句
     */
    public int update(String statement, Object obj) {
        if (null == statement || null == obj) {
            return 0;
        }
        return st.update(statement, obj);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 15:27
     * @Param [clazz 用于关联某个xml, statement 用于关联某条SQL语句]
     * @Desc 自定义更新没有入参，对应xml中某条SQL语句。
     */
    public int update(String statement) {
        if (null == statement) {
            return 0;
        }
        return st.update(statement);
    }


    /**
     * @Author yudm
     * @Date 2020/9/20 16:25
     * @Param [clazz 用于关联某个表, key 主键的值]
     * @Desc 通用删除，默认clazz第一个字段为主键。
     */
    public <T> int deleteByKey(Class<T> clazz, Object obj) {
        if (null == clazz || null == obj) {
            return 0;
        }
        Field[] fields = clazz.getDeclaredFields();
        List<String> columns = parsColumns(fields);
        if (columns.size() < 1) {
            return 0;
        }
        String sql = gsb.buildDeleteSql(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()), columns.get(0));
        return gle.update(sql, obj);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 16:44
     * @Param [clazz 用于关联某个xml, statement 用于关联某条SQL语句, obj 删除条件]
     * @Desc 自定义删除，有删除条件，对应xml中某条SQL语句。
     */
    public int delete(String statement, Object obj) {
        if (null == statement || null == obj) {
            return 0;
        }
        return st.delete(statement, obj);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 16:41
     * @Param [clazz 用于关联某个xml, statement 用于关联某条SQL语句]
     * @Desc 自定义删除，没有删除条件，对应xml中某条SQL语句。
     */
    public int delete(String statement) {
        if (null == statement) {
            return 0;
        }
        return st.delete(statement);
    }


    /**
     * @Author yudm
     * @Date 2020/9/20 17:08
     * @Param [entity 实体类对象，用于关联某张表同时也是查询条件]
     * @Desc 通用查询单个，有查询条件且都用and连接。
     */
    @SuppressWarnings("unchecked")
    public <T> T selectOne(T entity) {
        if (null == entity) {
            return null;
        }
        Class<T> clazz = (Class<T>) entity.getClass();
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parsToCVSelective(entity, columns, values);
        if (columns.size() < 1 || values.size() < 1) {
            return null;
        }
        String sql = gsb.buildSelectSql(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()), columns);
        List<T> list = gle.query(clazz, sql, values);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 17:19
     * @Param [statement 用于关联某条SQL语句, condition 查询条件]
     * @Desc 自定义查询单个，对应xml中一条SQL语句，有查询条件。
     */
    public <T> T selectOne(String statement, Object obj) {
        if (null == statement || obj == null) {
            return null;
        }
        return st.selectOne(statement, obj);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 17:17
     * @Param [statement 用于关联某条SQL语句]
     * @Desc 自定义查询单个，对应xml中一条SQL语句，没有查询条件。
     */
    public <T> T selectOne(String statement) {
        if (null == statement) {
            return null;
        }
        return st.selectOne(statement);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 17:07
     * @Param [clazz 用于关联某张表]
     * @Desc 通用查询所有
     */
    public <T> List<T> selectAll(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return gle.query(clazz, "SELECT * FROM " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()), null);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 17:22
     * @Param [entity 实体类对象，用于关联某个xml同时也是查询条件]
     * @Desc 通用查询多个，有查询条件。
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> selectList(T entity) {
        if (null == entity) {
            return null;
        }
        Class<T> clazz = (Class<T>) entity.getClass();
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        parsToCVSelective(entity, columns, values);
        if (columns.size() < 1 || values.size() < 1) {
            return null;
        }
        String sql = gsb.buildSelectSql(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()), columns);
        return gle.query(clazz, sql, values);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 17:26
     * @Param [statement 用于关联某条SQL语句, obj 查询条件]
     * @Desc 自定义查询多个，对应xml中一条SQL语句，有查询条件。
     */
    public <T> List<T> selectList(String statement, Object obj) {
        if (null == statement || null == obj) {
            return null;
        }
        return st.selectList(statement, obj);
    }

    /**
     * @Author yudm
     * @Date 2020/9/20 17:24
     * @Param [statement 用于关联某条SQL语句]
     * @Desc 自定义查询多个，对应xml中一条SQL语句，没有查询条件。
     */
    public <T> List<T> selectList(String statement) {
        if (null == statement) {
            return null;
        }
        return st.selectList(statement);
    }


    /**
     * @Author yudm
     * @Date 2020/10/4 12:36
     * @Param [fields]
     * @Desc 解析出属性名
     */
    private List<String> parsColumns(Field[] fields) {
        if (fields == null || fields.length < 1) {
            return null;
        }
        List<String> columns = new ArrayList<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            columns.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
        }
        return columns;
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:36
     * @Param [fields, entity]
     * @Desc 解析出对象中属性值
     */
    private <T> List<Object> parsValues(Field[] fields, T entity) {
        if (fields == null || fields.length < 1) {
            return null;
        }
        List<Object> values = new ArrayList<>();
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
            values.add(obj);
        }
        return values;
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:50
     * @Param [entity, columns, values]
     * @Desc 将实体类中的所有非静态非null的字段名和值解析到columns和values
     */
    private <T> void parsToCVSelective(T entity, List<String> columns, List<Object> values) {
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
}

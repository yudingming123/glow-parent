package com.jimei.glow.common.core.sql;

import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2021/1/12 11:23
 * @Desc
 */
public interface ISqlExecutor {
    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [sql, values]
     * @Desc 执行update操作
     */
    int update(String pack, String sql, List<Object> values);

    int saveBatch(String pack, String sql, List<List<Object>> values);

    /**
     * @Author yudm
     * @Date 2020/10/4 12:34
     * @Param [clazz, sql, values]
     * @Desc 执行query操作
     */
    <T> List<T> query(String pack, String sql, List<Object> values, Class<T> clazz);

    List<Map<String, Object>> query(String pack, String sql, List<Integer> types, List<Object> values);
}

package com.jimei.glow.local.core.sql;

import com.jimei.glow.common.core.sql.GlowSqlExecutor;

import java.util.List;
import java.util.Map;

/**
 * @Author yudm
 * @Date 2021/1/27 15:39
 * @Desc
 */
public class GlowLocalSqlExecutor implements GlowSqlExecutor {

    @Override
    public int update(String group, String sql, List<Integer> types, List<Object> values) {
        return 0;
    }

    @Override
    public <T> List<T> query(String group, String sql, List<Integer> types, List<Object> values, Class<T> clazz) {
        return null;
    }

    @Override
    public List<Map<String, Object>> query(String group, String sql, List<Integer> types, List<Object> values) {
        return null;
    }
}

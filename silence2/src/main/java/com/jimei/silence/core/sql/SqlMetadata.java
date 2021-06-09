package com.jimei.silence.core.sql;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yudm
 * @Date 2020/12/20 15:25
 * @Desc 用于记录sql操作的元数据类
 */
public class SqlMetadata implements Serializable {
    private String sql;
    private List<Object> params;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}

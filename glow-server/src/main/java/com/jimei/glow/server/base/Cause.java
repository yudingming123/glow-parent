package com.jimei.glow.server.base;


import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author yudm
 * @Date 2020/12/19 14:40
 * @Desc 入参统一格式
 */
public class Cause {
    //许可证
    String license;
    //集群对应的标签
    String group;
    //操作类型
    @NotEmpty(message = "action不能为空")
    String action;
    //SQL语句
    @NotEmpty(message = "sql语句不能为空")
    String sql;
    //参数
    List<Object> params;

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getgroup() {
        return group;
    }

    public void setgroup(String group) {
        this.group = group;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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

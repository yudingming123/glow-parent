package com.jimei.glow.server.base;


import lombok.Data;

import java.util.List;

/**
 * @Author yudm
 * @Date 2020/12/19 14:40
 * @Desc 入参统一格式
 */
@Data
public class ReqInfo {
    //许可证
    private String license;
    //操作类型
    private int action = -1;
    //事务id
    private String trsId;
    //集群对应的标签
    private String group;
    //SQL语句
    private String sql;
    //sql对应的参数
    private List<Object> params;
}

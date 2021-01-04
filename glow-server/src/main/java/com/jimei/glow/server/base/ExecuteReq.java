package com.jimei.glow.server.base;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author yudm
 * @Date 2020/12/26 19:55
 * @Desc
 */
@Data
public class ExecuteReq {
    //集群对应的标签
    private String group;
    //SQL语句
    @NotEmpty(message = "sql语句不能为空")
    private String sql;
    //sql对应的参数
    private List<Object> params;
}

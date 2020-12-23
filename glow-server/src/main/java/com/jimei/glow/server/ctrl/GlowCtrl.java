package com.jimei.glow.server.ctrl;

import com.jimei.glow.server.base.Cause;
import com.jimei.glow.server.base.ActionType;
import com.jimei.glow.server.base.Result;
import com.jimei.glow.server.core.DataSourceProperties;
import com.jimei.glow.server.core.SqlExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/")
@RestController
public class GlowCtrl {
    @Resource
    private DataSourceProperties dataSourceProperties;
    @Resource
    private SqlExecutor sqlExecutor;

    @GetMapping("/test")
    public DataSourceProperties test() {
        return dataSourceProperties;
    }


    @PostMapping("/execute")
    public Result query(@RequestBody @Validated Cause cause) {
        if (ActionType.WRITE.equals(cause.getAction())) {
            return Result.success(sqlExecutor.executeUpdate(cause.getLabel(), cause.getSql(), cause.getParams()));
        } else if (ActionType.READ.equals(cause.getAction())) {
            return Result.success(sqlExecutor.executeQuery(cause.getLabel(), cause.getSql(), cause.getParams()));
        } else {
            throw new RuntimeException("action不正确");
        }
    }


}












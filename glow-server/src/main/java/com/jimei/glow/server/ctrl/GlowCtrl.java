package com.jimei.glow.server.ctrl;

import com.jimei.glow.server.base.Cause;
import com.jimei.glow.server.base.Result;
import com.jimei.glow.server.core.GlowDataSourceProperty;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Connection;

@RequestMapping("/")
@RestController
public class GlowCtrl {
    @Resource
    private GlowDataSourceProperty dataSourceProperties;
    //@Resource
    //private SqlExecutor sqlExecutor;

    @Resource
    private SqlSessionTemplate sst;

    @GetMapping("/test")
    public GlowDataSourceProperty test() {
        return dataSourceProperties;
    }


    @PostMapping("/execute")
    public Result query(@RequestBody @Validated Cause cause) {
        /*if (ActionType.WRITE.equals(cause.getAction())) {
            return Result.success(sqlExecutor.executeUpdate(cause.getgroup(), cause.getSql(), cause.getParams()));
        } else if (ActionType.READ.equals(cause.getAction())) {
            return Result.success(sqlExecutor.executeQuery(cause.getgroup(), cause.getSql(), cause.getParams()));
        } else {
            throw new RuntimeException("action不正确");
        }*/
        Connection cn = SqlSessionUtils.getSqlSession(sst.getSqlSessionFactory(), sst.getExecutorType(), sst.getPersistenceExceptionTranslator()).getConnection();

        return Result.success(cn);
    }


}












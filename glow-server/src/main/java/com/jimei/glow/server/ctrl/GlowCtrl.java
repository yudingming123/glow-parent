package com.jimei.glow.server.ctrl;

import com.jimei.glow.server.base.ReqInfo;
import com.jimei.glow.server.base.RspInfo;
import com.jimei.glow.server.config.GlowDataSourceProperty;
import com.jimei.glow.server.core.GlowSqlException;
import com.jimei.glow.server.core.GlowTransactionManager;
import com.jimei.glow.server.core.GlowSqlExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping
@RestController
public class GlowCtrl {
    public static final int READ = 0;
    public static final int WRITE = 1;
    public static final int ROLLBACK = 2;
    public static final int COMMIT = 3;
    @Resource
    private GlowDataSourceProperty dataSourceProperties;
    @Resource
    private GlowSqlExecutor glowSqlExecutor;
    @Resource
    private GlowTransactionManager glowTransactionManager;

    @GetMapping("/test")
    public GlowDataSourceProperty test() {
        return dataSourceProperties;
    }

    @PostMapping("/execute")
    public RspInfo<Object> execute(@RequestBody ReqInfo req) {
        String license = req.getLicense();
        String trsId = req.getTrsId();
        int action = req.getAction();
        String group = req.getGroup();
        String sql = req.getSql();
        List<Object> params = req.getParams();
        switch (action) {
            case READ:
                return RspInfo.success(glowSqlExecutor.query(group, sql, params));
            case WRITE:
                if (StringUtils.isEmpty(trsId)) {
                    return RspInfo.success(glowSqlExecutor.update(group, sql, params));
                }
                return RspInfo.success(glowSqlExecutor.update(trsId, group, sql, params));
            case ROLLBACK:
                glowTransactionManager.rollbackOrCommit(trsId, GlowTransactionManager.ROLLBACK);
                return RspInfo.success(null);
            case COMMIT:
                glowTransactionManager.rollbackOrCommit(trsId, GlowTransactionManager.COMMIT);
                return RspInfo.success(null);
            default:
                throw new GlowSqlException("action不正确：" + action);
        }
    }
}












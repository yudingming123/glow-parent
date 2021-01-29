package com.jimei.glow.server.ctrl;

import com.google.gson.*;
import com.jimei.glow.common.base.GlowType;
import com.jimei.glow.common.base.ReqInfo;
import com.jimei.glow.common.base.RspInfo;
import com.jimei.glow.server.config.GlowServerDataSourceProperty;
import com.jimei.glow.server.core.GlowSqlException;
import com.jimei.glow.server.core.GlowSqlExecutor;
import com.jimei.glow.server.core.GlowTransactionManager;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping
@RestController
public class GlowCtrl {
    @Resource
    private GlowServerDataSourceProperty dataSourceProperties;
    @Resource
    private GlowSqlExecutor glowSqlExecutor;
    @Resource
    private GlowTransactionManager glowTransactionManager;

    @GetMapping("/test")
    public GlowServerDataSourceProperty test() {
        return dataSourceProperties;
    }

    private final Gson gson = new GsonBuilder().registerTypeAdapter(ReqInfo.class, (JsonDeserializer<ReqInfo>) (je, type, jdc) -> {
        ReqInfo req = new ReqInfo();
        JsonObject jo = je.getAsJsonObject();
        req.setLicense(jo.get("license").getAsString());
        req.setAction(jo.get("action").getAsInt());
        req.setTrsId(jo.get("trsId").getAsString());
        req.setGroup(jo.get("group").getAsString());
        req.setSql(jo.get("sql").getAsString());
        JsonArray jts = jo.get("types").getAsJsonArray();
        int length = jts.size();
        List<Integer> types = new ArrayList<>(length);
        for (JsonElement j : jts) {
            types.add(j.getAsInt());
        }
        JsonArray jps = jo.get("params").getAsJsonArray();
        List<Object> params = new ArrayList<>(length);
        for (int i = 0; i < length; ++i) {
            switch (types.get(i)) {
                case GlowType.BOOLEAN:
                    params.add(jps.get(i).getAsBoolean());
                    break;
                case GlowType.BYTE:
                    params.add(jps.get(i).getAsByte());
                    break;
                case GlowType.SHORT:
                    params.add(jps.get(i).getAsShort());
                    break;
                case GlowType.CHAR:
                case GlowType.STRING:
                    params.add(jps.get(i).getAsString());
                    break;
                case GlowType.INT:
                    params.add(jps.get(i).getAsInt());
                    break;
                case GlowType.FLOAT:
                    params.add(jps.get(i).getAsFloat());
                    break;
                case GlowType.DOUBLE:
                    params.add(jps.get(i).getAsDouble());
                    break;
                case GlowType.LONG:
                    params.add(jps.get(i).getAsLong());
                    break;
                case GlowType.BIGINT:
                    params.add(jps.get(i).getAsBigInteger());
                    break;
                case GlowType.DECIMAL:
                    params.add(jps.get(i).getAsBigDecimal());
                    break;
                case GlowType.TIME:
                    params.add(new Time(jps.get(i).getAsLong()));
                    break;
                case GlowType.STAMP:
                    params.add(new Timestamp(jps.get(i).getAsLong()));
                    break;
                case GlowType.DATE:
                    params.add(new Date(jps.get(i).getAsLong()));
                    break;
                default:
            }
        }
        req.setTypes(types);
        req.setParams(params);
        return req;
    }).create();

    @PostMapping("/execute")
    public RspInfo<Object> execute(@RequestBody String jsonStr) {
        ReqInfo req = parse(jsonStr);
        String license = req.getLicense();
        String trsId = req.getTrsId();
        int action = req.getAction();
        String group = req.getGroup();
        String sql = req.getSql();
        List<Object> params = req.getParams();
        switch (action) {
            case GlowType.READ:
                return RspInfo.success(glowSqlExecutor.query(group, sql, params));
            case GlowType.WRITE:
                if (StringUtils.isEmpty(trsId)) {
                    return RspInfo.success(glowSqlExecutor.update(group, sql, params));
                }
                return RspInfo.success(glowSqlExecutor.update(trsId, group, sql, params));
            case GlowType.ROLLBACK:
                glowTransactionManager.rollbackOrCommit(trsId, GlowTransactionManager.ROLLBACK);
                return RspInfo.success(null);
            case GlowType.COMMIT:
                glowTransactionManager.rollbackOrCommit(trsId, GlowTransactionManager.COMMIT);
                return RspInfo.success(null);
            default:
                throw new GlowSqlException("action不正确：" + action);
        }
    }

    private ReqInfo parse(String jsonStr) {

        return null;
    }
}












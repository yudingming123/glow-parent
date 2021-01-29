package com.jimei.glow.common.core.transaction;

import com.jimei.glow.common.core.exception.GlowSqlException;
import lombok.Data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author yudm
 * @Date 2020/12/26 20:11
 * @Desc
 */
@Data
public class GlowTransactionManager {
    public static final int ROLLBACK = 1;
    public static final int COMMIT = 2;
    public final long TRS_WAIT;
    //map<id,<group,<cn,ds>>>>
    private final Map<String, Map<String, Map<Connection, DataSource>>> tiGpCnDs = new HashMap<>();
    private final Map<String, Long> tiMs = new HashMap<>();

    public GlowTransactionManager(long period) {
        if (period < 10000) {
            this.TRS_WAIT = 20 * 1000;
        } else {
            this.TRS_WAIT = period;
        }
        // 定时回滚并清理过期的事务
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            long time = System.currentTimeMillis();
            for (Map.Entry<String, Long> entry : tiMs.entrySet()) {
                if (time - entry.getValue() > TRS_WAIT) {
                    rollbackOrCommit(entry.getKey(), ROLLBACK);
                }
            }

        }, 20, this.TRS_WAIT, TimeUnit.MILLISECONDS);
    }


    public Map<Connection, DataSource> getCnDsMap(String trsId, String group) {
        Map<String, Map<Connection, DataSource>> gCnDs = tiGpCnDs.get(trsId);
        if (null == gCnDs || gCnDs.size() < 1) {
            tiGpCnDs.remove(trsId);
            return null;
        }
        Map<Connection, DataSource> cnDs = gCnDs.get(group);
        if (null == cnDs || cnDs.size() < 1) {
            gCnDs.remove(group);
            return null;
        }
        //刷新事务对应的时间
        tiMs.replace(trsId, System.currentTimeMillis());
        return cnDs;
    }

    public synchronized void addTransaction(String trsId, String group, Map<Connection, DataSource> cnDsMap) {
        if (!tiGpCnDs.containsKey(trsId)) {
            Map<String, Map<Connection, DataSource>> map = new HashMap<>(1);
            map.put(group, cnDsMap);
            tiGpCnDs.put(trsId, map);
        } else {
            tiGpCnDs.get(trsId).put(group, cnDsMap);
        }
        //设置事务对应的开始时间
        tiMs.put(trsId, System.currentTimeMillis());
    }

    public synchronized void rollbackOrCommit(String trsId, int opr) {
        Map<String, Map<Connection, DataSource>> groupCnDsMap = tiGpCnDs.get(trsId);
        if (null == groupCnDsMap || groupCnDsMap.size() < 1) {
            tiGpCnDs.remove(trsId);
            return;
        }
        for (Map.Entry<String, Map<Connection, DataSource>> gcd : groupCnDsMap.entrySet()) {
            Map<Connection, DataSource> cnDsMap = gcd.getValue();
            if (null == cnDsMap || cnDsMap.size() < 1) {
                continue;
            }
            for (Map.Entry<Connection, DataSource> entry : cnDsMap.entrySet()) {
                try {
                    if (ROLLBACK == opr) {
                        entry.getKey().rollback();
                    } else if (COMMIT == opr) {
                        entry.getKey().commit();
                    } else {
                        throw new GlowSqlException("opr的值不正确：" + opr);
                    }
                } catch (Throwable t) {
                    throw new GlowSqlException(t);
                }
                close(entry.getKey(), entry.getValue());
            }
        }
        tiGpCnDs.remove(trsId);
    }

    private void close(Connection cn, DataSource ds) {
        try {
            cn.close();
        } catch (SQLException t) {
            throw new GlowSqlException(t);
        }
    }
}

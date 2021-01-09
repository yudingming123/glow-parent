package com.jimei.glow.client.core;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author yudm
 * @Date 2020/12/26 20:11
 * @Desc
 */
@Component
@Data
public class GlowTransactionManager {
    public static final int ROLLBACK = 1;
    public static final int COMMIT = 2;
    //map<id,<group,<cn,ds>>>>
    private final Map<String, Map<String, Map<Connection, DataSource>>> idGroupCnDsMap = new HashMap<>();

    public Map<Connection, DataSource> getCnDsMap(String trsId, String group) {
        return Optional.ofNullable(idGroupCnDsMap.get(trsId)).map(v -> v.get(group)).orElse(null);
    }

    public synchronized void addTransaction(String trsId, String group, Map<Connection, DataSource> cnDsMap) {
        if (!idGroupCnDsMap.containsKey(trsId)) {
            Map<String, Map<Connection, DataSource>> map = new HashMap<>(1);
            map.put(group, cnDsMap);
            idGroupCnDsMap.put(trsId, map);
        } else {
            idGroupCnDsMap.get(trsId).put(group, cnDsMap);
        }
    }

    public synchronized void rollbackOrCommit(String trsId, int opr) {
        Map<String, Map<Connection, DataSource>> groupCnDsMap = idGroupCnDsMap.get(trsId);
        if (null == groupCnDsMap || groupCnDsMap.size() < 1) {
            idGroupCnDsMap.remove(trsId);
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
        idGroupCnDsMap.remove(trsId);
    }

    private void close(Connection cn, DataSource ds) {
        try {
            cn.close();
        } catch (SQLException t) {
            throw new GlowSqlException(t);
        }
    }
}

package com.jimei.silence.core.sql;

import com.google.common.base.CaseFormat;
import com.jimei.glow.common.core.datasource.SilenceDataSourceUtil;
import com.jimei.glow.common.core.exception.SqlException;
import com.jimei.glow.common.core.sql.ISqlExecutor;
import com.jimei.glow.common.core.sql.SilenceConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @Author yudm
 * @Date 2021/5/13 17:40
 * @Desc
 */
@Component
public class SilenceSqlExecutor implements ISqlExecutor {

    @Resource
    private SilenceConfig silenceConfig;

    @Override
    public int update(String pack, String sql, List<Object> values) {
        Map<Connection, DataSource> cnDsMap = SilenceDataSourceUtil.getCnDsMap(silenceConfig.getGroup(pack));
        Collection<Connection> cns = cnDsMap.keySet();
        try {
            int res = doUpdate(cns, sql, values);
            commit(cns);
            return res;
        } catch (SQLException e) {
            rollback(cns);
            throw new SqlException(e);
        } finally {
            //不由事务管理，需要手动释放cn
            SilenceDataSourceUtil.release(cnDsMap);
        }
    }

    @Override
    public int saveBatch(String pack, String sql, List<List<Object>> values) {
        return 0;
    }

    @Override
    public <T> List<T> query(String pack, String sql, List<Object> values, Class<T> clazz) {
        return null;
    }

    @Override
    public List<Map<String, Object>> query(String pack, String sql, List<Integer> types, List<Object> values) {
        return null;
    }

    private int doUpdate(Collection<Connection> cns, String sql, List<Object> values) throws SQLException {
        PreparedStatement pst = null;
        try {
            int res = 0;
            for (Connection cn : cns) {
                pst = cn.prepareStatement(sql);
                //将属性值设置到sql中的占位符中
                fillPst(pst, values);
                res = pst.executeUpdate();
            }
            return res;
        } finally {
            //释放pst
            close(null, pst, null);
        }
    }

    private void commit(Collection<Connection> cns) {
        for (Connection cn : cns) {
            try {
                cn.commit();
            } catch (SQLException e) {
                throw new SqlException(e);
            }
        }
    }

    private void rollback(Collection<Connection> cns) {
        for (Connection cn : cns) {
            try {
                cn.rollback();
            } catch (SQLException e) {
                throw new SqlException(e);
            }
        }
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:48
     * @Param [statement, values]
     * @Desc 向sql的占位符中填充值
     */
    private static void fillPst(PreparedStatement pst, List<Object> values) throws SQLException {
        if (null != values && values.size() > 0) {
            for (int i = 0; i < values.size(); ++i) {
                pst.setObject(i + 1, values.get(i));
            }
        }
    }

    /**
     * @Author yudm
     * @Date 2020/10/4 12:33
     * @Param [clazz, resultSet]
     * @Desc 将ResultSet转化成对应的实体类集合
     */
    private List<Map<String, Object>> parsRs(ResultSet rs) {
        if (null == rs) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        try {

            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            //跳过表头
            while (rs.next()) {
                for (int i = 1; i <= count; ++i) {
                    //获取表中字段的名字并转为小写
                    String colName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, md.getColumnName(i));
                    map.put(colName, rs.getObject(i));
                }
                list.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * @Author yudm
     * @Date 2020/9/25 15:47
     * @Param [connection, statement]
     * @Desc 关闭资源, 当Connection是从连接池中来的时候，必须要关闭，传null。
     */
    private void close(Connection cn, Statement st, ResultSet rs) {
        try {
            if (null != cn) {
                cn.close();
            }
            if (null != st) {
                st.close();
            }
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

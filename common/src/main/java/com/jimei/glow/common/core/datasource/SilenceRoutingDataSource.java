package com.jimei.glow.common.core.datasource;

import com.jimei.glow.common.core.exception.SqlException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SilenceRoutingDataSource extends SilenceDataSource {
    //所有连接池组
    private final Map<String, List<DataSource>> groupDataSources;

    //负载均衡计数器组
    private final Map<String, Indexer> groupIndexer;

    public SilenceRoutingDataSource(Map<String, List<DataSource>> groupDataSources) {
        if (null == groupDataSources || groupDataSources.size() < 1) {
            throw new SqlException("参数groupDataSources不能为空");
        }
        this.groupDataSources = groupDataSources;
        Map<String, Indexer> map = new HashMap<>();
        for (Map.Entry<String, List<DataSource>> entry : groupDataSources.entrySet()) {
            map.put(entry.getKey(), new Indexer(entry.getValue().size(), 0));
        }
        this.groupIndexer = map;
    }

    /**
     * @Author yudm
     * @Date 2020/12/25 14:40
     * @Param [group]
     * @Desc 获取group对应连接池组中一个经过负载均衡的连接池
     **/
    public DataSource getDataSource(String group) {
        return getGroupDataSource(group).get(groupIndexer.get(group).getAndAdd());
    }

    /**
     * @Author yudm
     * @Date 2020/12/25 15:06
     * @Param [group]
     * @Desc 获取group对应的连接池组
     **/
    public List<DataSource> getGroupDataSource(String group) {
        if (null == groupDataSources || groupDataSources.size() < 1) {
            throw new SqlException("连接池:groupDataSources未被初始化");
        }
        List<DataSource> dataSources = groupDataSources.get(group);
        if (null == dataSources || dataSources.size() < 1) {
            throw new SqlException("找不到" + group + "对应的连接池组");
        }
        return dataSources;
    }

    /**
     * @Author yudm
     * @Date 2020/12/25 15:07
     * @Param []
     * @Desc 获取所有连接池组
     **/
    public Map<String, List<DataSource>> getGroupDataSources() {
        return groupDataSources;
    }

    /**
     * @Author yudm
     * @Date 2020/12/25 17:38
     * @Param [group, dataSource]
     * @Desc 用于动态添加数据源
     **/
    public synchronized void addDataSource(String group, DataSource dataSource) {
        if (null == group || group.trim().isEmpty() || null == dataSource) {
            throw new SqlException("新建数据源时，group和dataSource不能为空");
        }
        //如该组已经存在，则直接添加到组里面，否则新建组
        if (groupDataSources.containsKey(group)) {
            groupDataSources.get(group).add(dataSource);
            //刷新负载均衡计数器组
            groupIndexer.get(group).setTotal(groupDataSources.get(group).size());
        } else {
            List<DataSource> list = new ArrayList<>();
            list.add(dataSource);
            groupDataSources.put(group, list);
            //新建负载均衡计数器组
            groupIndexer.put(group, new Indexer(1, 0));
        }
    }
}

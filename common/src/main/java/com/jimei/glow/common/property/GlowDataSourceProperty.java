package com.jimei.glow.common.property;

import lombok.Data;

import java.util.Map;


/**
 * @Author yudm
 * @Date 2020/12/12 11:00
 * @Desc
 */
@Data
public class GlowDataSourceProperty {
    /**
     * hikari连接池的配置
     */
    private HikariProperty hikari;

    /**
     * 分组数据源配置
     */
    private Map<String, GroupProperty> group;
}
